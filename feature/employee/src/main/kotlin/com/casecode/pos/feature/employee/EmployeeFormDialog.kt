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
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosOutlinedTextField
import com.casecode.pos.core.designsystem.component.PosTextButton
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.business.Branch
import com.casecode.pos.core.model.business.EmployeeRole
import com.casecode.pos.core.ui.TrackScreenViewEvent
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
    TrackScreenViewEvent(screenName = if (isUpdate) "EmployeeUpdateDialog" else "EmployeeCreateDialog")
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
                uiState = uiState,
                isUpdate = isUpdate,
                branches = branches,
                onEvent = onEvent,
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
    uiState: EmployeeFormUiState,
    isUpdate: Boolean,
    onEvent: (EmployeeFormEvent) -> Unit,
    branches: List<Branch>,
    focusRequester: FocusRequester,
) {
    var rolesExpanded by remember { mutableStateOf(false) }
    var branchExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        PosOutlinedTextField(
            value = uiState.employeeId,
            enabled = uiState.isAutoGenerateId && !isUpdate,
            onValueChange = { onEvent(EmployeeFormEvent.EmployeeIdChanged(it)) },
            label = stringResource(uiString.core_ui_employee_id_hint),
            isError = uiState.formErrors.idError != null,
            keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next,
            ),
            supportingText = uiState.formErrors.idError?.let { stringResource(it) },
            trailingIcon = {
                Checkbox(
                    checked = uiState.isAutoGenerateId,
                    enabled = !isUpdate,
                    onCheckedChange = { onEvent(EmployeeFormEvent.AutoGenerateIdToggled(it)) },
                )
            },
            modifier =
            Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
        )
        PosOutlinedTextField(
            value = uiState.name,
            onValueChange = {
                onEvent(EmployeeFormEvent.EmployeeNameChanged(it))
            },
            label = stringResource(uiString.core_ui_employee_name_hint),
            isError = uiState.formErrors.nameError != null,
            keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            ),
            supportingText = uiState.formErrors.nameError?.let { stringResource(it) },
            modifier =
            Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
        )
        PosOutlinedTextField(
            value = uiState.password,
            onValueChange = {
                onEvent(EmployeeFormEvent.EmployeePasswordChanged(it))
            },
            label = stringResource(uiString.core_ui_employee_password_hint),
            supportingText = uiState.formErrors.passwordError?.let { stringResource(it) },
            visualTransformation = PasswordVisualTransformation(),
            isError = uiState.formErrors.passwordError != null,
            modifier = Modifier.fillMaxWidth(),
        )
        PosOutlinedTextField(
            value = uiState.phone,
            onValueChange = {
                onEvent(EmployeeFormEvent.EmployeePhoneChanged(it))
            },
            label = stringResource(uiString.core_ui_work_phone_number_hint),
            supportingText = uiState.formErrors.phoneError?.let { stringResource(it) },
            isError = uiState.formErrors.phoneError != null,
            modifier = Modifier.fillMaxWidth(),
        )
        ExposedDropdownMenuBox(
            expanded = rolesExpanded,
            onExpandedChange = { rolesExpanded = !rolesExpanded },
        ) {
            PosOutlinedTextField(
                value = uiState.role.toDisplayString(),
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
                            onEvent(EmployeeFormEvent.EmployeeRoleChanged(role))
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
                value = branches.find { it.id == uiState.assignedBranchId }?.name ?: "",
                onValueChange = {},
                readOnly = true,
                isError = uiState.formErrors.assignedBranchesError != null,
                supportingText = uiState.formErrors.assignedBranchesError?.let {
                    stringResource(
                        it,
                    )
                },
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
                        text = { Text(branch.name) },
                        onClick = {
                            onEvent(EmployeeFormEvent.EmployeeBranchAssigned(branch.id))
                            branchExpanded = false
                        },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun EmployeeFormDialogPreview() {
    POSTheme {
        PosBackground {
            EmployeeDialogContent(
                uiState = EmployeeFormUiState(),
                isUpdate = false,
                onEvent = {},
                branches = listOf(),
                focusRequester = FocusRequester.Default,
            )
        }
    }
}
