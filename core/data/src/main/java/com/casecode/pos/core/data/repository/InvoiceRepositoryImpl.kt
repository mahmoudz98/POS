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

import android.icu.util.Calendar
import com.casecode.pos.core.common.AppDispatchers.IO
import com.casecode.pos.core.common.Dispatcher
import com.casecode.pos.core.data.R
import com.casecode.pos.core.data.model.asExternalMapper
import com.casecode.pos.core.data.model.toInvoicesGroup
import com.casecode.pos.core.data.utils.ensureUserExists
import com.casecode.pos.core.domain.repository.AuthRepository
import com.casecode.pos.core.domain.repository.InvoiceRepository
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.firebase.services.FirestoreService
import com.casecode.pos.core.firebase.services.INVOICE_DATE_FIELD
import com.casecode.pos.core.firebase.services.INVOICE_FIELD
import com.casecode.pos.core.firebase.services.USERS_COLLECTION_PATH
import com.casecode.pos.core.model.data.users.Invoice
import com.casecode.pos.core.model.data.users.InvoiceGroup
import com.google.firebase.Timestamp
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
    private val auth: AuthRepository,
    private val db: FirestoreService,
    @Dispatcher(IO) val ioDispatcher: CoroutineDispatcher,
) : InvoiceRepository {
    override suspend fun addInvoice(invoice: Invoice): Resource<Int> {
        return withContext(ioDispatcher) {
            try {
                auth.ensureUserExists<Int> {
                    return@withContext it
                }
                val currentUID = auth.currentUserId()
                val currentNameLogin =
                    auth.currentNameLogin()

                suspendCoroutine { continuation ->
                    val doc =
                        db.getDocumentInChild(USERS_COLLECTION_PATH, currentUID, INVOICE_FIELD)
                    val invoiceMap = invoice.asExternalMapper(doc, currentNameLogin)
                    doc
                        .set(invoiceMap)
                        .addOnSuccessListener {
                            continuation.resume(
                                Resource.success(R.string.core_data_add_invoice_successfully),
                            )
                        }.addOnFailureListener {
                            continuation.resume(
                                Resource.error(R.string.core_data_add_invoice_failure),
                            )
                        }
                }
            } catch (_: UnknownHostException) {
                Resource.error(R.string.core_data_add_invoice_failure_network)
            } catch (_: Exception) {
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
                        .getCollectionChild(USERS_COLLECTION_PATH, currentUID, INVOICE_FIELD)
                        /* firestore
                             .getCollectionRefFromUser(currentUID, INVOICE_FIELD)*/
                        .whereGreaterThanOrEqualTo(INVOICE_DATE_FIELD, startTimestamp)
                        .whereLessThanOrEqualTo(INVOICE_DATE_FIELD, endTimestamp)
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
                            continuation.resume(
                                Resource.error(R.string.core_data_get_invoice_failure),
                            )
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
            auth.ensureUserExists<List<InvoiceGroup>> { return@withContext it }
            try {
                val currentUID = auth.currentUserId()
                suspendCoroutine { continuation ->
                    db
                        .getCollectionChild(USERS_COLLECTION_PATH, currentUID, INVOICE_FIELD)
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
                            continuation.resume(
                                Resource.error(R.string.core_data_get_invoice_failure),
                            )
                        }
                }
            } catch (_: UnknownHostException) {
                Resource.error(R.string.core_data_get_invoice_failure_network)
            } catch (e: Exception) {
                Timber.e(e)
                Resource.error(R.string.core_data_get_invoice_failure)
            }
        }
    }
}