package com.casecode.data.repository


import com.casecode.data.mapper.asEntitySubscriptions
import com.casecode.data.utils.AppDispatchers.IO
import com.casecode.data.utils.Dispatcher
import com.casecode.domain.model.subscriptions.Subscription
import com.casecode.domain.repository.SubscriptionsRepository
import com.casecode.domain.utils.Resource
import com.casecode.domain.utils.SUBSCRIPTIONS_COLLECTION_PATH
import com.casecode.domain.utils.SUBSCRIPTION_COST_FIELD
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import java.util.concurrent.CountDownLatch
import javax.inject.Inject

/**
 * Implementation of the [SubscriptionsRepository] interface.
 *
 * @param db The FirebaseFirestore instance.
 * @param ioDispatcher A dispatcher for IO-bound operations.
 */
class SubscriptionsRepositoryImpl @Inject constructor(
     private val db: FirebaseFirestore,
     @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
                                                     ) : SubscriptionsRepository
{
   
   /**
    * Gets the list of Subscription from the [FirebaseFirestore] database.
    *
    * @return A Flow of [Resource<List<Subscription>>] objects.
    */
   override fun getSubscriptions(): Flow<Resource<List<Subscription>>> =
      callbackFlow {
         trySend(Resource.Loading)
         
         val query =
            db.collection(SUBSCRIPTIONS_COLLECTION_PATH).orderBy(SUBSCRIPTION_COST_FIELD).get()
               .addOnSuccessListener { task ->
                  
                  mapToSubscriptions(task)
                  
               }.addOnFailureListener {
                  trySend(Resource.error(it.message ?: "Failure"))
                  Timber.e("getSubscriptions:Failure: $it")
                  close()
                  
               }
         awaitClose {
            query.isSuccessful
         }
         
      }.flowOn(ioDispatcher)
   
   /**
    * Gets the list of Subscription from the database.
    *
    * @param tasks The Task of getting the Subscription from the database.
    */
   private fun ProducerScope<Resource<List<Subscription>>>.mapToSubscriptions(tasks: QuerySnapshot)
   {
      val documents = tasks.documents
      val subscriptions = mutableListOf<Subscription>()
      val latch = countDownLatch(documents.size)
      
      documents.forEach { document ->
         
         subscriptions.asEntitySubscriptions(document)
         latch.countDown()
         
      }
      if (latch.count.toInt() == 0)
      {
         // All subCollection data has been fetched
         trySend(Resource.Success(subscriptions))
         Timber.i("subscriptions = $subscriptions")
         close()
      }
   }
   
   private fun countDownLatch(count: Int): CountDownLatch
   {
      return CountDownLatch(count)
   }
   
}