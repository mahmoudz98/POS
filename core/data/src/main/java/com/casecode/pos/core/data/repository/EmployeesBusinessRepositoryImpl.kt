/*
 * Designed and developed 2024 by Mahmood Abdalhafeez
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.casecode.pos.core.data.repository

import com.casecode.pos.core.common.AppDispatchers.IO
import com.casecode.pos.core.common.Dispatcher
import com.casecode.pos.core.data.R
import com.casecode.pos.core.data.model.asEntityEmployees
import com.casecode.pos.core.data.model.asExternalEmployee
import com.casecode.pos.core.data.model.asExternalEmployees
import com.casecode.pos.core.data.utils.ensureUserExists
import com.casecode.pos.core.data.utils.ensureUserExistsOrReturnError
import com.casecode.pos.core.domain.repository.AuthRepository
import com.casecode.pos.core.domain.repository.EmployeesBusinessRepository
import com.casecode.pos.core.domain.repository.ResourceEmployees
import com.casecode.pos.core.domain.utils.AddEmployeeResult
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.firebase.services.EMPLOYEES_FIELD
import com.casecode.pos.core.firebase.services.FirestoreService
import com.casecode.pos.core.firebase.services.USERS_COLLECTION_PATH
import com.casecode.pos.core.model.data.users.Employee
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.UnknownHostException
import javax.inject.Inject

class EmployeesBusinessRepositoryImpl
@Inject
constructor(
    private val db: FirestoreService,
    private val auth: AuthRepository,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : EmployeesBusinessRepository {
    override fun getEmployees(): Flow<ResourceEmployees> = flow {
        emit(Resource.Loading)
        auth.ensureUserExistsOrReturnError<List<Employee>> {
            emit(it)
            return@flow
        }
        val uid = auth.currentUserId()
        db.listenToCollection(USERS_COLLECTION_PATH, uid).collect {
            @Suppress("UNCHECKED_CAST")
            val employeesMap =
                it.get(EMPLOYEES_FIELD) as? List<Map<String, Any>>
            if (employeesMap.isNullOrEmpty()) {
                emit(Resource.empty())
            } else {
                emit(Resource.success(employeesMap.asEntityEmployees()))
            }
        }
    }.catch { e ->
        Timber.e(e)
        emit(Resource.error(R.string.core_data_get_business_failure))
    }.flowOn(ioDispatcher)

    override suspend fun setEmployees(employees: List<Employee>): AddEmployeeResult {
        return withContext(ioDispatcher) {
            try {
                auth.ensureUserExists {
                    return@withContext AddEmployeeResult.Error(it)
                }
                val uid = auth.currentUserId()
                val employeesRequest = employees.asExternalEmployees()
                val isSuccess =
                    db.updateDocument(
                        USERS_COLLECTION_PATH,
                        uid,
                        employeesRequest as Map<String, Any>,
                    )
                if (isSuccess) {
                    AddEmployeeResult.Success
                } else {
                    AddEmployeeResult.Error(R.string.core_data_add_employees_business_failure)
                }
            } catch (_: UnknownHostException) {
                AddEmployeeResult.Error(R.string.core_data_add_employees_business_network)
            } catch (e: Exception) {
                Timber.e("Exception while adding employees: $e")
                AddEmployeeResult.Error(R.string.core_data_add_employees_business_failure)
            }
        }
    }

    override suspend fun addEmployee(employees: Employee): AddEmployeeResult {
        return withContext(ioDispatcher) {
            try {
                auth.ensureUserExists {
                    return@withContext AddEmployeeResult.Error(it)
                }
                val uid = auth.currentUserId()
                val employeesRequest = employees.asExternalEmployee()
                val isSuccess =
                    db.updateDocument(
                        USERS_COLLECTION_PATH,
                        uid,
                        mapOf(EMPLOYEES_FIELD to FieldValue.arrayUnion(employeesRequest)),
                    )
                if (isSuccess) {
                    AddEmployeeResult.Success
                } else {
                    AddEmployeeResult.Error(R.string.core_data_employee_add_business_failure)
                }
            } catch (_: UnknownHostException) {
                AddEmployeeResult.Error(R.string.core_data_employee_add_business_network)
            } catch (e: Exception) {
                Timber.e("Exception while adding employees: $e")
                AddEmployeeResult.Error(R.string.core_data_employee_add_business_failure)
            }
        }
    }

    override suspend fun deleteEmployee(employee: Employee): Resource<Int> {
        return withContext(ioDispatcher) {
            if (!auth.hasUser()) {
                return@withContext Resource.empty(message = R.string.core_data_uid_empty)
            }
            try {
                val currentUID = auth.currentUserId()
                val deleteEmployee =
                    mapOf(EMPLOYEES_FIELD to FieldValue.arrayRemove(employee.asExternalEmployee()))
                val isSuccess = db.updateDocument(USERS_COLLECTION_PATH, currentUID, deleteEmployee)
                if (isSuccess) {
                    Resource.success(R.string.core_data_employee_delete_business_success)
                } else {
                    Resource.error(R.string.core_data_employee_delete_business_failure)
                }
            } catch (_: UnknownHostException) {
                Resource.error(R.string.core_data_employee_delete_business_network)
            } catch (_: Exception) {
                Resource.error(R.string.core_data_employee_delete_business_failure)
            }
        }
    }

    override suspend fun updateEmployee(
        oldEmployee: Employee,
        newEmployee: Employee,
    ): Resource<Boolean> {
        return withContext(ioDispatcher) {
            if (!auth.hasUser()) {
                return@withContext Resource.empty(message = R.string.core_data_uid_empty)
            }
            try {
                val currentUID = auth.currentUserId()
                val isSuccessRemove =
                    db.updateDocument(
                        USERS_COLLECTION_PATH,
                        currentUID,
                        mapOf(
                            EMPLOYEES_FIELD to
                                FieldValue.arrayRemove(oldEmployee.asExternalEmployee()),
                        ),
                    )
                val isSuccessAdd =
                    db.updateDocument(
                        USERS_COLLECTION_PATH,
                        currentUID,
                        mapOf(
                            EMPLOYEES_FIELD to
                                FieldValue.arrayUnion(newEmployee.asExternalEmployee()),
                        ),
                    )
                if (isSuccessRemove && isSuccessAdd) {
                    Resource.success(true)
                } else {
                    Resource.error(R.string.core_data_employee_update_business_failure)
                }
            } catch (_: UnknownHostException) {
                Resource.error(R.string.core_data_employee_update_business_network)
            } catch (_: Exception) {
                Resource.error(R.string.core_data_employee_update_business_failure)
            }
        }
    }
}