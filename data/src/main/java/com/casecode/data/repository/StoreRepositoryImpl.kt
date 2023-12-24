package com.casecode.data.repository

import com.casecode.domain.model.users.Item
import com.casecode.data.utils.AppDispatchers.IO
import com.casecode.data.utils.Dispatcher
import com.casecode.domain.model.stores.Store
import com.casecode.domain.repository.StoreRepository
import com.casecode.domain.repository.StoresResponse
import com.casecode.domain.utils.BASICITEMS_COLLECTION_PATH
import com.casecode.domain.utils.Resource
import com.casecode.domain.utils.STORECODE_FIELD
import com.casecode.domain.utils.STORES_COLLECTION_PATH
import com.casecode.domain.utils.STORETYPE_FIELD
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

class StoreRepositoryImpl @Inject constructor(
     private val firestore: FirebaseFirestore,
     @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
                                             ) : StoreRepository
{
   
   
   override fun getStores(): Flow<StoresResponse> = callbackFlow<StoresResponse> {
      trySend(Resource.loading())
      val callback =
         firestore.collection(STORES_COLLECTION_PATH).get().addOnCompleteListener { tasks ->
            if (tasks.isSuccessful)
            {
               getStoresAndBasicItems(tasks)
               
            } else
            {
               Timber.e("getStores: ${tasks.exception}")
               trySend(Resource.error(tasks.exception?.message!!))
               close()
            }
         }
      awaitClose {
         Timber.e("awitClose = ${callback.isComplete}")
         
         callback.isCanceled
      }
      
   }.flowOn(ioDispatcher)
   
   private fun ProducerScope<StoresResponse>.getStoresAndBasicItems(
        tasks: Task<QuerySnapshot>,
                                                                   )
   {
      val documents = tasks.result.documents
      val stores = mutableListOf<Store>()
      val remainingDocuments =
         AtomicInteger(documents.size) // Use AtomicInteger to track remaining documents
      
      documents.forEach { document ->
         val basicItems = mutableListOf<Item>()
         document.reference.collection(BASICITEMS_COLLECTION_PATH).get()
            .addOnSuccessListener { collections ->
               collections.documents.forEach {
                  try
                  {
                     // TODO: when document is not have any object to convert, error in runtime
                     val basicItem = it.toObject(Item::class.java)
                     if (basicItem != null)
                     {
                        basicItems.add(basicItem)
                     }
                  } catch (e: RuntimeException)
                  {
                     Timber.e(e)
                  }
                  
               }
               
               // Add the Store object to the list
               val storeCode = document.get(STORECODE_FIELD) as Long?
               val storeType = document.get(STORETYPE_FIELD) as String?
               stores.add(
                  Store(
                     basicItems = basicItems,
                     storeCode = storeCode,
                     storeType = storeType,
                       ),
                         )
               
               if (remainingDocuments.decrementAndGet() == 0)
               {
                  // All subCollection data has been fetched
                  trySendBlocking(Resource.Success(stores))
                  Timber.e("store = $stores")
                  close()
               }
            }
         
      }
   }
   
   
}