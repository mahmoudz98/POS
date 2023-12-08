package com.casecode.data.repository


import com.casecode.data.model.asEntitySubscriptions
import com.casecode.data.utils.AppDispatchers
import com.casecode.data.utils.Dispatcher
import com.casecode.domain.model.subscriptions.Subscription
import com.casecode.domain.repository.SubscriptionsRepository
import com.casecode.domain.utils.Resource
import com.casecode.domain.utils.SUBSCRIPTIONS_COLLECTION_PATH
import com.casecode.domain.utils.SUBSCRIPTION_COST_FIELD
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

/**
 * Implementation of the PlansRepository interface.
 *
 * @param db The FirebaseFirestore instance.
 * @param ioDispatcher A dispatcher for IO-bound operations.
 */
class SubscriptionsRepositoryImpl @Inject constructor(
     private val db: FirebaseFirestore,
     
     @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
                                                     ) : SubscriptionsRepository
{
   
   /**
    * Gets the list of Subscription from the database.
    *
    * @return A Flow of [Resource<List<Subscription>>] objects.
    */
   override fun getSubscriptions(): Flow<Resource<List<Subscription>>> = callbackFlow {
      trySend(Resource.Loading())
      val callback =
         db.collection(SUBSCRIPTIONS_COLLECTION_PATH).orderBy(SUBSCRIPTION_COST_FIELD).get()
            .addOnCompleteListener { task ->
               if (task.isSuccessful)
               {
                  
                  getSubscriptions(task)
               } else
               {
                  Timber.e("getSubscriptions:exception: ${task.exception}")
                  trySend(Resource.Error(task.exception))
                  close()
               }
            }.addOnFailureListener {
               trySend(Resource.Error(it))
               Timber.e("getSubscriptions:Failure: $it")
               
            }
      awaitClose {
         callback.isCanceled
         
      }
   }.flowOn(ioDispatcher)
   
   /**
    * Gets the list of Subscription from the database.
    *
    * @param tasks The Task of getting the Subscription from the database.
    */
   private fun ProducerScope<Resource<List<Subscription>>>.getSubscriptions(tasks: Task<QuerySnapshot>)
   {
      val documents = tasks.result.documents
      val subscriptions = mutableListOf<Subscription>()
      val remainingDocuments = AtomicInteger(documents.size)
      
      documents.forEach { document ->
         
         subscriptions.asEntitySubscriptions(document)
         
         if (remainingDocuments.decrementAndGet() == 0)
         {
            // All subCollection data has been fetched
            trySendBlocking(Resource.Success(subscriptions))
            Timber.e("subscriptions = $subscriptions")
            close()
         }
         
      }
      
   }
   
   
}