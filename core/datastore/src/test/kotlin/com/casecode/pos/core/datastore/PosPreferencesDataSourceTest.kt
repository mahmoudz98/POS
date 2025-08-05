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

import com.casecode.pos.core.datastore.test.testSessionPreferencesDataStore
import com.casecode.pos.core.model.LoginStateResult
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
        subject =
            PosPreferencesDataSource(tmpFolder.testSessionPreferencesDataStore(testScope.backgroundScope))
    }

    @Test
    fun shouldGetNotSignInByDefault() = runTest {
        assertEquals(subject.loginData.first(), LoginStateResult.LoggedOut)
    }

    @Test
    fun setLoginWithAdmin_shouldUpdateLoginState() = runTest {
        subject.saveNewLoginSession(
            isOwner = true,
            isCompleteSetupBusiness = true,
            userId = "213",
            userName = "dfsf",
            businessId = "4erwerwe",
            activeBranchId = "sdfsd123",
            role = "Admin",
        )
        assertEquals(subject.loginData.first(), LoginStateResult.OwnerLoggedIn("4erwerwe", "sdfsd123"))
    }

    @Test
    fun setLoginWthAdmin_andRestLogin_shouldUpdateLoginState() = runTest {
        subject.saveNewLoginSession(
            isOwner = true,
            isCompleteSetupBusiness = true,
            userId = "213",
            userName = "dfsf",
            businessId = "4erwerwe",
            activeBranchId = "sdfsd123",
            role = "Admin",
        )
        subject.clearLoginSession()
        assertEquals(subject.loginData.first(), LoginStateResult.LoggedOut)
    }

    @Test
    fun setLoginByEmployee_shouldUpdateLoginState() = runTest {
        subject.saveNewLoginSession(
            isOwner = false,
            isCompleteSetupBusiness = true,
            userId = "213",
            userName = "dfsf",
            businessId = "4erwerwe",
            activeBranchId = "sdfsd123",
            role = "Admin",

        )

        assertEquals(
            subject.loginData.first(),
            LoginStateResult.EmployeeLoggedIn(
                businessId = "4erwerwe",
                activeBranchId = "sdfsd123",
                role = "admin",
            ),
        )
    }
}