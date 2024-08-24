package com.casecode.pos.core.data.repository

import com.casecode.pos.core.common.AppDispatchers.IO
import com.casecode.pos.core.common.Dispatcher
import com.casecode.pos.core.data.R
import com.casecode.pos.core.data.model.asEntityBusiness
import com.casecode.pos.core.data.model.asExternalBranch
import com.casecode.pos.core.data.model.asExternalBusiness
import com.casecode.pos.core.data.service.AuthService
import com.casecode.pos.core.data.service.checkUserNotFound
import com.casecode.pos.core.data.utils.BRANCHES_FIELD
import com.casecode.pos.core.data.utils.BUSINESS_FIELD
import com.casecode.pos.core.data.utils.BUSINESS_IS_COMPLETED_STEP_FIELD
import com.casecode.pos.core.data.utils.USERS_COLLECTION_PATH
import com.casecode.pos.core.domain.repository.AddBusiness
import com.casecode.pos.core.domain.repository.BusinessRepository
import com.casecode.pos.core.domain.repository.CompleteBusiness
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Branch
import com.casecode.pos.core.model.data.users.Business
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.UnknownHostException
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Created by Mahmoud Abdalhafeez on 12/13/2023
 */
class BusinessRepositoryImpl
    @Inject
    constructor(
        private val firestore: FirebaseFirestore,
        private val auth: AuthService,
        @Dispatcher(IO) val ioDispatcher: CoroutineDispatcher,
    ) : BusinessRepository {
        override suspend fun getBusiness(): Resource<Business> {
            return withContext(ioDispatcher) {
                try {
                    auth.checkUserNotFound<Business> {
                        return@withContext it
                }

                val document =
                    firestore
                        .collection(USERS_COLLECTION_PATH)
                        .document(auth.currentUserId())
                        .get()
                        .await()

                @Suppress("UNCHECKED_CAST")
                val businessMap =
                    document.get(BUSINESS_FIELD) as Map<String, Any>
                val business = businessMap.asEntityBusiness()
                return@withContext Resource.success(business)
            } catch (e: UnknownHostException) {
                Resource.error(R.string.core_data_get_business_failure_network)
            } catch (e: Exception) {
                Timber.e(e)
                return@withContext Resource.error(R.string.core_data_get_business_failure)
            }
        }
    }

    override suspend fun setBusiness(business: Business): AddBusiness {
        return withContext(ioDispatcher) {
            try {
                auth.checkUserNotFound<Boolean> {
                    return@withContext it
                }

                val currentUID = auth.currentUserId()
                val resultAddBusiness =
                    suspendCoroutine<AddBusiness> { continuation ->

                        firestore
                            .collection(USERS_COLLECTION_PATH)
                            .document(currentUID)
                            .set(business.asExternalBusiness() as Map<String, Any>)
                            .addOnSuccessListener {
                                continuation.resume(AddBusiness.success(true))
                            }.addOnFailureListener {
                                val message =
                                    it.message ?: "Failure in database, when add new business"
                                Timber.e("Business Failure: $message")
                                continuation.resume(AddBusiness.error(R.string.core_data_add_subscription_business_failure))
                            }
                    }
                resultAddBusiness
            } catch (e: FirebaseFirestoreException) {
                AddBusiness.error(R.string.core_data_add_business_failure)
            } catch (e: UnknownHostException) {
                AddBusiness.error(R.string.core_data_add_business_network)
            } catch (e: Exception) {
                AddBusiness.error(R.string.core_data_add_business_failure)
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

                val resultCompleteBusinessStep =
                    suspendCoroutine<CompleteBusiness> { continuation ->
                        firestore
                            .collection(USERS_COLLECTION_PATH)
                            .document(currentUID)
                            .update(
                                "$BUSINESS_FIELD.$BUSINESS_IS_COMPLETED_STEP_FIELD",
                                true,
                            ).addOnSuccessListener {
                                continuation.resume(CompleteBusiness.success(true))
                            }.addOnFailureListener {
                                continuation.resume(CompleteBusiness.error(R.string.core_data_complete_business_failure))
                            }
                    }
                resultCompleteBusinessStep
            } catch (e: FirebaseFirestoreException) {
                CompleteBusiness.error(R.string.core_data_complete_business_failure)
            } catch (e: Exception) {
                CompleteBusiness.error(R.string.core_data_complete_business_failure)
            }
        }
    }

    override suspend fun addBranch(branch: Branch): Resource<Boolean> {
        return withContext(ioDispatcher) {
            if (!auth.hasUser()) {
                return@withContext Resource.empty(message = R.string.core_data_uid_empty)
            }
            val currentUID = auth.currentUserId()

            suspendCoroutine { continuation ->
                firestore
                    .collection(USERS_COLLECTION_PATH)
                    .document(currentUID)
                    .update(
                        "$BUSINESS_FIELD.$BRANCHES_FIELD",
                        FieldValue.arrayUnion(branch.asExternalBranch()),
                    ).addOnSuccessListener {
                        continuation.resume(Resource.success(true))
                    }.addOnFailureListener {
                        when (it) {
                            is UnknownHostException -> {
                                continuation.resume(Resource.error(R.string.core_data_add_branch_business_network))
                            }

                            else -> {
                                Timber.e("Exception while adding new branch: $it")
                                continuation.resume(Resource.error(R.string.core_data_add_branch_business_failure))
                            }
                        }
                    }
            }
        }
    }

    /*override suspend fun updateBranch(
        oldBranch: Branch,
        NewBranch: Branch,
    ): Resource<Boolean> {
        return withContext(ioDispatcher) {
            suspendCoroutine {continuation->
                val updatesEmployee = mapOf(
                    EMPLOYEES_FIELD to FieldValue.arrayRemove(oldBranch.asExternalEmployee()),
                    EMPLOYEES_FIELD to FieldValue.arrayUnion(newBranch.asExternalEmployee()),
                )
                firestore.collection(USERS_COLLECTION_PATH).document(auth.currentUserId)
                    .update(updatesEmployee).addOnSuccessListener {
                        continuation.resumeWith(Result.success(Resource.success(true)))
                    }.addOnFailureListener{ exception ->
                        when (exception) {
                            is UnknownHostException -> {
                                continuation.resume(Resource.error(R.string.employee_update_business_network))
                            }
                            else -> {
                                Timber.e("Exception while adding employees: $exception")
                                continuation.resume(Resource.error(R.string.employee_update_business_failure))
                            }
                        }
                    }
            }
        }
    }*/
}