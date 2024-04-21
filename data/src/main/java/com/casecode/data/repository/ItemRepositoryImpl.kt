package com.casecode.data.repository

import com.casecode.data.mapper.asExternalModel
import com.casecode.data.utils.AppDispatchers
import com.casecode.data.utils.Dispatcher
import com.casecode.domain.model.users.Item
import com.casecode.domain.repository.AddItem
import com.casecode.domain.repository.AuthService
import com.casecode.domain.repository.DeleteItem
import com.casecode.domain.repository.ItemRepository
import com.casecode.domain.repository.ResourceItems
import com.casecode.domain.repository.UpdateItem
import com.casecode.domain.utils.ITEMS_COLLECTION_PATH
import com.casecode.domain.utils.ITEM_NAME_FIELD
import com.casecode.domain.utils.Resource
import com.casecode.domain.utils.USERS_COLLECTION_PATH
import com.casecode.pos.data.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenSource
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.SnapshotListenOptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.UnknownHostException
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Implementation of the [ItemRepository] interface for handling item-related operations using Firestore.
 *
 * @property firestore Reference to Firestore for storing and retrieving items.
 * @property ioDispatcher Coroutine dispatcher for performing operations asynchronously on IO-bound threads.
 * @constructor Creates an [ItemRepositoryImpl] with the provided [firestore] and [ioDispatcher].
 */
class ItemRepositoryImpl
@Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    @Dispatcher(AppDispatchers.IO) val ioDispatcher: CoroutineDispatcher,
) : ItemRepository, AuthService {
    // ISSUE: improve minimize cost of use server with use offline cache to get document from
    private val optionsCache by lazy {
        SnapshotListenOptions.Builder().setMetadataChanges(MetadataChanges.INCLUDE)
            .setSource(ListenSource.CACHE).build()
    }
    override val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()

    override val currentUser: Flow<FirebaseUser?>
        get() = callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { auth -> this.trySend(auth.currentUser) }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }.flowOn(ioDispatcher)

    override fun getItems(): Flow<ResourceItems> = callbackFlow<Resource<List<Item>>> {
        trySend(Resource.Loading)

        val listenerRegistration = getItemCollectionRef(currentUserId).orderBy(ITEM_NAME_FIELD)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->

                if (error != null) {
                    Timber.e(error)
                    trySend(Resource.error(R.string.error_fetching_items))
                    close()
                }

                // Trace for where data getting form local db or server or cache memory.
                val source = if (snapshot != null && snapshot.metadata.hasPendingWrites()) {
                    LOCAL_DB
                } else if (snapshot != null && snapshot.metadata.isFromCache) {
                    LOCAL_CACHE
                } else {
                    SERVER_DB
                }
                // Handle the snapshot
                val itemMutableList = mutableListOf<Item>()
                with(itemMutableList) {
                    snapshot?.documents?.mapNotNull { document ->
                        Timber.i("$source , Item:  ${document.data}")
                        document.toObject(Item::class.java)?.let { add(it) }
                    }
                }

                if (itemMutableList.isEmpty()) {
                    trySend(Resource.empty())
                } else {
                    trySend(Resource.success(itemMutableList))
                }
            }

        // Close the listener when the flow is cancelled
        awaitClose {
            listenerRegistration.remove()
        }
    }.flowOn(ioDispatcher)


    override suspend fun addItem(item: Item): AddItem {
        return withContext(ioDispatcher) {
            try {
                suspendCoroutine { continuation ->
                    val itemMap = item.asExternalModel()
                    if (currentUserId.isBlank()) {
                        continuation.resume(Resource.error(com.casecode.pos.domain.R.string.uid_empty))
                        return@suspendCoroutine
                    }
                    val sku = item.sku
                    getItemDocumentRef(uid = currentUserId, sku = sku).set(itemMap)
                        .addOnSuccessListener {
                            continuation.resume(Resource.Success(R.string.item_added_successfully))
                        }.addOnFailureListener { failure ->
                            Timber.e(failure)
                            continuation.resume(Resource.error(R.string.add_item_failure_generic))
                        }
                }
            } catch (e: UnknownHostException) {
                Resource.error(R.string.add_item_failure_network)
            } catch (e: Exception) {
                Timber.e(e)
                Resource.error(R.string.add_item_failure_generic)
            }
        }
    }

    override suspend fun updateItem(item: Item): UpdateItem {
        return withContext(ioDispatcher) {
            try {

                suspendCoroutine { continuation ->
                    if (currentUserId.isBlank()) {
                        continuation.resume(Resource.error(com.casecode.pos.domain.R.string.uid_empty))
                        return@suspendCoroutine
                    }
                    val itemMap = item.asExternalModel()
                    val sku = item.sku
                    getItemDocumentRef(uid = currentUserId, sku = sku).set(
                        itemMap,
                        SetOptions.merge(),
                    ).addOnSuccessListener {
                        continuation.resume(Resource.Success(R.string.item_updated_successfully))
                    }.addOnFailureListener { failure ->
                        Timber.e(failure)

                        continuation.resume(Resource.error(R.string.update_item_failure_generic))
                    }
                }
            } catch (e: UnknownHostException) {
                Resource.error(R.string.update_item_failure_network)
            } catch (e: Exception) {
                Timber.e(e)
                Resource.error(R.string.update_item_failure_generic)
            }
        }
    }

    override suspend fun deleteItem(item: Item): DeleteItem {
        return withContext(ioDispatcher) {
            try {
                suspendCoroutine { continuation ->
                    if (currentUserId.isBlank()) {
                        continuation.resume(Resource.error(com.casecode.pos.domain.R.string.uid_empty))
                        return@suspendCoroutine
                    }
                    getItemDocumentRef(uid = currentUserId, sku = item.sku).delete()
                        .addOnSuccessListener {
                            continuation.resume(Resource.success(R.string.item_deleted_successfully))
                        }.addOnFailureListener { failure ->
                            Timber.e(failure)
                            continuation.resume(Resource.error(R.string.delete_item_failure_generic))
                        }
                }

            } catch (e: UnknownHostException) {
                Resource.error(R.string.delete_item_failure_network)
            } catch (e: Exception) {
                Timber.e(e)
                Resource.error(R.string.delete_item_failure_generic)
            }
        }
    }

    /**
     * Retrieves a reference to the Firestore document corresponding to the given user ID and SKU.
     *
     * @param uid The user ID.
     * @param sku The SKU (stock keeping unit) of the item.
     * @return The Firestore document reference.
     */
    private fun getItemDocumentRef(
        uid: String,
        sku: String,
    ): DocumentReference {
        val itemsCollection = firestore.collection(USERS_COLLECTION_PATH).document(uid)
            .collection(ITEMS_COLLECTION_PATH)
        return itemsCollection.document(sku)
    }

    /**
     * Retrieves a reference to the Firestore collection of items for the given user ID.
     *
     * @param uid The user ID.
     * @return The Firestore collection reference.
     */
    private fun getItemCollectionRef(uid: String): CollectionReference =
        firestore.collection(USERS_COLLECTION_PATH).document(uid).collection(ITEMS_COLLECTION_PATH)

    companion object {
        private const val LOCAL_CACHE = "local_cache"
        private const val LOCAL_DB = "Local_DB"
        private const val SERVER_DB = "Server_DB"
    }
}