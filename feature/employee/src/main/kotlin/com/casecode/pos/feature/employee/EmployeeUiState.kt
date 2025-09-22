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
    val employeeSelected:Employee? = null,
    val dialogState: DialogState = DialogState.None,
    val isEmployeeCreatedSuccessfully: Boolean = false,
    @StringRes val userMessage: Int? = null,

    )

sealed interface EmployeeEvent {
    object CreationEmployeeOpened : EmployeeEvent
    data class UpdatingEmployeeOpened(val employee: Employee) : EmployeeEvent
    object EmployeeFormClosed : EmployeeEvent
    object DeletingEmployeeOpened : EmployeeEvent
    object DeletingEmployeeClosed : EmployeeEvent
    object UserMessageShown : EmployeeEvent
}

enum class DialogState {
    None, Creating, Updating, Deleting
}
