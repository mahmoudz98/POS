package com.casecode.data.repository

import com.casecode.data.mapper.asEntityEmployees
import com.casecode.data.mapper.asExternalEmployee
import com.casecode.data.mapper.asExternalEmployees
import com.casecode.data.utils.AppDispatchers.IO
import com.casecode.data.utils.Dispatcher
import com.casecode.domain.model.users.Employee
import com.casecode.domain.repository.AddEmployees
import com.casecode.domain.repository.EmployeesBusinessRepository
import com.casecode.domain.utils.EMPLOYEES_FIELD
import com.casecode.domain.utils.Resource
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

class EmployeesBusinessRepositoryImpl
@Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: AuthService,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : EmployeesBusinessRepository {
    override  fun getEmployees(): Flow<Resource<List<Employee>>> = callbackFlow {
        trySend(Resource.Loading)
        val listenerRegistration = firestore.collection(USERS_COLLECTION_PATH).document(
            auth.currentUserId
        ).addSnapshotListener { snapshot, error ->
            if (error != null) {
                Timber.e(error)
                trySend(Resource.error(R.string.get_business_failure))
                close()
            }
           @Suppress("UNCHECKED_CAST") val employeesMap = snapshot?.get(EMPLOYEES_FIELD) as? List<Map<String, Any>>
            if(employeesMap.isNullOrEmpty()) {
                trySend(Resource.empty())
                close()
            }else {
                trySend(Resource.success(employeesMap.asEntityEmployees()))
            }
        }
        awaitClose {
            listenerRegistration.remove()
        }
    }.flowOn(ioDispatcher)


    override suspend fun setEmployees(
        employees: MutableList<Employee>,
        uid: String,
    ): AddEmployees {
        return withContext(ioDispatcher) {
            try {
                val resultAddEmployee = suspendCoroutine<AddEmployees> { continuation ->
                    val employeesRequest = employees.asExternalEmployees()

                    firestore.collection(USERS_COLLECTION_PATH).document(uid)
                        .update(employeesRequest as Map<String, Any>).addOnSuccessListener {
                            continuation.resume(Resource.Success(true))
                        }.addOnFailureListener {
                            continuation.resume(Resource.error(R.string.add_employees_business_failure))
                            Timber.e("employees Failure: $it")
                        }
                }
                resultAddEmployee
            } catch (e: UnknownHostException) {
                Resource.error(R.string.add_employees_business_network)
            } catch (e: Exception) {
                Timber.e("Exception while adding employees: $e")
                Resource.error(R.string.add_employees_business_failure)
            }
        }
    }

    override suspend fun addEmployee(employees: Employee): Resource<Boolean> {
        return withContext(ioDispatcher) {
            try {
                val resultAddEmployee = suspendCoroutine<AddEmployees> { continuation ->
                    val employeesRequest = employees.asExternalEmployee()

                    firestore.collection(USERS_COLLECTION_PATH).document(auth.currentUserId)
                        .update(EMPLOYEES_FIELD, FieldValue.arrayUnion(employeesRequest))
                        .addOnSuccessListener {
                            Timber.d("employees is added successfully")
                            continuation.resume(Resource.Success(true))
                        }.addOnFailureListener {
                            continuation.resume(Resource.error(R.string.employee_add_business_failure))

                            Timber.e("employees Failure: $it")
                        }
                }
                resultAddEmployee
            } catch (e: UnknownHostException) {
                Resource.error(R.string.employee_add_business_network)
            } catch (e: Exception) {
                Timber.e("Exception while adding employees: $e")
                Resource.error(R.string.employee_add_business_failure)
            }
        }
    }

    override suspend fun updateEmployee(
        employees: Employee,
        oldEmployee: Employee,
    ): Resource<Boolean> {
        return withContext(ioDispatcher) {
                suspendCoroutine {continuation->
                    val updatesEmployee = mapOf(
                        EMPLOYEES_FIELD to FieldValue.arrayRemove(oldEmployee.asExternalEmployee()),
                        EMPLOYEES_FIELD to FieldValue.arrayUnion(employees.asExternalEmployee()),
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
    }
}