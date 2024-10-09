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

class AddEmployeeUseCaseTest {
    private val testEmployeesBusinessRepository = TestEmployeesBusinessRepository()
    private val addEmployeeUseCase = AddEmployeeUseCase(testEmployeesBusinessRepository)

    @Test
    fun `addEmployee should return Success when employees are not empty`() = runTest {
        // Given
        val employees = Employee(
            name = "",
            phoneNumber = "",
            password = "",
            branchName = "",
            permission = "",
        )

        // When
        val result = addEmployeeUseCase(employees)

        // Then
        assert(result is AddEmployeeResult.Success)
    }

    @Test
    fun `addEmployee when has error return Error`() = runTest {
        // Given
        val employees = Employee()

        // When
        testEmployeesBusinessRepository.setReturnError(true)
        val result = addEmployeeUseCase(employees)

        // Then
        assert(result is AddEmployeeResult.Error)
    }
}