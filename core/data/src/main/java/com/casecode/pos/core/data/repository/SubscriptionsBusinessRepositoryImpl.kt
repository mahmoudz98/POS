package com.casecode.pos.core.data.repository

import com.casecode.pos.core.data.model.asSubscriptionBusinessModel
import com.casecode.pos.core.data.model.asSubscriptionRequest
import com.casecode.pos.core.data.service.AuthService
import com.casecode.pos.core.common.Dispatcher
import com.casecode.pos.core.common.AppDispatchers.IO
import com.casecode.pos.core.data.utils.SUBSCRIPTION_BUSINESS_FIELD
import com.casecode.pos.core.data.utils.USERS_COLLECTION_PATH
import com.casecode.pos.core.domain.repository.AddSubscriptionBusiness
import com.casecode.pos.core.domain.repository.SubscriptionsBusinessRepository
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.SubscriptionBusiness
import com.casecode.pos.core.data.R
import com.casecode.pos.core.data.service.checkUserNotFound
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.UnknownHostException
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SubscriptionsBusinessRepositoryImpl
@Inject
constructor(
    private val fireStore: FirebaseFirestore,
    private val auth: AuthService,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : SubscriptionsBusinessRepository {
    override suspend fun setSubscriptionBusiness(
        subscriptionBusiness: SubscriptionBusiness,
    ): AddSubscriptionBusiness {
        return withContext(ioDispatcher) {
            try {
               auth.checkUserNotFound<Boolean> {
                    return@withContext it
               }
                val currentUID = auth.currentUserId()
                val resultAddSubscription =
                    suspendCoroutine<AddSubscriptionBusiness> { continuation ->

                        val addSubscriptionBusinessRequest =
                            subscriptionBusiness.asSubscriptionRequest()

                        fireStore.collection(USERS_COLLECTION_PATH).document(currentUID)
                            .update(
                                SUBSCRIPTION_BUSINESS_FIELD,
                                FieldValue.arrayUnion(addSubscriptionBusinessRequest),
                            ).addOnSuccessListener {
                                Timber.d("Subscription business is added successfully")
                                continuation.resume(AddSubscriptionBusiness.success(true))
                            }.addOnFailureListener {
                                continuation.resume(AddSubscriptionBusiness.error(R.string.core_data_add_subscription_business_failure))

                                Timber.e("Add Subscription Business failure: $it")
                            }
                    }
                resultAddSubscription
            } catch (e: UnknownHostException) {
                AddSubscriptionBusiness.error(R.string.core_data_add_subscription_business_network)
            } catch (e: Exception) {
                Timber.e("Exception while adding business: $e")
                AddSubscriptionBusiness.error(R.string.core_data_add_subscription_business_failure)
            }
        }
    }

    override fun getSubscriptionsBusiness(): Flow<Resource<List<SubscriptionBusiness>>> {
        return callbackFlow<Resource<List<SubscriptionBusiness>>> {
            trySend(Resource.Loading)
            auth.checkUserNotFound<List<SubscriptionBusiness>> {
                trySend(it)
                close()
            }
            val currentUID = auth.currentUserId()
            val listenerRegistration =
                fireStore.collection(USERS_COLLECTION_PATH).document(currentUID)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Timber.e(error)
                            trySend(Resource.error(R.string.core_data_add_subscription_business_failure))
                            close()
                        }
                        @Suppress("UNCHECKED_CAST")
                        val subscriptionBusinessMap = snapshot?.get(
                            SUBSCRIPTION_BUSINESS_FIELD,
                        ) as? List<Map<String, Any>>
                        Timber.e("subscriptionBusinessMap: $subscriptionBusinessMap")
                        if (subscriptionBusinessMap.isNullOrEmpty()) {
                            trySend(Resource.empty())
                        } else {
                            trySend(
                                Resource.success(
                                    asSubscriptionBusinessModel(
                                        subscriptionBusinessMap,
                                    ),
                                ),
                            )
                        }
                    }
            awaitClose {
                listenerRegistration.remove()
            }
        }.flowOn(ioDispatcher)
    }
}