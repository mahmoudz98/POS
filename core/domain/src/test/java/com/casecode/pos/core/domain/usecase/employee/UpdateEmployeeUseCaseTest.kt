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
package com.casecode.pos.core.domain.usecase.employee

import com.casecode.pos.core.domain.usecase.UpdateEmployeesUseCase
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Employee
import com.casecode.pos.core.testing.repository.TestEmployeesBusinessRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class UpdateEmployeeUseCaseTest {
    private val testEmployeesBusinessRepository = TestEmployeesBusinessRepository()
    private val updateEmployeeUseCase = UpdateEmployeesUseCase(testEmployeesBusinessRepository)

    @Test
    fun `when has error return Error`() = runTest {
        val oldEmployee = Employee()
        val newEmployee =
            Employee(
                name = "new name",
                phoneNumber = "1212",
                password = "12333",
                branchName = "new branch",
                permission = "sales",
            )
        // When
        testEmployeesBusinessRepository.setReturnError(true)
        val result = updateEmployeeUseCase(oldEmployee, newEmployee)
        // Then
        assert(result is Resource.Error)
    }

    @Test
    fun `when has success return Success`() = runTest {
        val oldEmployee = Employee()
        val newEmployee =
            Employee(
                name = "new name",
                phoneNumber = "1212",
                password = "12333",
                branchName = "new branch",
                permission = "sales",
            )
        // When
        val result = updateEmployeeUseCase(oldEmployee, newEmployee)
        // Then
        assert(result is Resource.Success)
    }
}