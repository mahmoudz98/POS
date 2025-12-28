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
package com.casecode.pos.feature.login.employee

import com.casecode.pos.feature.login.employee.R.string as featureString

data class LoginEmployeeFormState(
    val companyCode: String = "",
    val employeeId: String = "",
    val password: String = "",
    val companyCodeError: Int? = null,
    val employeeIdError: Int? = null,
    val passwordError: Int? = null,
) {
    fun validate(): LoginEmployeeFormState {
        return this.copy(
            companyCodeError = if (companyCode.isBlank()) featureString.feature_login_employee_login_error_uid_empty else null,
            employeeIdError = if (employeeId.isBlank()) featureString.feature_login_employee_error_name_empty else null,
            passwordError = if (password.isBlank()) featureString.feature_login_employee_error_password_empty else null,
        )
    }

    val isValid: Boolean
        get() = companyCode.isNotBlank() &&
            employeeId.isNotBlank() &&
            password.isNotBlank() &&
            companyCodeError == null &&
            employeeIdError == null &&
            passwordError == null
}

sealed interface LoginEmployeeFormEvent {
    data class CompanyCodeChanged(val value: String) : LoginEmployeeFormEvent
    data class EmployeeIdChanged(val value: String) : LoginEmployeeFormEvent
    data class PasswordChanged(val value: String) : LoginEmployeeFormEvent
    data object SubmitClicked : LoginEmployeeFormEvent
}
