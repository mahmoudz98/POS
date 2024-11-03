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
package com.casecode.pos.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.casecode.pos.core.designsystem.component.PosOutlinedTextField
import com.casecode.pos.core.model.data.users.Branch
import kotlin.collections.forEach
import com.casecode.pos.core.ui.R.string as uiString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDialogContent(
    name: String,
    onNameChange: (String) -> Unit,
    hasNameError: Boolean,
    phone: String,
    onPhoneChange: (String) -> Unit,
    hasPhoneError: Int?,
    password: String,
    onPasswordChange: (String) -> Unit,
    hasPasswordError: Int?,
    branches: List<Branch>,
    selectedBranch: String,
    onSelectedBranchChange: (String) -> Unit,
    branchError: Boolean,
    selectedPermission: String,
    onSelectedPermissionChange: (String) -> Unit,
    permissionError: Boolean,
    focusRequester: FocusRequester,
) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        PosOutlinedTextField(
            value = name,
            onValueChange = { onNameChange(it) },
            label = stringResource(uiString.core_ui_employee_name_hint),
            isError = hasNameError,
            keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            ),
            supportingText = if (hasNameError) {
                stringResource(
                    uiString.core_ui_error_employee_name_empty,
                )
            } else {
                null
            },
            modifier =
            Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
        )

        PosOutlinedTextField(
            value = phone,
            onValueChange = {
                onPhoneChange(it)
            },
            label = stringResource(uiString.core_ui_work_phone_number_hint),
            supportingText = hasPhoneError?.let { stringResource(it) },
            keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
            ),
            isError = hasPhoneError != null,
            modifier = Modifier.fillMaxWidth(),
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
                supportingText = if (branchError) {
                    stringResource(
                        uiString.core_ui_error_add_employee_branch_empty,
                    )
                } else {
                    null
                },
                label = stringResource(uiString.core_ui_branch_name_hint),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = branchExpanded,
                    )
                },
                modifier =
                Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
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
                                branch.branchName,
                            )
                        },
                        onClick = {
                            onSelectedBranchChange(branch.branchName)
                            branchExpanded = false
                        },
                    )
                }
            }
        }
        var permissionExpanded by remember { mutableStateOf(false) }
        val permissions = stringArrayResource(R.array.core_ui_employee_permissions)
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
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = permissionExpanded,
                    )
                },
                supportingText =
                if (permissionError) {
                    stringResource(
                        uiString.core_ui_error_add_employee_permission_empty,
                    )
                } else {
                    null
                },
                modifier =
                Modifier
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
                            onSelectedPermissionChange(permission)
                            permissionExpanded = false
                        },
                    )
                }
            }
        }
    }
}