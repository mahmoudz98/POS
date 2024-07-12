package com.casecode.pos.core.data.repository

import com.casecode.pos.core.common.AppDispatchers.IO
import com.casecode.pos.core.common.Dispatcher
import com.casecode.pos.core.data.R
import com.casecode.pos.core.data.model.asExternalMapper
import com.casecode.pos.core.data.model.toInvoicesGroup
import com.casecode.pos.core.data.service.AuthService
import com.casecode.pos.core.data.service.checkHasUser
import com.casecode.pos.core.data.utils.Invoice_FIELD
import com.casecode.pos.core.data.utils.getCollectionRefFromUser
import com.casecode.pos.core.data.utils.getDocumentFromUser
import com.casecode.pos.core.domain.repository.InvoiceRepository
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Invoice
import com.casecode.pos.core.model.data.users.InvoiceGroup
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class InvoiceRepositoryImpl @Inject constructor(
    private val auth: AuthService,
    private val db: FirebaseFirestore,
    @Dispatcher(IO) val ioDispatcher: CoroutineDispatcher,
) : InvoiceRepository {

    override suspend fun addInvoice(invoice: Invoice): Resource<Int> {
        return withContext(ioDispatcher) {
            try {
                auth.checkHasUser<Int> {
                    return@withContext it
                }
                val currentUID = auth.currentUserId()
                suspendCoroutine { continuation ->
                    val documentRef = db.getDocumentFromUser(currentUID, Invoice_FIELD)

                    val invoiceMap = invoice.asExternalMapper(documentRef)
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
            auth.checkHasUser<List<InvoiceGroup>> {  return@withContext it }
            try {
                val currentUID = auth.currentUserId()
                suspendCoroutine { continuation ->
                    db.getCollectionRefFromUser(currentUID, Invoice_FIELD).get()
                        .addOnSuccessListener {
                            val invoices = it.documents.mapNotNull { document ->
                                document.toObject(Invoice::class.java)
                            }
                            if (invoices.isEmpty()) {
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



}