package com.casecode.pos.feature.stepper.employees

import android.content.Context
import android.telephony.TelephonyManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosOutlinedTextField
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.ui.validatePhoneNumber
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
            onDismiss()
        }
    }
    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 80.dp),
        onDismissRequest = onDismiss,
        title = { Text(stringResource(if (isUpdate) uiString.core_ui_update_employee_title else uiString.core_ui_add_employee_title)) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                PosOutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = it.isBlank()
                    },
                    label = stringResource(uiString.core_ui_employee_name_hint),
                    isError = nameError,
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next,
                        ),
                    supportingText = if (nameError) stringResource(uiString.core_ui_error_employee_name_empty) else null,
                    modifier = Modifier.fillMaxWidth(),
                )

                PosOutlinedTextField(
                    value = phone,
                    onValueChange = {
                        phone = it
                        phoneError = validatePhoneNumber(it, countryIsoCode)
                    },
                    label = stringResource(uiString.core_ui_work_phone_number_hint),
                    supportingText = phoneError?.let { stringResource(it) },
                    keyboardOptions =
                        KeyboardOptions(
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
                            if (it.isBlank()) {
                                uiString.core_ui_error_add_employee_password_empty
                            } else if (it.length < 6) {
                                uiString.core_ui_error_add_employee_password
                            } else {
                                null
                            }
                    },
                    label = stringResource(uiString.core_ui_employee_password_hint),
                    supportingText = phoneError?.let { stringResource(it) },
                    visualTransformation = PasswordVisualTransformation(),
                    isError = passwordError != null,
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
                        supportingText = if (branchError) stringResource(uiString.core_ui_error_add_employee_branch_empty) else null,
                        label = stringResource(uiString.core_ui_branch_name_hint),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = branchExpanded) },
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
                                    selectedBranch = branch.branchName
                                    branchExpanded = false
                                },
                            )
                        }
                    }
                }

                var permissionExpanded by remember { mutableStateOf(false) }
                val permissions = stringArrayResource(com.casecode.pos.core.ui.R.array.core_ui_employee_permissions)
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
            Text(
                text =
                stringResource(
                    if (isUpdate) uiString.core_ui_update_employee_button_text else uiString.core_ui_add_employee_button_text,
                ),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier =
                Modifier.clickable {
                    onClickTriggered()
                },
            )
        },
    )
}

@com.casecode.pos.core.ui.DevicePreviews
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