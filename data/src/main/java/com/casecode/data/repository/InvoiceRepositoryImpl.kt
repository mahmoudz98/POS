package com.casecode.data.repository

import com.casecode.data.mapper.toInvoicesGroup
import com.casecode.data.utils.AppDispatchers
import com.casecode.data.utils.Dispatcher
import com.casecode.data.utils.getCollectionRefFromUser
import com.casecode.data.utils.getDocumentFromUser
import com.casecode.domain.model.users.Invoice
import com.casecode.domain.model.users.InvoiceGroup
import com.casecode.domain.repository.InvoiceRepository
import com.casecode.domain.utils.Invoice_CREATEDBY_FIELD
import com.casecode.domain.utils.Invoice_CUSTOMER_FIELD
import com.casecode.domain.utils.Invoice_DATE_FIELD
import com.casecode.domain.utils.Invoice_FIELD
import com.casecode.domain.utils.Invoice_ITEMS_FIELD
import com.casecode.domain.utils.Invoice_NAME_FIELD
import com.casecode.domain.utils.Resource
import com.casecode.pos.data.R
import com.casecode.service.AuthService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.UnknownHostException
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class InvoiceRepositoryImpl @Inject constructor(
    private val auth: AuthService,
    private val db: FirebaseFirestore,
    @Dispatcher(AppDispatchers.IO) val ioDispatcher: CoroutineDispatcher,
) : InvoiceRepository {

    override suspend fun addInvoice(invoice: Invoice): Resource<Int> {
        return withContext(ioDispatcher) {
            try {
                suspendCoroutine { continuation ->
                    if (checkHasUser(continuation)) return@suspendCoroutine
                    val documentRef = db.getDocumentFromUser(auth.currentUserId, Invoice_FIELD)
                    val invoiceMap = mapOf(
                        Invoice_NAME_FIELD to documentRef.id,
                        Invoice_DATE_FIELD to invoice.date,
                        Invoice_CREATEDBY_FIELD to invoice.createdBy,
                        Invoice_CUSTOMER_FIELD to invoice.customer,
                        Invoice_ITEMS_FIELD to invoice.items,
                    )
                    documentRef.set(invoiceMap).addOnSuccessListener {
                        continuation.resume(Resource.success(R.string.add_invoice_successfully))
                    }.addOnFailureListener {
                        continuation.resume(Resource.error(R.string.add_invoice_failure))
                    }
                }
            } catch (e: UnknownHostException) {
                Resource.error(R.string.add_invoice_failure_network)

            } catch (e: Exception) {
                Resource.error(R.string.add_invoice_failure)
            }
        }
    }

    override suspend fun getInvoices(): Resource<List<InvoiceGroup>> {
        return withContext(ioDispatcher) {
            try {
                suspendCoroutine { continuation ->
                    if (checkHasUser(continuation)) return@suspendCoroutine

                    db.getCollectionRefFromUser(auth.currentUserId, Invoice_FIELD).get()
                        .addOnSuccessListener {
                            val invoices = it.documents.mapNotNull { document ->
                                document.toObject(Invoice::class.java)
                            }
                            if(invoices.isEmpty())
                            {
                                continuation.resume(Resource.empty())
                                return@addOnSuccessListener
                            }
                            val invoicesGroup = invoices.toInvoicesGroup()
                            continuation.resume(Resource.success(invoicesGroup))
                        }.addOnFailureListener {
                            continuation.resume(Resource.error(R.string.get_invoice_failure))
                        }
                }
            } catch (e: UnknownHostException) {
                Resource.error(R.string.get_invoice_failure_network)

            } catch (e: Exception) {
                Resource.error(R.string.get_invoice_failure)
            }

        }
    }



    private  fun <T>checkHasUser(continuation: Continuation<Resource<T>>): Boolean {
        if (!auth.hasUser) {
            continuation.resume(
                Resource.Error(
                    com.casecode.pos.domain.R.string.uid_empty,
                ),
            )
            return true
        }
        return false
    }
}