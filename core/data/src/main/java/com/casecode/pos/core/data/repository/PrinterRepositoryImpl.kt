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
import com.casecode.pos.core.data.model.asExternalMapper
import com.casecode.pos.core.data.model.asExternalModel
import com.casecode.pos.core.data.utils.ensureUserExists
import com.casecode.pos.core.domain.repository.AuthRepository
import com.casecode.pos.core.domain.repository.PrinterRepository
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.firebase.services.FirestoreService
import com.casecode.pos.core.firebase.services.PRINTER_INFO_COLLECTION_PATH
import com.casecode.pos.core.firebase.services.USERS_COLLECTION_PATH
import com.casecode.pos.core.model.data.PrinterInfo
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
    private val db: FirestoreService,
    private val auth: AuthRepository,
    @Dispatcher(IO) val ioDispatcher: CoroutineDispatcher,
) : PrinterRepository {
    override suspend fun getPrinters(): Resource<List<PrinterInfo>> {
        return withContext(ioDispatcher) {
            try {
                val uid = auth.currentUserId()
                auth.ensureUserExists<List<PrinterInfo>> {
                    return@withContext it
                }
                suspendCoroutine { continuation ->
                    db
                        .getCollectionChild(
                            USERS_COLLECTION_PATH,
                            uid,
                            PRINTER_INFO_COLLECTION_PATH,
                        )
                        .get()
                        .addOnSuccessListener { documents ->
                            val printerMutableList = mutableListOf<PrinterInfo>()
                            documents.mapNotNull {
                            }
                            documents.documents.mapNotNull { document ->
                                document
                                    .asExternalModel()
                                    .let { printerMutableList.add(it) }
                            }

                            if (printerMutableList.isEmpty()) {
                                continuation.resume(Resource.empty())
                            } else {
                                continuation.resume(Resource.success(printerMutableList))
                            }
                        }.addOnFailureListener {
                            Timber.e("${it.message}")
                            continuation.resume(
                                Resource.error(R.string.core_data_get_printer_info_failure),
                            )
                        }
                }
            } catch (_: UnknownHostException) {
                Resource.error(R.string.core_data_get_printer_info_failure_network)
            }
        }
    }

    override suspend fun addPrinter(printerInfo: PrinterInfo): Resource<Int> {
        return withContext(ioDispatcher) {
            try {
                val uid = auth.currentUserId()
                auth.ensureUserExists<Int> {
                    return@withContext it
                }
                suspendCoroutine { continuation ->
                    db
                        .getCollectionChild(
                            USERS_COLLECTION_PATH,
                            uid,
                            PRINTER_INFO_COLLECTION_PATH,
                        )
                        .document(printerInfo.name)
                        .set(printerInfo.asExternalMapper())
                        .addOnSuccessListener {
                            continuation.resume(
                                Resource.success(R.string.core_data_add_printer_info_sucessfully),
                            )
                        }.addOnFailureListener {
                            Timber.e("${it.message}")
                            continuation.resume(
                                Resource.error(R.string.core_data_add_printer_info_failure_network),
                            )
                        }
                }
            } catch (_: UnknownHostException) {
                return@withContext Resource.error(
                    R.string.core_data_add_printer_info_failure_network,
                )
            } catch (e: Exception) {
                Timber.e("${e.message}")
                return@withContext Resource.error(R.string.core_data_add_printer_info_failure)
            }
        }
    }
}