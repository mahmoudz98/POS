package com.casecode.pos.core.data.repository

import com.casecode.pos.core.common.AppDispatchers.IO
import com.casecode.pos.core.common.Dispatcher
import com.casecode.pos.core.data.R
import com.casecode.pos.core.data.model.ItemDataModel
import com.casecode.pos.core.data.model.asDomainModel
import com.casecode.pos.core.data.model.asExternalMapper
import com.casecode.pos.core.data.service.AuthService
import com.casecode.pos.core.data.service.checkUserNotFound
import com.casecode.pos.core.data.service.trace
import com.casecode.pos.core.data.utils.ITEMS_COLLECTION_PATH
import com.casecode.pos.core.data.utils.ITEM_NAME_FIELD
import com.casecode.pos.core.data.utils.ITEM_QUANTITY_FIELD
import com.casecode.pos.core.data.utils.getCollectionRefFromUser
import com.casecode.pos.core.data.utils.getDocumentFromUser
import com.casecode.pos.core.domain.repository.AddItem
import com.casecode.pos.core.domain.repository.DeleteItem
import com.casecode.pos.core.domain.repository.ItemRepository
import com.casecode.pos.core.domain.repository.UpdateItem
import com.casecode.pos.core.domain.repository.UpdateQuantityItems
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Item
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenSource
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.SnapshotListenOptions
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
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
    @Inject
    constructor(
        private val firestore: FirebaseFirestore,
        private val authService: AuthService,
        @Dispatcher(IO) val ioDispatcher: CoroutineDispatcher,
    ) : ItemRepository {
        // TODO: improve minimize cost of use server with use offline cache to get document from
        private val optionsCache by lazy {
            SnapshotListenOptions
                .Builder()
                .setMetadataChanges(MetadataChanges.INCLUDE)
                .setSource(ListenSource.CACHE)
            .build()
    }

    override fun getItems(): Flow<Resource<List<Item>>> =
        flow<Resource<List<Item>>> {
            emit(Resource.Loading)
            delay(300)
            val uid = authService.currentUserId()
            Timber.e("uid: $uid")
            authService.checkUserNotFound<List<Item>> {
                emit(it)
                return@flow
            }

            firestore
                .getCollectionRefFromUser(uid, ITEMS_COLLECTION_PATH)
                .orderBy(ITEM_NAME_FIELD)
                .snapshots()
                .collect { snapshot ->
                    if (snapshot.metadata.hasPendingWrites()) {
                        Timber.i("Data from LOCAL_DB")
                    } else if (snapshot.metadata.isFromCache) {
                        Timber.i("Data from LOCAL_CACHE")
                    } else {
                        Timber.i("Data from SERVER_DB")
                    }

                    val itemMutableList = mutableListOf<Item>()
                    snapshot.documents.mapNotNull { document ->
                        document
                            .toObject(ItemDataModel::class.java)
                            ?.let { itemMutableList.add(it.asDomainModel()) }
                    }

                    if (itemMutableList.isEmpty()) {
                        emit(Resource.empty())
                    } else {
                        emit(Resource.success(itemMutableList))
                    }
                }
        }.catch { e ->
            Timber.e(e)
            emit(Resource.error(R.string.core_data_error_fetching_items))
        }.flowOn(ioDispatcher)

    override suspend fun addItem(item: Item): AddItem {
        return withContext(ioDispatcher) {
            try {
                authService.checkUserNotFound<Int> {
                    return@withContext it
                }
                val currentUserId = authService.currentUserId()
                suspendCoroutine { continuation ->
                    val itemMap = item.asExternalMapper()

                    val sku = item.sku
                    firestore
                        .getDocumentFromUser(
                            currentUserId,
                            ITEMS_COLLECTION_PATH,
                            sku,
                        ).set(itemMap)
                        .addOnSuccessListener {
                            continuation.resume(Resource.Success(R.string.core_data_item_added_successfully))
                        }.addOnFailureListener { failure ->
                            Timber.e(failure)
                            continuation.resume(Resource.error(R.string.core_data_add_item_failure_generic))
                        }
                }
            } catch (e: UnknownHostException) {
                Resource.error(R.string.core_data_add_item_failure_network)
            } catch (e: Exception) {
                Timber.e(e)
                Resource.error(R.string.core_data_add_item_failure_generic)
            }
        }
    }

    override suspend fun updateItem(item: Item): UpdateItem {
        return withContext(ioDispatcher) {
            trace(UPDATE_ITEM_TRACE) {
                try {
                    authService.checkUserNotFound<Int> {
                        return@withContext it
                    }
                    val currentUserUid = authService.currentUserId()
                    suspendCoroutine { continuation ->

                        val itemMap = item.asExternalMapper()
                        val sku = item.sku
                        firestore
                            .getDocumentFromUser(
                                currentUserUid,
                                ITEMS_COLLECTION_PATH,
                                sku,
                            ).set(
                                itemMap,
                                SetOptions.merge(),
                            ).addOnSuccessListener {
                                continuation.resume(Resource.Success(R.string.core_data_item_updated_successfully))
                            }.addOnFailureListener { failure ->
                                Timber.e(failure)

                                continuation.resume(Resource.error(R.string.core_data_update_item_failure_generic))
                            }
                    }
                } catch (e: UnknownHostException) {
                    Resource.error(R.string.core_data_update_item_failure_network)
                } catch (e: Exception) {
                    Timber.e(e)
                    Resource.error(R.string.core_data_update_item_failure_generic)
                }
            }
        }
    }

    override suspend fun updateQuantityInItems(items: List<Item>): UpdateQuantityItems {
        return withContext(ioDispatcher) {
            trace(UPDATE_QUANTITY_ITEM_TRACE) {
                try {
                    authService.checkUserNotFound<List<Item>> {
                        return@withContext it
                    }
                    val currentUserUid = authService.currentUserId()
                    suspendCoroutine { continuation ->

                        val batch = firestore.batch()
                        val collectionRef =
                            firestore.getCollectionRefFromUser(
                                currentUserUid,
                                ITEMS_COLLECTION_PATH,
                            )

                        items.forEach {
                            val itemRef = collectionRef.document(it.sku)
                            batch.update(
                                itemRef,
                                ITEM_QUANTITY_FIELD,
                                FieldValue.increment(-it.quantity),
                            )
                        }
                        batch
                            .commit()
                            .addOnSuccessListener {
                                continuation.resume(Resource.Success(items))
                            }.addOnFailureListener {
                                Timber.e(it)
                                continuation.resume(Resource.error(R.string.core_data_update_item_failure_generic))
                            }
                    }
                } catch (e: UnknownHostException) {
                    Resource.error(R.string.core_data_update_item_failure_network)
                } catch (e: Exception) {
                    Timber.e(e)
                    Resource.error(R.string.core_data_update_item_failure_generic)
                }
            }
        }
    }

    override suspend fun deleteItem(item: Item): DeleteItem {
        return withContext(ioDispatcher) {
            try {
                authService.checkUserNotFound<Int> {
                    return@withContext it
                }
                val currentUserId = authService.currentUserId()
                suspendCoroutine { continuation ->

                    firestore
                        .getDocumentFromUser(
                            currentUserId,
                            ITEMS_COLLECTION_PATH,
                            item.sku,
                        ).delete()
                        .addOnSuccessListener {
                            continuation.resume(Resource.success(R.string.core_data_item_deleted_successfully))
                        }.addOnFailureListener { failure ->
                            Timber.e(failure)
                            continuation.resume(Resource.error(R.string.core_data_delete_item_failure_generic))
                        }
                }
            } catch (e: UnknownHostException) {
                Resource.error(R.string.core_data_delete_item_failure_network)
            } catch (e: Exception) {
                Timber.e(e)
                Resource.error(R.string.core_data_delete_item_failure_generic)
            }
        }
    }

    companion object {
        private const val LOCAL_CACHE = "local_cache"
        private const val LOCAL_DB = "Local_DB"
        private const val SERVER_DB = "Server_DB"
        private const val UPDATE_ITEM_TRACE = "updateItem"
        private const val UPDATE_QUANTITY_ITEM_TRACE = "QuantityUpdateItem"
    }
}