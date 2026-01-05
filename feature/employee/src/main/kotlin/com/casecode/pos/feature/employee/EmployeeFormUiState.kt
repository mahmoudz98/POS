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
package com.casecode.pos.feature.employee

import com.casecode.pos.core.model.business.EmployeeRole
import com.casecode.pos.core.ui.utils.validatePassword
import com.casecode.pos.core.ui.R.string as uiString

internal data class FormErrors(
    val idError: Int? = null,
    val nameError: Int? = null,
    val phoneError: Int? = null,
    val passwordError: Int? = null,
    val assignedBranchesError: Int? = null,
)

/**
 * Represents the state of the employee creation form/dialog.
 * Adheres to SRP by only containing state related to adding a form employee.
 */
internal data class EmployeeFormUiState(
    val employeeId: String = "",
    val isAutoGenerateId: Boolean = true,
    val name: String = "",
    val phone: String = "",
    val password: String = "",
    val role: EmployeeRole = EmployeeRole.CASHIER,
    val assignedBranchId: String = "",
    val formErrors: FormErrors = FormErrors(),
) {
    fun validate(isUpdate: Boolean): FormErrors {
        val idError = when {
            !isUpdate && employeeId.isBlank() -> uiString.core_ui_error_employee_id_empty
            !isUpdate && !isAutoGenerateId ->
                uiString.core_ui_error_employee_id_duplicate
            else -> null
        }
        val nameError =
            if (name.isBlank()) uiString.core_ui_error_employee_name_empty else null
        val phoneError =
            if (phone.isEmpty()) uiString.core_ui_error_phone_empty else null

        val passwordError = if (isUpdate && password.isBlank()) null else validatePassword(password)
        val assignedBranchesError =
            if (assignedBranchId.isEmpty()) uiString.core_ui_error_branch_name_empty else null

        return FormErrors(
            idError = idError,
            nameError = nameError,
            phoneError = phoneError,
            passwordError = passwordError,
            assignedBranchesError = assignedBranchesError,
        )
    }

    fun isValid(): Boolean {
        return formErrors.run {
            this.nameError == null && this.passwordError == null &&
                this.phoneError == null && this.assignedBranchesError == null
        }
    }
}

sealed interface EmployeeFormEvent {
    data class EmployeeIdChanged(val id: String) : EmployeeFormEvent
    data class AutoGenerateIdToggled(val isEnabled: Boolean) : EmployeeFormEvent
    data class EmployeeNameChanged(val name: String) : EmployeeFormEvent
    data class EmployeePhoneChanged(val phone: String) : EmployeeFormEvent
    data class EmployeePasswordChanged(val password: String) : EmployeeFormEvent
    data class EmployeeRoleChanged(val role: EmployeeRole) : EmployeeFormEvent
    data class EmployeeBranchAssigned(val branchId: String) : EmployeeFormEvent
    object SaveClicked : EmployeeFormEvent
}
