package com.casecode.pos.feature.employee

import android.util.Patterns
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import com.casecode.pos.core.designsystem.component.PosOutlinedTextField
import  com.casecode.pos.core.ui.R.string as uiString
import  com.casecode.pos.core.ui.R.array as uiArray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDialog(
    viewModel: EmployeeViewModel = hiltViewModel(),
    isUpdate: Boolean = false,
    onDismiss: () -> Unit,
) {
    val employeeUpdate = if (isUpdate) viewModel.employeeSelected.collectAsState() else null
    val branches = viewModel.branches.collectAsState()
    var name by remember { mutableStateOf(employeeUpdate?.value?.name ?: "") }
    var phone by remember { mutableStateOf(employeeUpdate?.value?.phoneNumber ?: "") }
    var password by remember { mutableStateOf(employeeUpdate?.value?.password ?: "") }
    var selectedBranch by remember { mutableStateOf(employeeUpdate?.value?.branchName ?: "") }
    var selectedPermission by remember { mutableStateOf(employeeUpdate?.value?.permission ?: "") }

    var nameError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf<Int?>(null) }
    var passwordError by remember { mutableStateOf<Int?>(null) }
    var branchError by remember { mutableStateOf(false) }
    var permissionError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(if (isUpdate) uiString.core_ui_update_employee_title else uiString.core_ui_add_employee_title)) },
        text = {
            Column {
                PosOutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = it.isBlank()
                    },
                    label = stringResource(uiString.core_ui_employee_name_hint),
                    isError = nameError,
                    supportingText = if (nameError) stringResource(uiString.core_ui_error_employee_name_empty) else null,
                    modifier = Modifier.fillMaxWidth(),
                )


                PosOutlinedTextField(
                    value = phone,
                    onValueChange = {
                        phone = it
                        phoneError =
                            if (it.isBlank()) uiString.core_ui_error_phone_empty else if (!it.matches(
                                    Patterns.PHONE.toRegex(),
                                )
                            ) uiString.core_ui_error_phone_invalid else null
                    },
                    label = stringResource(uiString.core_ui_work_phone_number_hint),
                    supportingText = phoneError?.let { stringResource(it) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next,
                    ),
                    isError = phoneError != null,
                    modifier = Modifier.fillMaxWidth(),
                )

                PosOutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError =
                            if (it.isBlank()) uiString.core_ui_error_add_employee_password_empty else if (it.length < 6) uiString.core_ui_error_add_employee_password else null
                    },
                    label = stringResource(uiString.core_ui_employee_password_hint),
                    supportingText = phoneError?.let { stringResource(it) },

                    visualTransformation = PasswordVisualTransformation(),
                    isError = passwordError != null,
                    modifier = Modifier.fillMaxWidth(),
                )


                // Branch Dropdown
                var branchExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = branchExpanded,
                    onExpandedChange = { branchExpanded = !branchExpanded },
                ) {
                    PosOutlinedTextField(
                        value = selectedBranch,
                        onValueChange = {},
                        readOnly = true,
                        isError = branchError,
                        supportingText =
                        if (branchError) stringResource(uiString.core_ui_error_add_employee_branch_empty) else null,

                        label = stringResource(uiString.core_ui_branch_name_hint),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = branchExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
                    )
                    ExposedDropdownMenu(
                        expanded = branchExpanded,
                        onDismissRequest = { branchExpanded = false },
                    ) {
                        branches.value.forEach { branch ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        branch.branchName,
                                    )
                                }, // Assuming 'name' is a property of Branch
                                onClick = {
                                    selectedBranch = branch.branchName
                                    branchExpanded = false
                                },
                            )
                        }
                    }
                }

                // Permission Dropdown (similar to Branch Dropdown)
                var permissionExpanded by remember { mutableStateOf(false) }
                val permissions = stringArrayResource(uiArray.core_ui_employee_permissions)
                ExposedDropdownMenuBox(
                    expanded = permissionExpanded,
                    onExpandedChange = { permissionExpanded = !permissionExpanded },
                ) {
                    PosOutlinedTextField(
                        value = selectedPermission,
                        onValueChange = {},
                        readOnly = true,

                        isError = permissionError,
                        label = stringResource(uiString.core_ui_permissions_text),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = permissionExpanded) },
                        supportingText = if (permissionError) stringResource(uiString.core_ui_error_add_employee_permission_empty) else null,

                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
                    )
                    ExposedDropdownMenu(
                        expanded = permissionExpanded,
                        onDismissRequest = { permissionExpanded = false },
                    ) {
                        permissions.forEach { permission ->
                            DropdownMenuItem(
                                text = { Text(permission) },
                                onClick = {
                                    selectedPermission = permission
                                    permissionExpanded = false
                                },
                            )
                        }
                    }
                }

            }
        },
        confirmButton = {
            Button(
                onClick = {

                    if (name.isEmpty() || phone.isEmpty() ||
                        !phone.matches(Patterns.PHONE.toRegex()) ||
                        password.isEmpty() || password.length < 6 ||
                        selectedBranch.isEmpty() || selectedPermission.isEmpty()
                    ) {
                        nameError = name.isEmpty()
                        phoneError = if (phone.isEmpty()) {
                            uiString.core_ui_error_phone_empty
                        } else if (!phone.matches(
                                Patterns.PHONE.toRegex(),
                            )
                        ) {
                            uiString.core_ui_error_phone_invalid
                        } else null
                        passwordError = if (password.isEmpty()) {
                            uiString.core_ui_error_add_employee_password_empty
                        } else if (password.length < 6) {
                            uiString.core_ui_error_add_employee_password
                        } else null
                        branchError = selectedBranch.isEmpty()
                        permissionError = selectedPermission.isEmpty()
                    } else {
                        if (isUpdate) {
                            viewModel.updateEmployee(
                                name,
                                phone,
                                password,
                                selectedBranch,
                                selectedPermission,
                            )
                        } else {
                            viewModel.addEmployee(
                                name,
                                phone,
                                password,
                                selectedBranch,
                                selectedPermission,
                            )
                        }
                        onDismiss()
                    }
                },
            ) {
                Text(stringResource(if (isUpdate) uiString.core_ui_update_employee_button_text else uiString.core_ui_add_employee_button_text))
            }
        },

        )
}