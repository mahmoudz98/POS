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

import androidx.annotation.StringRes
import com.casecode.pos.core.model.business.Branch
import com.casecode.pos.core.model.business.Employee

/**
 * The complete UI state for the employee feature screen, combining all sub-states.
 */
data class EmployeeUiState(
    val employees: List<Employee> = emptyList(),
    val isLoading: Boolean = false,
    val availableBranches: List<Branch> = emptyList(),
    val employeeSelected: Employee? = null,
    val dialogState: DialogState = DialogState.None,
    val isEmployeeCreatedSuccessfully: Boolean = false,
    @StringRes val userMessage: Int? = null,

)

sealed interface EmployeeEvent {
    object CompanyCodeOpened : EmployeeEvent
    object CompanyCodeClosed : EmployeeEvent
    object CreationEmployeeOpened : EmployeeEvent
    object NavigationBack : EmployeeEvent
    data class UpdatingEmployeeOpened(val employee: Employee) : EmployeeEvent
    object EmployeeFormClosed : EmployeeEvent
    data class DeletingEmployeeOpened(val employee: Employee) : EmployeeEvent
    object DeletingEmployeeClosed : EmployeeEvent
    object UserMessageShown : EmployeeEvent
}

enum class DialogState {
    None,
    CompanyCode,
    Creating,
    Updating,
    Deleting,
}
