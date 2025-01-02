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
package com.casecode.pos.feature.stepper.employees

import android.content.Context
import android.telephony.TelephonyManager
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosTextButton
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.ui.DevicePreviews
import com.casecode.pos.core.ui.EmployeeDialogContent
import com.casecode.pos.core.ui.utils.validatePhoneNumber
import com.casecode.pos.feature.stepper.StepperBusinessUiState
import com.casecode.pos.feature.stepper.StepperBusinessViewModel
import com.casecode.pos.core.ui.R.string as uiString

@Composable
fun EmployeeStepperDialog(
    viewModel: StepperBusinessViewModel,
    isUpdate: Boolean = false,
    onDismiss: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val countryIsoCode = telephonyManager.networkCountryIso.uppercase()
    EmployeeStepperDialog(
        uiState = uiState,
        isUpdate = isUpdate,
        countryIsoCode = countryIsoCode,
        onClick = { name, phone, password, selectedBranch, selectedPermission ->
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
        },
        onDismiss = onDismiss,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeStepperDialog(
    uiState: StepperBusinessUiState,
    isUpdate: Boolean,
    countryIsoCode: String,
    onClick: (String, String, String, String, String) -> Unit,
    onDismiss: () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val employeeUpdate = if (isUpdate) uiState.employeeSelected else null
    val branches = uiState.branches
    var name by remember { mutableStateOf(employeeUpdate?.name ?: "") }
    var phone by remember { mutableStateOf(employeeUpdate?.phoneNumber ?: "") }
    var password by remember { mutableStateOf(employeeUpdate?.password ?: "") }
    var selectedBranch by remember { mutableStateOf(employeeUpdate?.branchName ?: "") }
    var selectedPermission by remember { mutableStateOf(employeeUpdate?.permission ?: "") }

    var nameError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf<Int?>(null) }
    var passwordError by remember { mutableStateOf<Int?>(null) }
    var branchError by remember { mutableStateOf(false) }
    var permissionError by remember { mutableStateOf(false) }
    val onClickTriggered = {
        val validatePhoneNumber = validatePhoneNumber(phone, countryIsoCode)
        if (name.isEmpty() ||
            validatePhoneNumber != null ||
            password.isEmpty() ||
            password.length < 6 ||
            selectedBranch.isEmpty() ||
            selectedPermission.isEmpty()
        ) {
            nameError = name.isEmpty()
            phoneError = validatePhoneNumber
            passwordError =
                if (password.isEmpty()) {
                    uiString.core_ui_error_add_employee_password_empty
                } else if (password.length < 6) {
                    uiString.core_ui_error_add_employee_password
                } else {
                    null
                }
            branchError = selectedBranch.isEmpty()
            permissionError = selectedPermission.isEmpty()
        } else {
            onClick(name, phone, password, selectedBranch, selectedPermission)
            keyboardController?.hide()
            onDismiss()
        }
    }

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 80.dp),
        onDismissRequest = {
            focusRequester.freeFocus()
            keyboardController?.hide()
            onDismiss()
        },
        title = { Text(stringResource(if (isUpdate) uiString.core_ui_update_employee_title else uiString.core_ui_add_employee_title)) },
        text = {
            EmployeeDialogContent(
                name = name,
                onNameChange = {
                    name = it
                    nameError = it.isBlank()
                },
                hasNameError = nameError,
                phone = phone,
                onPhoneChange = {
                    phone = it
                    phoneError = validatePhoneNumber(it, countryIsoCode)
                },
                hasPhoneError = phoneError,
                password = password,
                onPasswordChange = {
                    password = it
                    passwordError =
                        if (it.isBlank()) {
                            uiString.core_ui_error_add_employee_password_empty
                        } else if (it.length < 6) {
                            uiString.core_ui_error_add_employee_password
                        } else {
                            null
                        }
                },
                hasPasswordError = passwordError,
                branches = branches,
                selectedBranch = selectedBranch,
                onSelectedBranchChange = {
                    selectedBranch = it
                    branchError = it.isBlank()
                },
                branchError = branchError,
                selectedPermission = selectedPermission,
                onSelectedPermissionChange = {
                    selectedPermission = it
                    permissionError = it.isBlank()
                },
                permissionError = permissionError,
                focusRequester = focusRequester,
            )
        },
        confirmButton = {
            PosTextButton(
                onClick = {
                    onClickTriggered()
                },
            ) {
                Text(
                    stringResource(
                        if (isUpdate) uiString.core_ui_update_employee_button_text else uiString.core_ui_add_employee_button_text,
                    ),
                )
            }
        },
    )
}

@DevicePreviews
@Composable
fun EmployeeStepperDialogPreview() {
    POSTheme {
        PosBackground {
            EmployeeStepperDialog(
                uiState = StepperBusinessUiState(),
                isUpdate = false,
                countryIsoCode = "en",
                onClick = { _, _, _, _, _ -> },
                onDismiss = {},
            )
        }
    }
}