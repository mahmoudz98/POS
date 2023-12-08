package com.casecode.data.repository

import com.casecode.data.model.toBusinessRequest
import com.casecode.data.utils.AppDispatchers
import com.casecode.data.utils.Dispatcher
import com.casecode.domain.model.users.Business
import com.casecode.domain.repository.AddBusiness
import com.casecode.domain.repository.BusinessRepository
import com.casecode.domain.utils.BRANCHES_COLLECTION_PATH
import com.casecode.domain.utils.BUSINESS_EMAIL_FIELD
import com.casecode.domain.utils.BUSINESS_FIELD
import com.casecode.domain.utils.BUSINESS_PHONE_NUMBER_FIELD
import com.casecode.domain.utils.BUSINESS_STORE_TYPE_FIELD
import com.casecode.domain.utils.CUSTOMERS_COLLECTION_PATH
import com.casecode.domain.utils.Resource
import com.casecode.domain.utils.USERS_COLLECTION_PATH
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BusinessRepositoryImpl @Inject constructor(
     private val firestore: FirebaseFirestore,
     @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
                                                ) : BusinessRepository
{
   override suspend fun getBusiness(uid: String): Business
   {
      TODO("Not yet implemented")
   }
   
   suspend fun setBusinessOld(business: Business, uid: String): AddBusiness
   {
      
      return withContext(ioDispatcher) {
         try
         {
            if (uid.isBlank())
            {
               return@withContext Resource.Success(false)
            }
            
            Timber.i("currentCoroutineContext: ${currentCoroutineContext()}")
            
            val data = hashMapOf(
               BUSINESS_STORE_TYPE_FIELD to business.storeType,
               BUSINESS_EMAIL_FIELD to business.email,
               BUSINESS_PHONE_NUMBER_FIELD to business.phone
                                
                                )
            firestore.collection(CUSTOMERS_COLLECTION_PATH).document(uid).collection(
               BUSINESS_FIELD).add(data).addOnSuccessListener {
               Timber.e("customer success")
               business.branches.forEach { branch ->
                  it.collection(BRANCHES_COLLECTION_PATH).document().set(branch)
               }
               
            }.await()
            Resource.Success(true)
            
         } catch (e: Exception)
         {
            Timber.e("Error: ${e.message}")
            Resource.Error(e)
            
         }
      }
   }
   
   override suspend fun setBusiness(business: Business, uid: String): AddBusiness
   {
      
      return withContext(ioDispatcher) {
         try
         {
            // Use suspendCoroutine to handle the asynchronous Firestore operation
            val result = suspendCoroutine { continuation ->
               
               // Create a map containing the business data
               val businessRequest = business.toBusinessRequest()
               firestore.collection(USERS_COLLECTION_PATH).document(uid)
                  .update(businessRequest as Map<String, Any>).addOnSuccessListener {
                     Timber.d("Business is added successfully")
                     continuation.resume(Resource.Success(true))
                  }.addOnFailureListener {
                     continuation.resume(Resource.Error(it))
                     
                     Timber.e("Business Failure: $it")
                  }
            }
            result
         } catch (e: Exception)
         {
            Timber.e("Exception while adding business: $e")
            Resource.Error(e)
         }
      }
      
   }
   
   
}