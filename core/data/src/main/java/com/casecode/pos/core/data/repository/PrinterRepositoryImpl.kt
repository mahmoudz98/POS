package com.casecode.pos.core.data.repository

import com.casecode.pos.core.common.AppDispatchers.IO
import com.casecode.pos.core.common.Dispatcher
import com.casecode.pos.core.data.R
import com.casecode.pos.core.data.model.asExternalMapper
import com.casecode.pos.core.data.service.AuthService
import com.casecode.pos.core.data.service.checkUserNotFound
import com.casecode.pos.core.data.utils.PRINTER_INFO_COLLECTION_PATH
import com.casecode.pos.core.data.utils.getCollectionRefFromUser
import com.casecode.pos.core.domain.repository.PrinterRepository
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.PrinterInfo
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.UnknownHostException
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class PrinterRepositoryImpl
    @Inject
    constructor(
        private val firestore: FirebaseFirestore,
        private val auth: AuthService,
        @Dispatcher(IO) val ioDispatcher: CoroutineDispatcher,
    ) : PrinterRepository {
        override suspend fun getPrinters(): Resource<List<PrinterInfo>> {
            return withContext(ioDispatcher) {
                try {
                    val uid = auth.currentUserId()
                    auth.checkUserNotFound<List<PrinterInfo>> {
                        return@withContext it
                    }
                    suspendCoroutine { continuation ->
                        firestore
                            .getCollectionRefFromUser(uid, PRINTER_INFO_COLLECTION_PATH)
                            .get()
                            .addOnSuccessListener { documents ->
                                val printerMutableList = mutableListOf<PrinterInfo>()
                                documents.documents.mapNotNull { document ->
                                    document
                                        .toObject(PrinterInfo::class.java)
                                        ?.let { printerMutableList.add(it) }
                                }
                                if (printerMutableList.isEmpty()) {
                                    continuation.resume(Resource.empty())
                                } else {
                                    continuation.resume(Resource.success(printerMutableList))
                                }
                            }.addOnFailureListener {
                                Timber.e("${it.message}")
                                continuation.resume(Resource.error(R.string.core_data_get_printer_info_failure))
                            }
                    }
                } catch (e: UnknownHostException) {
                Resource.error(R.string.core_data_get_printer_info_failure_network)
            }
        }
    }

    override suspend fun addPrinter(printerInfo: PrinterInfo): Resource<Int> {
        return withContext(ioDispatcher) {
            try {
                val uid = auth.currentUserId()
                auth.checkUserNotFound<Int> {
                    return@withContext it
                }
                suspendCoroutine { continuation ->

                    firestore
                        .getCollectionRefFromUser(uid, PRINTER_INFO_COLLECTION_PATH)
                        .document(printerInfo.name)
                        .set(printerInfo.asExternalMapper())
                        .addOnSuccessListener {
                            continuation.resume(Resource.success(R.string.core_data_add_printer_info_sucessfully))
                        }.addOnFailureListener {
                            Timber.e("${it.message}")
                            continuation.resume(Resource.error(R.string.core_data_add_printer_info_failure_network))
                        }
                }
            } catch (e: UnknownHostException) {
                return@withContext Resource.error(R.string.core_data_add_printer_info_failure_network)
            } catch (e: Exception) {
                Timber.e("${e.message}")
                return@withContext Resource.error(R.string.core_data_add_printer_info_failure)
            }
        }
    }
}