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

import androidx.datastore.core.DataStore
import com.casecode.pos.core.model.data.EmployeeLoginData
import com.casecode.pos.core.model.data.LoginStateResult
import com.casecode.pos.core.model.data.permissions.Permission
import com.casecode.pos.core.model.data.permissions.toPermission
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import com.casecode.pos.core.model.data.users.Employee as EmployeeModel

/**
 * Data source for POS preferences, using DataStore to store and retrieve data.
 *
 * @param loginPreferences The DataStore instance used to store and retrieve login preferences.
 */
class PosPreferencesDataSource
@Inject
constructor(
    private val loginPreferences: DataStore<LoginPreferences>,
) {
    val currentUid =
        loginPreferences.data.map {
            it.uid
        }
    val currentNameLogin =
        loginPreferences.data.map {
            when (it.authState) {
                AuthState.LOGIN_ADMIN -> {
                    AuthState.LOGIN_ADMIN.name
                }

                AuthState.LOGIN_EMPLOYEE -> {
                    it.employee.name
                }

                else -> ""
            }
        }
    val loginData =
        loginPreferences.data.map {
            when (it.authState) {
                AuthState.LOGIN_ADMIN -> {
                    LoginStateResult.SuccessLoginAdmin(it.uid)
                }

                AuthState.LOGIN_EMPLOYEE -> {
                    LoginStateResult.EmployeeLogin(
                        EmployeeLoginData(
                            it.employee.name,
                            it.uid,
                            it.employee.phoneNumber,
                            it.employee.password,
                            it.employee.branchName,
                            it.employee.permission.toPermission() ?: Permission.NONE,
                        ),
                    )
                }

                AuthState.NOT_COMPLETE_SETUP_BUSINESS -> {
                    LoginStateResult.NotCompleteBusiness(it.uid)
                }

                AuthState.NONE, null -> LoginStateResult.NotSignIn
                AuthState.UNRECOGNIZED -> LoginStateResult.Error
            }
        }

    suspend fun setLoginWithAdmin(
        uid: String,
        isCompleteSetupBusiness: Boolean,
    ) {
        try {
            loginPreferences.updateData {
                it.copy {
                    authState =
                        if (isCompleteSetupBusiness) {
                            AuthState.LOGIN_ADMIN
                        } else {
                            AuthState.NOT_COMPLETE_SETUP_BUSINESS
                        }

                    this.uid = uid
                }
            }
        } catch (ioException: IOException) {
            Timber.e("Failed to update login preference: $ioException")
        }
    }

    suspend fun setLoginByEmployee(
        employee: EmployeeModel,
        uid: String,
    ) {
        try {
            loginPreferences.updateData {
                it.copy {
                    authState = AuthState.LOGIN_EMPLOYEE
                    this.uid = uid
                    this.employee =
                        employee {
                            this.name = employee.name
                            this.password = employee.password ?: ""
                            this.branchName = employee.branchName ?: ""
                            this.phoneNumber = employee.phoneNumber
                            this.permission = employee.permission
                        }
                }
            }
        } catch (ioEx: IOException) {
            Timber.e("Failed to update login preference with employee: $ioEx")
        }
    }

    suspend fun restLogin() {
        try {
            loginPreferences.updateData {
                it.toBuilder()
                    .clearUid()
                    .clearEmployee()
                    .clearAuthState()
                    .build()
            }
        } catch (ioEx: IOException) {
            Timber.e("Failed to update rest login preference: $ioEx")
        }
    }
}