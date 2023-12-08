package com.casecode.data.repository

import com.casecode.data.model.toEmployeesRequest
import com.casecode.data.utils.AppDispatchers
import com.casecode.data.utils.Dispatcher
import com.casecode.domain.model.users.Employee
import com.casecode.domain.repository.AddEmployees
import com.casecode.domain.repository.EmployeesBusinessRepository
import com.casecode.domain.utils.Resource
import com.casecode.domain.utils.USERS_COLLECTION_PATH
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class EmployeesBusinessRepositoryImpl @Inject constructor(
     private val firstore: FirebaseFirestore,
     @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
                                                         )
   : EmployeesBusinessRepository
{
   override suspend fun getEmployees(uid: String): List<Employee>
   {
      TODO("Not yet implemented")
   }
   
   override suspend fun setEmployees(employees: ArrayList<Employee>, uid: String): AddEmployees
   {
   return withContext(ioDispatcher) {
      try
      {
         
         
         val resultAddEmployee = suspendCoroutine { continuation ->
               Timber.e("uid = $uid")
            val employeesRequest = employees.toEmployeesRequest()
            Timber.e("employeesRequest = $employeesRequest")
            
            firstore.collection(USERS_COLLECTION_PATH).document(uid)
               .update(employeesRequest as Map<String, Any>)
               .addOnSuccessListener {
                  Timber.d("employees is added successfully")
                  continuation.resume(Resource.Success(true))
               }.addOnFailureListener {
                  continuation.resume(Resource.Error(it))
                  
                  Timber.e("employees Failure: $it")
               }
            
         }
         resultAddEmployee
      }catch (e: Exception){
         Timber.e("Exception while adding employees: $e")
         Resource.Error(e)
      }
   }
   
   }
}