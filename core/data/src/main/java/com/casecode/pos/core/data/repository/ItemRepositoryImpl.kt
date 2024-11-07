/*
 * Designed and developed 2024 by Mahmood Abdalhafeez
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.casecode.pos.core.data.repository

import com.casecode.pos.core.common.AppDispatchers.IO
import com.casecode.pos.core.common.Dispatcher
import com.casecode.pos.core.data.R
import com.casecode.pos.core.data.model.asDomainModel
import com.casecode.pos.core.data.model.asExternalMapper
import com.casecode.pos.core.data.utils.ensureUserExists
import com.casecode.pos.core.domain.repository.AddItem
import com.casecode.pos.core.domain.repository.AuthRepository
import com.casecode.pos.core.domain.repository.DeleteItem
import com.casecode.pos.core.domain.repository.ItemRepository
import com.casecode.pos.core.domain.repository.UpdateItem
import com.casecode.pos.core.domain.repository.UpdateQuantityItems
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.firebase.services.FirestoreService
import com.casecode.pos.core.firebase.services.ITEMS_COLLECTION_PATH
import com.casecode.pos.core.firebase.services.ITEM_DELETED_FIELD
import com.casecode.pos.core.firebase.services.ITEM_NAME_FIELD
import com.casecode.pos.core.firebase.services.ITEM_QUANTITY_FIELD
import com.casecode.pos.core.firebase.services.SetOptions
import com.casecode.pos.core.firebase.services.USERS_COLLECTION_PATH
import com.casecode.pos.core.firebase.services.model.ItemDataModel
import com.casecode.pos.core.model.data.users.Item
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
 * @property ioDispatcher Coroutine dispatcher for performing operations asynchronously on IO-bound threads.
 * @constructor Creates an [ItemRepositoryImpl] with the provided [FirestoreService] and [ioDispatcher].
 */
class ItemRepositoryImpl
@Inject
constructor(
    private val db: FirestoreService,
    private val auth: AuthRepository,
    @Dispatcher(IO) val ioDispatcher: CoroutineDispatcher,
) : ItemRepository {
    override fun getItems(): Flow<Resource<List<Item>>> = flow<Resource<List<Item>>> {
        emit(Resource.Loading)
        delay(300)
        val uid = auth.currentUserId()
        Timber.e("uid: $uid")
        auth.ensureUserExists<List<Item>> {
            emit(it)
            return@flow
        }
        db
            .listenToCollectionChild(
                collection = USERS_COLLECTION_PATH,
                documentId = uid,
                collectionChild = ITEMS_COLLECTION_PATH,
                condition = ITEM_DELETED_FIELD to false,
                sortWithFieldName = ITEM_NAME_FIELD,
            ).collect { snapshot ->
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
                auth.ensureUserExists<Int> {
                    return@withContext it
                }
                val currentUserId =
                    auth.currentUserId()
                suspendCoroutine { continuation ->
                    val itemMap = item.asExternalMapper()
                    val sku = item.sku
                    db
                        .getOrAddDocumentInChild(
                            USERS_COLLECTION_PATH,
                            currentUserId,
                            ITEMS_COLLECTION_PATH,
                            sku,
                        ).set(itemMap)
                        .addOnSuccessListener {
                            continuation.resume(
                                Resource.Success(R.string.core_data_item_added_successfully),
                            )
                        }.addOnFailureListener { failure ->
                            Timber.e(failure)
                            continuation.resume(
                                Resource.error(R.string.core_data_add_item_failure_generic),
                            )
                        }
                }
            } catch (_: UnknownHostException) {
                Resource.error(R.string.core_data_add_item_failure_network)
            } catch (e: Exception) {
                Timber.e(e)
                Resource.error(R.string.core_data_add_item_failure_generic)
            }
        }
    }

    override suspend fun updateItem(item: Item): UpdateItem {
        return withContext(ioDispatcher) {
            try {
                auth.ensureUserExists<Int> {
                    return@withContext it
                }
                val currentUserUid =
                    auth.currentUserId()
                suspendCoroutine { continuation ->
                    val itemMap = item.asExternalMapper()
                    val sku = item.sku
                    db
                        .getOrAddDocumentInChild(
                            USERS_COLLECTION_PATH,
                            currentUserUid,
                            ITEMS_COLLECTION_PATH,
                            sku,
                        ).set(
                            itemMap,
                            SetOptions.merge(),
                        ).addOnSuccessListener {
                            continuation.resume(
                                Resource.Success(R.string.core_data_item_updated_successfully),
                            )
                        }.addOnFailureListener { failure ->
                            Timber.e(failure)

                            continuation.resume(
                                Resource.error(R.string.core_data_update_item_failure_generic),
                            )
                        }
                }
            } catch (_: UnknownHostException) {
                Resource.error(R.string.core_data_update_item_failure_network)
            } catch (e: Exception) {
                Timber.e(e)
                Resource.error(R.string.core_data_update_item_failure_generic)
            }
        }
    }

    override suspend fun updateQuantityInItems(items: List<Item>): UpdateQuantityItems {
        return withContext(ioDispatcher) {
            try {
                auth.ensureUserExists<List<Item>> {
                    return@withContext it
                }
                val currentUserUid =
                    auth.currentUserId()
                suspendCoroutine { continuation ->
                    val batch = db.batch()
                    val collectionRef =
                        db.getCollectionChild(
                            USERS_COLLECTION_PATH,
                            currentUserUid,
                            ITEMS_COLLECTION_PATH,
                        )
                    for (item in items) {
                        if (!item.isTrackStock()) continue
                        val itemRef = collectionRef.document(item.sku)
                        batch.update(
                            itemRef,
                            ITEM_QUANTITY_FIELD,
                            com.casecode.pos.core.firebase.services.FieldValue
                                .increment(-item.quantity.toDouble()),
                        )
                    }

                    batch
                        .commit()
                        .addOnSuccessListener {
                            continuation.resume(Resource.Success(items))
                        }.addOnFailureListener {
                            Timber.e(it)
                            continuation.resume(
                                Resource.error(R.string.core_data_update_item_failure_generic),
                            )
                        }
                }
            } catch (_: UnknownHostException) {
                Resource.error(R.string.core_data_update_item_failure_network)
            } catch (e: Exception) {
                Timber.e(e)
                Resource.error(R.string.core_data_update_item_failure_generic)
            }
        }
    }

    override suspend fun deleteItem(item: Item): DeleteItem {
        return withContext(ioDispatcher) {
            try {
                auth.ensureUserExists<Int> {
                    return@withContext it
                }
                val currentUserId =
                    auth.currentUserId()
                suspendCoroutine { continuation ->
                    db
                        .getOrAddDocumentInChild(
                            USERS_COLLECTION_PATH,
                            currentUserId,
                            ITEMS_COLLECTION_PATH,
                            item.sku,
                        ).update(mapOf(ITEM_DELETED_FIELD to true))
                        .addOnSuccessListener {
                            continuation.resume(
                                Resource.success(R.string.core_data_item_deleted_successfully),
                            )
                        }.addOnFailureListener { failure ->
                            Timber.e(failure)
                            continuation.resume(
                                Resource.error(R.string.core_data_delete_item_failure_generic),
                            )
                        }
                }
            } catch (_: UnknownHostException) {
                Resource.error(R.string.core_data_delete_item_failure_network)
            } catch (e: Exception) {
                Timber.e(e)
                Resource.error(R.string.core_data_delete_item_failure_generic)
            }
        }
    }
}