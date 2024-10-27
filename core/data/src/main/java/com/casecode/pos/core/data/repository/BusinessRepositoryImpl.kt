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
import com.casecode.pos.core.data.model.asEntityBusiness
import com.casecode.pos.core.data.model.asExternalBranch
import com.casecode.pos.core.data.model.asExternalBusiness
import com.casecode.pos.core.data.utils.checkUserNotFoundAndReturnErrorMessage
import com.casecode.pos.core.data.utils.checkUserNotFoundAndReturnMessage
import com.casecode.pos.core.domain.repository.AddBusiness
import com.casecode.pos.core.domain.repository.AuthRepository
import com.casecode.pos.core.domain.repository.BusinessRepository
import com.casecode.pos.core.domain.repository.CompleteBusiness
import com.casecode.pos.core.domain.utils.AddBranchBusinessResult
import com.casecode.pos.core.domain.utils.BusinessResult
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.firebase.services.BRANCHES_FIELD
import com.casecode.pos.core.firebase.services.BUSINESS_FIELD
import com.casecode.pos.core.firebase.services.BUSINESS_IS_COMPLETED_STEP_FIELD
import com.casecode.pos.core.firebase.services.FirestoreService
import com.casecode.pos.core.firebase.services.USERS_COLLECTION_PATH
import com.casecode.pos.core.model.data.users.Branch
import com.casecode.pos.core.model.data.users.Business
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.UnknownHostException
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BusinessRepositoryImpl
@Inject
constructor(
    private val db: FirestoreService,
    private val auth: AuthRepository,
    @Dispatcher(IO) val ioDispatcher: CoroutineDispatcher,
) : BusinessRepository {
    override suspend fun getBusiness(): BusinessResult {
        return withContext(ioDispatcher) {
            try {
                auth.checkUserNotFoundAndReturnMessage {
                    return@withContext BusinessResult.Error(it)
                }
                val doc = db.getDocument(USERS_COLLECTION_PATH, auth.currentUserId())

                @Suppress("UNCHECKED_CAST")
                val businessMap = doc.get(BUSINESS_FIELD) as Map<String, Any>
                val business = businessMap.asEntityBusiness()
                return@withContext BusinessResult.Success(business)
            } catch (_: UnknownHostException) {
                BusinessResult.Error(R.string.core_data_get_business_failure_network)
            } catch (e: Exception) {
                Timber.e(e)
                return@withContext BusinessResult.Error(R.string.core_data_get_business_failure)
            }
        }
    }

    override suspend fun setBusiness(business: Business): AddBusiness {
        return withContext(ioDispatcher) {
            try {
                auth.checkUserNotFoundAndReturnErrorMessage<Boolean> {
                    return@withContext it
                }

                val currentUID = auth.currentUserId()

                suspendCoroutine<AddBusiness> { continuation ->
                    db.setDocumentWithTask(
                        USERS_COLLECTION_PATH,
                        currentUID,
                        business.asExternalBusiness() as Map<String, Any>,
                    )
                        .addOnSuccessListener {
                            continuation.resume(Resource.Companion.success(true))
                        }.addOnFailureListener {
                            continuation.resume(Resource.Companion.error(R.string.core_data_add_subscription_business_failure))
                        }
                }
            } catch (_: FirebaseFirestoreException) {
                Resource.Companion.error(R.string.core_data_add_business_failure)
            } catch (_: UnknownHostException) {
                Resource.Companion.error(R.string.core_data_add_business_network)
            } catch (_: Exception) {
                Resource.Companion.error(R.string.core_data_add_business_failure)
            }
        }
    }

    override suspend fun completeBusinessSetup(): CompleteBusiness {
        return withContext(ioDispatcher) {
            try {
                if (!auth.hasUser()) {
                    return@withContext Resource.empty(message = R.string.core_data_uid_empty)
                }
                val currentUID = auth.currentUserId()

                suspendCoroutine<CompleteBusiness> { continuation ->
                    db.updateDocumentWithTask(
                        USERS_COLLECTION_PATH,
                        currentUID,
                        mapOf("$BUSINESS_FIELD.$BUSINESS_IS_COMPLETED_STEP_FIELD" to true),
                    ).addOnSuccessListener {
                        continuation.resume(Resource.success(true))
                    }.addOnFailureListener {
                        continuation.resume(Resource.error(R.string.core_data_complete_business_failure))
                    }
                }
            } catch (_: FirebaseFirestoreException) {
                Resource.error(R.string.core_data_complete_business_failure)
            } catch (_: Exception) {
                Resource.error(R.string.core_data_complete_business_failure)
            }
        }
    }

    override suspend fun addBranch(branch: Branch): AddBranchBusinessResult {
        return withContext(ioDispatcher) {
            if (!auth.hasUser()) {
                return@withContext AddBranchBusinessResult.Error(message = R.string.core_data_uid_empty)
            }
            val currentUID = auth.currentUserId()

            suspendCoroutine<AddBranchBusinessResult> { continuation ->
                db.updateDocumentWithTask(
                    USERS_COLLECTION_PATH,
                    currentUID,
                    mapOf("$BUSINESS_FIELD.$BRANCHES_FIELD" to FieldValue.arrayUnion(branch.asExternalBranch())),
                ).addOnSuccessListener {
                    continuation.resume(AddBranchBusinessResult.Success)
                }.addOnFailureListener {
                    when (it) {
                        is UnknownHostException -> {
                            continuation.resume(AddBranchBusinessResult.Error(R.string.core_data_add_branch_business_network))
                        }

                        else -> {
                            continuation.resume(AddBranchBusinessResult.Error(R.string.core_data_add_branch_business_failure))
                        }
                    }
                }
            }
        }
    }
}