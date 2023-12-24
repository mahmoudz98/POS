package com.casecode.data.repository

import com.casecode.data.model.asSubscriptionRequest
import com.casecode.data.utils.AppDispatchers.IO
import com.casecode.data.utils.Dispatcher
import com.casecode.domain.model.users.SubscriptionBusiness
import com.casecode.domain.repository.AddSubscriptionBusiness
import com.casecode.domain.repository.SubscriptionsBusinessRepository
import com.casecode.domain.utils.Resource
import com.casecode.domain.utils.USERS_COLLECTION_PATH
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SubscriptionsBusinessRepositoryImpl @Inject constructor(private val fireStore: FirebaseFirestore,
   @Dispatcher(IO)private val ioDispatcher: CoroutineDispatcher):SubscriptionsBusinessRepository
{
   override suspend fun setSubscriptionBusiness(
        subscriptionBusiness: SubscriptionBusiness,
        uid: String,
                                               ): AddSubscriptionBusiness
   {
      if (uid.isBlank())
      {
         return Resource.error("can't find uid")
      }
      
      return withContext(ioDispatcher) {
         try
         {
            val resultAddSubscription = suspendCoroutine<AddSubscriptionBusiness> { continuation ->
               val addSubscriptionBusinessRequest = subscriptionBusiness.asSubscriptionRequest()
               fireStore.collection(USERS_COLLECTION_PATH).document(uid)
                  .update(addSubscriptionBusinessRequest).addOnSuccessListener {
                     Timber.d("Subscription business is added successfully")
                     continuation.resume(Resource.Success(true))
                  }.addOnFailureListener {
                     continuation.resume(Resource.error(it.message!!))
                     
                     Timber.e("Subscription Business Failure: $it")
                  }
            }
            resultAddSubscription
            
         } catch (e: Exception)
         {
            Timber.e("Exception while adding business: $e")
            Resource.error(e.message!!)
         }
      }
   }
   
   override fun getSubscriptionsBusiness(): Flow<Resource<List<SubscriptionBusiness>>>
   {
      TODO("Not yet implemented")
   }
}