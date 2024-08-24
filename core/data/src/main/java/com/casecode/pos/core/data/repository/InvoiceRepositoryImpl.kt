package com.casecode.pos.core.data.repository

import android.icu.util.Calendar
import com.casecode.pos.core.common.AppDispatchers.IO
import com.casecode.pos.core.common.Dispatcher
import com.casecode.pos.core.data.R
import com.casecode.pos.core.data.model.asExternalMapper
import com.casecode.pos.core.data.model.toInvoicesGroup
import com.casecode.pos.core.data.service.AuthService
import com.casecode.pos.core.data.service.checkUserNotFound
import com.casecode.pos.core.data.utils.Invoice_DATE_FIELD
import com.casecode.pos.core.data.utils.Invoice_FIELD
import com.casecode.pos.core.data.utils.getCollectionRefFromUser
import com.casecode.pos.core.data.utils.getDocumentFromUser
import com.casecode.pos.core.domain.repository.InvoiceRepository
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Invoice
import com.casecode.pos.core.model.data.users.InvoiceGroup
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.UnknownHostException
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class InvoiceRepositoryImpl
    @Inject
    constructor(
        private val auth: AuthService,
        private val db: FirebaseFirestore,
        @Dispatcher(IO) val ioDispatcher: CoroutineDispatcher,
    ) : InvoiceRepository {
        override suspend fun addInvoice(invoice: Invoice): Resource<Int> {
            return withContext(ioDispatcher) {
                try {
                    auth.checkUserNotFound<Int> {
                        return@withContext it
                    }
                    val currentUID = auth.currentUserId()
                    val currentNameLogin = auth.currentNameLogin()
                    suspendCoroutine { continuation ->
                        val documentRef = db.getDocumentFromUser(currentUID, Invoice_FIELD)
                        val invoiceMap = invoice.asExternalMapper(documentRef, currentNameLogin)
                        documentRef
                            .set(invoiceMap)
                            .addOnSuccessListener {
                                continuation.resume(Resource.success(R.string.core_data_add_invoice_successfully))
                            }.addOnFailureListener {
                                continuation.resume(Resource.error(R.string.core_data_add_invoice_failure))
                            }
                    }
                } catch (e: UnknownHostException) {
                    Resource.error(R.string.core_data_add_invoice_failure_network)
                } catch (e: Exception) {
                Resource.error(R.string.core_data_add_invoice_failure)
            }
        }
    }

    override suspend fun getTodayInvoices(): Resource<List<Invoice>> {
        return withContext(ioDispatcher) {
            try {
                // Get current date
                val calendar = Calendar.getInstance()

                // Set time to start of the day (00:00:00)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startOfDay = calendar.time

                // Set time to end of the day (23:59:59)
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                calendar.set(Calendar.MILLISECOND, 999)
                val endOfDay = calendar.time

                // Convert to Timestamps
                val startTimestamp = Timestamp(startOfDay)
                val endTimestamp = Timestamp(endOfDay)

                val currentUID = auth.currentUserId()
                suspendCoroutine { continuation ->
                    db
                        .getCollectionRefFromUser(currentUID, Invoice_FIELD)
                        .whereGreaterThanOrEqualTo(Invoice_DATE_FIELD, startTimestamp)
                        .whereLessThanOrEqualTo(Invoice_DATE_FIELD, endTimestamp)
                        .get()
                        .addOnSuccessListener {
                            val invoices =
                                it.documents.mapNotNull { document ->
                                    document.toObject(Invoice::class.java)
                                }
                            if (invoices.isEmpty()) {
                                continuation.resume(Resource.empty())
                                return@addOnSuccessListener
                            }
                            continuation.resume(Resource.success(invoices))
                        }.addOnFailureListener {
                            continuation.resume(Resource.error(R.string.core_data_get_invoice_failure))
                        }
                }
            } catch (e: UnknownHostException) {
                Timber.e("getInvoice: ${e.message}")
                Resource.error(R.string.core_data_get_invoice_failure)
            } catch (e: Exception) {
                Timber.e("getInvoice: ${e.message}")
                Resource.error(R.string.core_data_get_invoice_failure)
            }
        }
    }

    override suspend fun getInvoices(): Resource<List<InvoiceGroup>> {
        return withContext(ioDispatcher) {
            auth.checkUserNotFound<List<InvoiceGroup>> { return@withContext it }
            try {
                val currentUID = auth.currentUserId()
                suspendCoroutine { continuation ->
                    db
                        .getCollectionRefFromUser(currentUID, Invoice_FIELD)
                        .get()
                        .addOnSuccessListener {
                            val invoices =
                                it.documents.mapNotNull { document ->
                                    document.toObject(Invoice::class.java)
                                }
                            if (invoices.isEmpty()) {
                                continuation.resume(Resource.empty())
                                return@addOnSuccessListener
                            }
                            val invoicesGroup = invoices.toInvoicesGroup()
                            continuation.resume(Resource.success(invoicesGroup))
                        }.addOnFailureListener {
                            continuation.resume(Resource.error(R.string.core_data_get_invoice_failure))
                        }
                }
            } catch (e: UnknownHostException) {
                Resource.error(R.string.core_data_get_invoice_failure_network)
            } catch (e: Exception) {
                Resource.error(R.string.core_data_get_invoice_failure)
            }
        }
    }
}