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

import com.casecode.pos.core.domain.utils.AddEmployeeResult
import com.casecode.pos.core.model.data.users.Employee
import com.casecode.pos.core.testing.repository.TestEmployeesBusinessRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AddEmployeesBusinessUseCaseTest {
    private val testEmployeesBusinessRepository = TestEmployeesBusinessRepository()
    private val addEmployeesBusinessUseCase = AddEmployeesBusinessUseCase(testEmployeesBusinessRepository)

    @Test
    fun `addEmployees when employees are not empty return Success`() = runTest {
        // Given
        val employees = arrayListOf(Employee())

        // When
        val result = addEmployeesBusinessUseCase(employees)

        // Then
        assert(result is AddEmployeeResult.Success)
    }

    @Test
    fun `addEmployees when employees are empty return Empty`() = runTest {
        // Given
        val employees = arrayListOf<Employee>()

        // When
        val result = addEmployeesBusinessUseCase(employees)

        // Then
        assert(result is AddEmployeeResult.Error)
    }

    @Test
    fun `addEmployees when employees are empty returns error`() = runTest {
        // Given
        val employees = arrayListOf<Employee>()

        // When
        testEmployeesBusinessRepository.setReturnError(true)
        val result = addEmployeesBusinessUseCase(employees)

        // Then
        assert(result is AddEmployeeResult.Error)
    }
}