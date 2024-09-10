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
import com.casecode.pos.core.domain.utils.AddEmployeeResult
import com.casecode.pos.core.model.data.users.Employee
import com.casecode.pos.core.testing.repository.TestEmployeesBusinessRepository
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class AddEmployeesBusinessUseCaseTest {
    // Given uid and employees
    private val employees = arrayListOf(Employee())

    // Subject under test
    private val testEmployeesBusinessRepository: TestEmployeesBusinessRepository =
        TestEmployeesBusinessRepository()
    private val addEmployeesBusinessUseCase: AddEmployeesBusinessUseCase =
        AddEmployeesBusinessUseCase(testEmployeesBusinessRepository)

    @Test
    fun addEmployees_shouldAddNewEmployees_returnResourceOfTrue() =
        runTest {
            // When
            val resultAddEmployeesBusiness = addEmployeesBusinessUseCase(employees)

            // Then
            assert(resultAddEmployeesBusiness is AddEmployeeResult.Success)
        }

    @Test
    fun `addEmployees when empty Business return resource with employees empty`() =
        runTest {
            // When subscription business fields is empty
            val resultEmptySubscriptionBusiness =
                addEmployeesBusinessUseCase(arrayListOf())

            // Then - return Resource of empty data.
            assertThat(
                resultEmptySubscriptionBusiness,
                `is`(AddEmployeeResult.Error(R.string.core_domain_employees_empty)),
            )
        }
}