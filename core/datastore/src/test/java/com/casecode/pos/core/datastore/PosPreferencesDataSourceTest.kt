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
package com.casecode.pos.core.datastore

import com.casecode.pos.core.datastore.test.testLoginPreferencesDataStore
import com.casecode.pos.core.model.data.EmployeeLoginData
import com.casecode.pos.core.model.data.LoginStateResult
import com.casecode.pos.core.model.data.permissions.Permission
import com.casecode.pos.core.model.data.users.Employee
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import kotlin.test.assertEquals

class PosPreferencesDataSourceTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    private val testScope = TestScope(testDispatcher)

    private lateinit var subject: PosPreferencesDataSource

    @get:Rule
    val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

    @Before
    fun setup() {
        subject = PosPreferencesDataSource(tmpFolder.testLoginPreferencesDataStore(testScope))
    }

    @Test
    fun shouldGetNotSignInByDefault() =
        runTest {
            assertEquals(subject.loginData.first(), LoginStateResult.NotSignIn)
        }

    @Test
    fun setLoginWithAdmin_shouldUpdateLoginState() =
        runTest {
            subject.setLoginWithAdmin("123", true)
            assertEquals(subject.loginData.first(), LoginStateResult.SuccessLoginAdmin("123"))
        }

    @Test
    fun setLoginWthAdmin_andRestLogin_shouldUpdateLoginState() =
        runTest {
            subject.setLoginWithAdmin("dgdfgdfg3434", true)
            subject.restLogin()
            assertEquals(subject.loginData.first(), LoginStateResult.NotSignIn)
        }

    @Test
    fun setLoginByEmployee_shouldUpdateLoginState() =
        runTest {
            subject.setLoginByEmployee(
                Employee(
                    name = "Mahmoud",
                    phoneNumber = "(+20) 586-5192",
                    password = "password",
                    branchName = "branch",
                    permission = "admin",
                ),
                "uid",
            )
            assertEquals(
                subject.loginData.first(),
                LoginStateResult.EmployeeLogin(
                    EmployeeLoginData(
                        name = "Mahmoud",
                        uid = "uid",
                        phoneNumber = "(+20) 586-5192",
                        password = "password",
                        branch = "branch",
                        permission = Permission.ADMIN,
                    ),
                ),
            )
        }
}