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
import com.casecode.pos.core.data.utils.ensureUserExistsOrReturnError
import com.casecode.pos.core.domain.repository.AuthRepository
import com.casecode.pos.core.domain.repository.SupplierInvoiceRepository
import com.casecode.pos.core.domain.utils.OperationResult
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.firebase.services.FirestoreService
import com.casecode.pos.core.firebase.services.SUPPLIER_INVOICES_COLLECTION_PATH
import com.casecode.pos.core.firebase.services.SUPPLIER_INVOICE_PAYMENT_DETAILS_FIELD
import com.casecode.pos.core.firebase.services.SUPPLIER_INVOICE_PAYMENT_STATUS_FIELD
import com.casecode.pos.core.firebase.services.USERS_COLLECTION_PATH
import com.casecode.pos.core.model.data.users.PaymentDetails
import com.casecode.pos.core.model.data.users.PaymentStatus
import com.casecode.pos.core.model.data.users.SupplierInvoice
import com.google.firebase.firestore.FieldValue
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

class SupplierInvoiceRepositoryImpl
@Inject constructor(
    private val db: FirestoreService,
    private val auth: AuthRepository,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : SupplierInvoiceRepository {

    override fun getInvoices(): Flow<Resource<List<SupplierInvoice>>> = flow {
        emit(Resource.Loading)
        auth.ensureUserExistsOrReturnError<List<SupplierInvoice>> {
            emit(it)
            return@flow
        }
        val uid = auth.currentUserId()
        db.listenToCollectionChild(
            collection = USERS_COLLECTION_PATH,
            documentId = uid,
            collectionChild = SUPPLIER_INVOICES_COLLECTION_PATH,
        ).collect { snapshot ->
            val invoices = mutableListOf<SupplierInvoice>()
            snapshot.documents.mapNotNull { document ->
                Timber.e("supplierInvoiceMap: ${document.data}")
                val supplierInvoiceMap = document.data as Map<String, Any>
                invoices.add(supplierInvoiceMap.asDomainModel())
            }
            if (invoices.isNotEmpty()) {
                emit(Resource.Success(invoices))
            } else {
                emit(Resource.empty())
            }
        }
    }.catch { e ->
        Timber.e(e)
        emit(Resource.Error(R.string.core_data_error_fetching_supplier_invoices))
    }.flowOn(ioDispatcher)

    override suspend fun addInvoice(invoice: SupplierInvoice): OperationResult {
        return withContext(ioDispatcher) {
            try {
                auth.ensureUserExists { message ->
                    return@withContext OperationResult.Failure(message)
                }
                val uid = auth.currentUserId()
                val currentUserName = auth.currentNameLogin()
                val invoiceUpdated = invoice.copy(createdBy = currentUserName)
                suspendCoroutine<OperationResult> { continuation ->
                    val doc =
                        db.getDocumentInChild(
                            USERS_COLLECTION_PATH,
                            uid,
                            SUPPLIER_INVOICES_COLLECTION_PATH,
                        )
                    doc.set(
                        asExternalMapper(doc.id, invoiceUpdated),
                    ).addOnSuccessListener {
                        continuation.resume(OperationResult.Success)
                    }.addOnFailureListener {
                        continuation.resume(
                            OperationResult.Failure(
                                R.string.core_data_add_supplier_invoice_failure_generic,
                            ),
                        )
                    }
                }
            } catch (_: UnknownHostException) {
                OperationResult.Failure(R.string.core_data_add_supplier_invocie_failure_network)
            } catch (e: Exception) {
                Timber.e(e)
                OperationResult.Failure(R.string.core_data_add_supplier_invoice_failure_generic)
            }
        }
    }

    override suspend fun updateInvoice(invoice: SupplierInvoice): OperationResult {
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
                            SUPPLIER_INVOICES_COLLECTION_PATH,
                            invoice.invoiceId,
                        )
                    doc.update(
                        asExternalMapper(doc.id, invoice),
                    ).addOnSuccessListener {
                        continuation.resume(OperationResult.Success)
                    }.addOnFailureListener {
                        continuation.resume(
                            OperationResult.Failure(
                                R.string.core_data_update_supplier_invoice_failure_generic,
                            ),
                        )
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

    override suspend fun addPaymentDetails(
        invoiceId: String,
        paymentDetails: PaymentDetails,
        paymentStatus: PaymentStatus,
    ): OperationResult {
        return withContext(ioDispatcher) {
            try {
                auth.ensureUserExists { message ->
                    return@withContext OperationResult.Failure(message)
                }
                val uid = auth.currentUserId()
                val currentUserName = auth.currentNameLogin()
                Timber.e("currentUsername: $currentUserName")
                val paymentWithCreatedBy = paymentDetails.copy(createdBy = currentUserName)
                Timber.e("paymentWithCreatedBy: $paymentWithCreatedBy")
                suspendCoroutine<OperationResult> { continuation ->
                    val doc = db.getOrAddDocumentInChild(
                        USERS_COLLECTION_PATH,
                        uid,
                        SUPPLIER_INVOICES_COLLECTION_PATH,
                        invoiceId,
                    )
                    val paymentMap =
                        mapOf(
                            SUPPLIER_INVOICE_PAYMENT_DETAILS_FIELD to
                                FieldValue.arrayUnion(paymentWithCreatedBy.asExternalMapper()),
                        )
                    doc.update(
                        mapOf(SUPPLIER_INVOICE_PAYMENT_STATUS_FIELD to paymentStatus.name),
                    )
                    doc.update(
                        paymentMap,
                    ).addOnSuccessListener {
                        continuation.resume(OperationResult.Success)
                    }.addOnFailureListener { exception ->
                        Timber.e(exception)
                        continuation.resume(
                            OperationResult.Failure(
                                R.string.core_data_update_supplier_invoice_payment_failure_generic,
                            ),
                        )
                    }
                }
            } catch (_: UnknownHostException) {
                OperationResult.Failure(
                    R.string.core_data_update_supplier_invoice_payment_failure_network,
                )
            } catch (e: Exception) {
                Timber.e(e)
                OperationResult.Failure(
                    R.string.core_data_update_supplier_invoice_payment_failure_generic,
                )
            }
        }
    }
}