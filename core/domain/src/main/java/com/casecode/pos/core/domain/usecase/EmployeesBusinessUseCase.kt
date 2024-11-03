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
package com.casecode.pos.core.domain.usecase

import com.casecode.pos.core.domain.R
import com.casecode.pos.core.domain.repository.EmployeesBusinessRepository
import com.casecode.pos.core.domain.utils.AddEmployeeResult
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Employee
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for adding a list of employees to the data store.
 *
 * This use case validates the input and interacts with the [EmployeesBusinessRepository]
 * to persist the employees. If the provided list is empty, an [AddEmployeeResult.Error] is returned.
 * Otherwise, the result of the repository operation is returned.
 *
 * @param employeesRepo The repository responsible for managing employees.
 */
class AddEmployeesBusinessUseCase
@Inject
constructor(
    private val employeesRepo: EmployeesBusinessRepository,
) {
    suspend operator fun invoke(employees: List<Employee>): AddEmployeeResult {
        if (employees.isEmpty()) {
            return AddEmployeeResult.Error(R.string.core_domain_employees_empty)
        }
        return employeesRepo.setEmployees(employees)
    }
}

/**
 * Use case for adding a new employee.
 *
 * This use case interacts with the [EmployeesBusinessRepository] to add a new [Employee] to the data source.
 *
 * @property employeesRepo The repository responsible for managing employee data.
 */
class AddEmployeeUseCase
@Inject
constructor(
    private val employeesRepo: EmployeesBusinessRepository,
) {
    suspend operator fun invoke(employee: Employee): AddEmployeeResult =
        employeesRepo.addEmployee(employee)
}

/**
 * Use case for retrieving a list of employees.
 *
 * This use case interacts with the [EmployeesBusinessRepository] to fetch employee data
 * and returns the result as a [Flow] of [Resource].
 *
 * @property employeesRepo The repository responsible for fetching employee data.
 */
class GetEmployeesBusinessUseCase
@Inject
constructor(
    private val employeesRepo: EmployeesBusinessRepository,
) {
    operator fun invoke(): Flow<Resource<List<Employee>>> = employeesRepo.getEmployees()
}

/**
 * Use case for updating an existing employee.
 *
 * This use case interacts with the [EmployeesBusinessRepository] to update an employee's information.
 *
 * @param employeesRepo The repository responsible for managing employee data.
 */
class UpdateEmployeesUseCase
@Inject
constructor(
    private val employeesRepo: EmployeesBusinessRepository,
) {
    suspend operator fun invoke(oldEmployee: Employee, newEmployee: Employee) =
        employeesRepo.updateEmployee(oldEmployee, newEmployee)
}

/**
 * Use case for deleting an employee.
 *
 * @param employeeRepo The repository responsible for employee data operations.
 */
class DeleteEmployeeUseCase
@Inject
constructor(
    private val employeeRepo: EmployeesBusinessRepository,
) {
    suspend operator fun invoke(employee: Employee) = employeeRepo.deleteEmployee(employee)
}