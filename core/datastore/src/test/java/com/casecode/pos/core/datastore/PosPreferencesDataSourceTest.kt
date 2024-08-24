package com.casecode.pos.core.datastore

import com.casecode.pos.core.datastore.test.testLoginPreferencesDataStore
import com.casecode.pos.core.model.data.EmployeeLoginData
import com.casecode.pos.core.model.data.LoginStateResult
import com.casecode.pos.core.model.data.permissions.Permission
import com.casecode.pos.core.model.data.users.Employee
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
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
            println(" coroutineContext: $coroutineContext")
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
            subject.setLoginWithAdmin("123", true)
            subject.restLogin()

            assertEquals(subject.loginData.last(), LoginStateResult.NotSignIn)
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