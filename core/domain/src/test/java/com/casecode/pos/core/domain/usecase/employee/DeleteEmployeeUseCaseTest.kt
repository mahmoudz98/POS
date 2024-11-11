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

import com.casecode.pos.core.domain.usecase.DeleteEmployeeUseCase
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Employee
import com.casecode.pos.core.testing.repository.TestEmployeesBusinessRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DeleteEmployeeUseCaseTest {
    // Subject under test
    private val testEmployeesBusinessRepository: TestEmployeesBusinessRepository =
        TestEmployeesBusinessRepository()
    private val deleteEmployeeUseCase =
        DeleteEmployeeUseCase(testEmployeesBusinessRepository)

    @Test
    fun whenEmployeeDeleted_ReturnSuccess() = runTest {
        // Given
        val employee = Employee()
        // When
        val result = deleteEmployeeUseCase(employee)
        // Then
        assert(result is Resource.Success)
    }

    @Test
    fun whenHasError_returnError() = runTest {
        // Given
        val employee = Employee()
        testEmployeesBusinessRepository.setReturnError(true)
        // When
        val result = deleteEmployeeUseCase(employee)
        // Then
        assert(result is Resource.Error)
    }
}