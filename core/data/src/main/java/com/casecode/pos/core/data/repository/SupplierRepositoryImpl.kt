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
import com.casecode.pos.core.data.utils.ensureUserExists
import com.casecode.pos.core.data.utils.ensureUserExistsOrReturnError
import com.casecode.pos.core.domain.repository.AuthRepository
import com.casecode.pos.core.domain.repository.SupplierRepository
import com.casecode.pos.core.domain.utils.OperationResult
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.firebase.services.FirestoreService
import com.casecode.pos.core.firebase.services.SUPPLIERS_COLLECTION_PATH
import com.casecode.pos.core.firebase.services.SUPPLIER_ADDRESS_FIELD
import com.casecode.pos.core.firebase.services.SUPPLIER_CATEGORY_FIELD
import com.casecode.pos.core.firebase.services.SUPPLIER_COMPANY_NAME_FIELD
import com.casecode.pos.core.firebase.services.SUPPLIER_CONTACT_EMAIL_FIELD
import com.casecode.pos.core.firebase.services.SUPPLIER_CONTACT_NAME_FIELD
import com.casecode.pos.core.firebase.services.SUPPLIER_CONTACT_PHONE_FIELD
import com.casecode.pos.core.firebase.services.SUPPLIER_ID_FIELD
import com.casecode.pos.core.firebase.services.USERS_COLLECTION_PATH
import com.casecode.pos.core.model.data.users.Supplier
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.UnknownHostException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SupplierRepositoryImpl
@Inject constructor(
    private val db: FirestoreService,
    private val auth: AuthRepository,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : SupplierRepository {
    override fun getSuppliers(): Flow<Resource<List<Supplier>>> = flow {
        emit(Resource.Loading)
        auth.ensureUserExistsOrReturnError<List<Supplier>> {
            emit(it)
            return@flow
        }
        val uid = auth.currentUserId()
        db.listenToCollectionChild(
            collection = USERS_COLLECTION_PATH,
            documentId = uid,
            collectionChild = SUPPLIERS_COLLECTION_PATH,
        ).collect { snapshot ->
            val suppliers = mutableListOf<Supplier>()
            snapshot.documents.mapNotNull { document ->
                document.toObject(Supplier::class.java)?.let { supplier ->
                    suppliers.add(supplier)
                }
            }
            if (suppliers.isNotEmpty()) {
                emit(Resource.Success(suppliers))
            } else {
                emit(Resource.empty())
            }
        }
    }.catch { e ->
        Timber.e(e)
        emit(Resource.Error(R.string.core_data_error_fetching_suppliers))
    }.flowOn(ioDispatcher)

    override suspend fun addSupplier(supplier: Supplier): OperationResult {
        return withContext(ioDispatcher) {
            try {
                auth.ensureUserExists { message ->
                    return@withContext OperationResult.Failure(message)
                }
                val uid = auth.currentUserId()
                suspendCoroutine<OperationResult> { continuation ->
                    val doc =
                        db.getDocumentInChild(
                            USERS_COLLECTION_PATH,
                            uid,
                            SUPPLIERS_COLLECTION_PATH,
                        )
                    doc.set(
                        mapOf(
                            SUPPLIER_ID_FIELD to doc.id,
                            SUPPLIER_COMPANY_NAME_FIELD to supplier.companyName,
                            SUPPLIER_CONTACT_NAME_FIELD to supplier.contactName,
                            SUPPLIER_CONTACT_EMAIL_FIELD to supplier.contactEmail,
                            SUPPLIER_CONTACT_PHONE_FIELD to supplier.contactPhone,
                            SUPPLIER_ADDRESS_FIELD to supplier.address,
                            SUPPLIER_CATEGORY_FIELD to supplier.category,
                        ),
                    ).addOnSuccessListener {
                        continuation.resume(OperationResult.Success)
                    }.addOnFailureListener {
                        continuation.resume(OperationResult.Failure(R.string.core_data_add_supplier_failure_generic))
                    }
                }
            } catch (_: UnknownHostException) {
                OperationResult.Failure(R.string.core_data_add_supplier_failure_network)
            } catch (e: Exception) {
                Timber.e(e)
                OperationResult.Failure(R.string.core_data_add_supplier_failure_generic)
            }
        }
    }

    override suspend fun updateSupplier(
        oldSupplier: Supplier,
        newSupplier: Supplier,
    ): OperationResult {
        return withContext(ioDispatcher) {
            try {
                auth.ensureUserExists { message ->
                    return@withContext OperationResult.Failure(message)
                }
                val uid = auth.currentUserId()
                suspendCoroutine<OperationResult> { continuation ->
                    val doc =
                        db.getOrAddDocumentInChild(
                            USERS_COLLECTION_PATH,
                            uid,
                            SUPPLIERS_COLLECTION_PATH,
                            oldSupplier.id,
                        )
                    doc.update(
                        mapOf(
                            SUPPLIER_ID_FIELD to doc.id,
                            SUPPLIER_COMPANY_NAME_FIELD to newSupplier.companyName,
                            SUPPLIER_CONTACT_NAME_FIELD to newSupplier.contactName,
                            SUPPLIER_CONTACT_EMAIL_FIELD to newSupplier.contactEmail,
                            SUPPLIER_CONTACT_PHONE_FIELD to newSupplier.contactPhone,
                            SUPPLIER_ADDRESS_FIELD to newSupplier.address,
                            SUPPLIER_CATEGORY_FIELD to newSupplier.category,
                        ),
                    ).addOnSuccessListener {
                        continuation.resume(OperationResult.Success)
                    }.addOnFailureListener {
                        continuation.resume(OperationResult.Failure(R.string.core_data_update_supplier_failure_generic))
                    }
                }
            } catch (_: UnknownHostException) {
                OperationResult.Failure(R.string.core_data_update_supplier_failure_network)
            } catch (e: Exception) {
                Timber.e(e)
                OperationResult.Failure(R.string.core_data_update_supplier_failure_generic)
            }
        }
    }

    override suspend fun deleteSupplier(supplier: Supplier): OperationResult {
        return withContext(ioDispatcher) {
            try {
                auth.ensureUserExists { message ->
                    return@withContext OperationResult.Failure(message)
                }
                val uid = auth.currentUserId()
                suspendCoroutine<OperationResult> { continuation ->
                    val doc =
                        db.getOrAddDocumentInChild(
                            USERS_COLLECTION_PATH,
                            uid,
                            SUPPLIERS_COLLECTION_PATH,
                            supplier.id,
                        )
                    doc.delete().addOnSuccessListener {
                        continuation.resume(OperationResult.Success)
                    }.addOnFailureListener {
                        continuation.resume(OperationResult.Failure(R.string.core_data_delete_supplier_failure_generic))
                    }
                }
            } catch (_: UnknownHostException) {
                OperationResult.Failure(R.string.core_data_delete_supplier_failure_network)
            } catch (e: Exception) {
                Timber.e(e)
                OperationResult.Failure(R.string.core_data_delete_supplier_failure_generic)
            }
        }
    }
}