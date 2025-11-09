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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.PosOutlinedTextField
import com.casecode.pos.core.designsystem.component.PosTextButton
import com.casecode.pos.core.model.business.Branch
import com.casecode.pos.core.model.business.EmployeeRole
import com.casecode.pos.core.ui.business.toDisplayString
import com.casecode.pos.core.ui.R.string as uiString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EmployeeFormDialog(
    viewModel: EmployeeViewModel = hiltViewModel(),
    isUpdate: Boolean = false,
    onDismiss: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val addEmployeeUiState by viewModel.employeeFormUiState.collectAsStateWithLifecycle()
    EmployeeFormDialog(
        isUpdate = isUpdate,
        uiState = addEmployeeUiState,
        branches = uiState.availableBranches,
        onEvent = viewModel::onEventEmployeeForm,
        onDismiss = onDismiss,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EmployeeFormDialog(
    isUpdate: Boolean,
    uiState: EmployeeFormUiState,
    branches: List<Branch>,
    onEvent: (EmployeeFormEvent) -> Unit,
    onDismiss: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    AlertDialog(
        onDismissRequest = {
            focusRequester.freeFocus()
            keyboardController?.hide()
            onDismiss()
        },
        title = {
            Text(
                stringResource(
                    if (isUpdate) {
                        uiString.core_ui_update_employee_title
                    } else {
                        uiString.core_ui_add_employee_title
                    },
                ),
            )
        },
        text = {
            EmployeeDialogContent(
                name = uiState.name,
                onNameChange = {
                    onEvent(EmployeeFormEvent.EmployeeNameChanged(it))
                },
                hasNameError = uiState.formErrors.nameError,
                password = uiState.password,
                onPasswordChange = {
                    onEvent(EmployeeFormEvent.EmployeePasswordChanged(it))
                },
                hasPasswordError = uiState.formErrors.passwordError,
                phone = uiState.phone,
                onPhoneChange = {
                    onEvent(EmployeeFormEvent.EmployeePhoneChanged(it))
                },
                hasPhoneError = uiState.formErrors.phoneError,
                branches = branches,
                selectedBranchId = uiState.assignedBranchId,
                onSelectedBranchChange = {
                    onEvent(EmployeeFormEvent.EmployeeBranchAssigned(it))
                },
                branchError = uiState.formErrors.assignedBranchesError,
                selectedRole = uiState.role,
                onSelectedRoleChange = {
                    onEvent(EmployeeFormEvent.EmployeeRoleChanged(it))
                },
                focusRequester = focusRequester,
            )
        },
        confirmButton = {
            PosTextButton(
                onClick = {
                    onEvent(EmployeeFormEvent.SaveClicked)
                    keyboardController?.hide()
                },
            ) {
                Text(
                    stringResource(
                        if (isUpdate) {
                            uiString.core_ui_update_employee_button_text
                        } else {
                            uiString.core_ui_add_employee_button_text
                        },
                    ),
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmployeeDialogContent(
    name: String,
    onNameChange: (String) -> Unit,
    hasNameError: Int?,
    password: String,
    onPasswordChange: (String) -> Unit,
    hasPasswordError: Int?,
    phone: String,
    onPhoneChange: (String) -> Unit,
    hasPhoneError: Int?,
    branches: List<Branch>,
    selectedBranchId: String,
    onSelectedBranchChange: (String) -> Unit,
    branchError: Int?,
    selectedRole: EmployeeRole,
    onSelectedRoleChange: (EmployeeRole) -> Unit,
    focusRequester: FocusRequester,
) {
    var rolesExpanded by remember { mutableStateOf(false) }
    var branchExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        PosOutlinedTextField(
            value = name,
            onValueChange = { onNameChange(it) },
            label = stringResource(uiString.core_ui_employee_name_hint),
            isError = hasNameError != null,
            keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            ),
            supportingText = hasNameError?.let { stringResource(it) },
            modifier =
            Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
        )
        PosOutlinedTextField(
            value = password,
            onValueChange = { onPasswordChange(it) },
            label = stringResource(uiString.core_ui_employee_password_hint),
            supportingText = hasPasswordError?.let { stringResource(it) },
            visualTransformation = PasswordVisualTransformation(),
            isError = hasPasswordError != null,
            modifier = Modifier.fillMaxWidth(),
        )
        PosOutlinedTextField(
            value = phone,
            onValueChange = { onPhoneChange(it) },
            label = stringResource(uiString.core_ui_work_phone_number_hint),
            supportingText = hasPhoneError?.let { stringResource(it) },
            isError = hasPhoneError != null,
            modifier = Modifier.fillMaxWidth(),
        )
        ExposedDropdownMenuBox(
            expanded = rolesExpanded,
            onExpandedChange = { rolesExpanded = !rolesExpanded },
        ) {
            PosOutlinedTextField(
                value = selectedRole.toDisplayString(),
                onValueChange = {},
                readOnly = true,
                label = stringResource(uiString.core_ui_employee_role_text),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = rolesExpanded,
                    )
                },
                modifier =
                Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
            )
            ExposedDropdownMenu(
                expanded = rolesExpanded,
                onDismissRequest = { rolesExpanded = false },
            ) {
                EmployeeRole.entries.forEach { role ->
                    DropdownMenuItem(
                        text = { Text(role.toDisplayString()) },
                        onClick = {
                            onSelectedRoleChange(role)
                            rolesExpanded = false
                        },
                    )
                }
            }
        }
        ExposedDropdownMenuBox(
            expanded = branchExpanded,
            onExpandedChange = { branchExpanded = !branchExpanded },
        ) {
            PosOutlinedTextField(
                value = branches.find { it.id == selectedBranchId }?.name ?: "",
                onValueChange = {},
                readOnly = true,
                isError = branchError != null,
                supportingText = branchError?.let { stringResource(it) },
                label = stringResource(uiString.core_ui_branch_name_hint),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = branchExpanded,
                    )
                },
                modifier =
                Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
            )
            ExposedDropdownMenu(
                expanded = branchExpanded,
                onDismissRequest = { branchExpanded = false },
            ) {
                branches.forEach { branch ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                branch.name,
                            )
                        },
                        onClick = {
                            onSelectedBranchChange(branch.id)
                            branchExpanded = false
                        },
                    )
                }
            }
        }
    }
}
