package com.casecode.data.repository

import com.casecode.data.mapper.asSubscriptionBusinessModel
import com.casecode.data.mapper.asSubscriptionRequest
import com.casecode.data.utils.AppDispatchers.IO
import com.casecode.data.utils.Dispatcher
import com.casecode.domain.model.users.SubscriptionBusiness
import com.casecode.domain.repository.AddSubscriptionBusiness
import com.casecode.domain.repository.SubscriptionsBusinessRepository
import com.casecode.domain.utils.Resource
import com.casecode.domain.utils.SUBSCRIPTIONS_COLLECTION_PATH
import com.casecode.domain.utils.SUBSCRIPTION_BUSINESS_FIELD
import com.casecode.domain.utils.SUBSCRIPTION_COST_FIELD
import com.casecode.domain.utils.USERS_COLLECTION_PATH
import com.casecode.pos.data.R
import com.casecode.service.AuthService
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
            uid: String,
        ): AddSubscriptionBusiness {
            return withContext(ioDispatcher) {
                try {
                    val resultAddSubscription =
                        suspendCoroutine<AddSubscriptionBusiness> { continuation ->

                            val addSubscriptionBusinessRequest =
                                subscriptionBusiness.asSubscriptionRequest()
                            fireStore.collection(USERS_COLLECTION_PATH).document(uid)
                                .update(SUBSCRIPTION_BUSINESS_FIELD, FieldValue.arrayUnion(addSubscriptionBusinessRequest)).addOnSuccessListener {
                                    Timber.d("Subscription business is added successfully")
                                    continuation.resume(AddSubscriptionBusiness.success(true))
                                }.addOnFailureListener {
                                    continuation.resume(AddSubscriptionBusiness.error(R.string.add_subscription_business_failure))

                                    Timber.e("Add Subscription Business failure: $it")
                                }
                        }
                    resultAddSubscription
                } catch (e: UnknownHostException) {
                    AddSubscriptionBusiness.error(R.string.add_subscription_business_network)
                } catch (e: Exception) {
                    Timber.e("Exception while adding business: $e")
                    AddSubscriptionBusiness.error(R.string.add_subscription_business_failure)
                }
            }
        }

        override fun getSubscriptionsBusiness(): Flow<Resource<List<SubscriptionBusiness>>> {
           return callbackFlow<Resource<List<SubscriptionBusiness>>> {
                trySend(Resource.Loading)
                val listenerRegistration =
                    fireStore.collection(USERS_COLLECTION_PATH).document(auth.currentUserId)
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                Timber.e(error)
                                trySend(Resource.error(R.string.add_subscription_business_failure))
                                close()
                            }
                            @Suppress("UNCHECKED_CAST")
                            val subscriptionBusinessMap = snapshot?.get(
                                SUBSCRIPTION_BUSINESS_FIELD) as? List<Map<String, Any>>
                            Timber.e("subscriptionBusinessMap: $subscriptionBusinessMap")
                            if(subscriptionBusinessMap.isNullOrEmpty()){
                                trySend(Resource.empty())
                            }else{
                                trySend(Resource.success(asSubscriptionBusinessModel(subscriptionBusinessMap)))
                            }
                        }
                awaitClose {
                    listenerRegistration.remove()
                }
            }.flowOn(ioDispatcher)
        }
    }