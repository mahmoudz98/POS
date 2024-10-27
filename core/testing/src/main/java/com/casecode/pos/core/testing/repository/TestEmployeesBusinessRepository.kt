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
package com.casecode.pos.core.testing.repository

import com.casecode.pos.core.data.R
import com.casecode.pos.core.domain.repository.EmployeesBusinessRepository
import com.casecode.pos.core.domain.utils.AddEmployeeResult
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Employee
import com.casecode.pos.core.testing.base.BaseTestRepository
import com.casecode.pos.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.junit.Rule
import javax.inject.Inject

class TestEmployeesBusinessRepository
@Inject
constructor() :
    BaseTestRepository(),
    EmployeesBusinessRepository {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    override fun init() {}

    val fakeEmployees =
        listOf(
            Employee("Mahmoud", "1018867266", "123213", "branch1", "Sale"),
            Employee("Ahmed", "22323", "123231213", "branch2", "Admin"),
        )

    override fun getEmployees(): Flow<Resource<List<Employee>>> {
        return flow<Resource<List<Employee>>> {
            if (shouldReturnError) return@flow emit(Resource.error(R.string.core_data_get_business_failure))
            if (shouldReturnEmpty) return@flow emit(Resource.empty())
            emit(Resource.success(fakeEmployees))
        }
    }

    override suspend fun setEmployees(employees: List<Employee>): AddEmployeeResult {
        if (shouldReturnError) {
            return AddEmployeeResult.Error(-1)
        }
        return AddEmployeeResult.Success
    }

    override suspend fun addEmployee(employees: Employee): AddEmployeeResult {
        if (shouldReturnError) {
            return AddEmployeeResult.Error(R.string.core_data_add_employees_business_failure)
        }
        return AddEmployeeResult.Success
    }

    override suspend fun deleteEmployee(employee: Employee): Resource<Int> {
        if (shouldReturnError) {
            return Resource.error(R.string.core_data_employee_delete_business_failure)
        }
        return Resource.success(R.string.core_data_employee_delete_business_success)
    }

    override suspend fun updateEmployee(
        employees: Employee,
        oldEmployee: Employee,
    ): Resource<Boolean> {
        if (shouldReturnError) return Resource.error(R.string.core_data_employee_update_business_failure)
        return Resource.success(true)
    }
}