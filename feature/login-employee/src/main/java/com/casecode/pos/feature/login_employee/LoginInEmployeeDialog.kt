package com.casecode.pos.feature.login_employee

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosLoadingWheel
import com.casecode.pos.core.designsystem.component.PosOutlinedTextField
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.ui.startScanningBarcode
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.casecode.pos.core.ui.R.string as uiString

@Composable
fun LoginInEmployeeDialog(
    viewModel: LoginEmployeeViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
) {
    val uiState by viewModel.loginEmployeeUiState.collectAsStateWithLifecycle()
    LoginInEmployeeDialog(
        uiState, viewModel::showMessageLoginEmployee,
        viewModel::snackbarMessageShownLoginEmployee,
        viewModel::loginByEmployee,
        onDismiss,
    )

}

@Composable
fun LoginInEmployeeDialog(
    uiState: LoginEmployeeUiState,
    showMessage: (Int) -> Unit,
    onShowMessage: () -> Unit,
    onLoginEmployeeClick: (String, String, String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass,

    ) {

    val isCompact = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val userAdmin = rememberSaveable { mutableStateOf("") }
    val name = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }

    val userAdminError = remember { mutableStateOf(false) }
    val nameError = remember { mutableStateOf(false) }
    val passwordError = remember { mutableStateOf(false) }
    var isScanLauncher by remember { mutableStateOf(false) }

    val snackState = remember { SnackbarHostState() }

    LaunchedScanBarcode(
        isScanLauncher, context,
        onFailureScanEmpty = { message ->
            showMessage(message)
            isScanLauncher = false
        },
        onResultScan = {
            userAdmin.value = it
            userAdminError.value = it.isBlank()
            isScanLauncher = false
        },
    )

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = modifier.widthIn(max = configuration.screenWidthDp.dp - 80.dp),
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                stringResource(R.string.feature_login_employee_title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        },
        text = {
            Box {
                SnackbarHost(
                    hostState = snackState,
                    modifier
                        .align(Alignment.TopCenter)
                        .padding(2.dp)
                        .zIndex(1f),
                )

                uiState.userMessage?.let { message ->
                    val snackbarText = stringResource(message)
                    LaunchedEffect(snackState, uiState, message, snackbarText) {
                        snackState.showSnackbar(snackbarText)
                        onShowMessage()
                    }
                }

                Column {
                    PosOutlinedTextField(
                        readOnly = true,
                        value = userAdmin.value,
                        onValueChange = {
                            userAdmin.value = it
                            userAdminError.value = it.isBlank()
                        },
                        label = stringResource(R.string.feature_login_employee_hint_scan_admin_id),
                        isError = userAdminError.value,
                        supportingText = if (userAdminError.value) stringResource(R.string.feature_login_employee_login_error_uid_empty) else null,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Unspecified,
                            imeAction = ImeAction.Next,
                        ),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    isScanLauncher = true
                                },
                            ) {
                                Icon(PosIcons.QrCodeScanner, "")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    if (!isCompact) {
                        Row {
                            PosOutlinedTextField(
                                value = name.value,
                                onValueChange = {
                                    name.value = it
                                    nameError.value = it.isEmpty()
                                },
                                isError = nameError.value,
                                label = stringResource(uiString.core_ui_employee_name_hint),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next,
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp),
                                supportingText = if (nameError.value) stringResource(R.string.feature_login_employee_error_name_empty) else null,
                            )
                            PosOutlinedTextField(
                                value = password.value,
                                onValueChange = {
                                    password.value = it
                                    passwordError.value = it.isEmpty()
                                },
                                isError = passwordError.value,
                                label = stringResource(uiString.core_ui_employee_password_hint),
                                visualTransformation = PasswordVisualTransformation(),
                                supportingText = if (passwordError.value) stringResource(R.string.feature_login_employee_error_password_empty) else null,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Done,
                                ),
                                modifier = Modifier.weight(1f),
                            )
                        }
                    } else {
                        PosOutlinedTextField(
                            value = name.value,
                            onValueChange = {
                                name.value = it
                                nameError.value = it.isEmpty()
                            },
                            isError = nameError.value,
                            label = stringResource(uiString.core_ui_employee_name_hint),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            supportingText = if (nameError.value) stringResource(R.string.feature_login_employee_error_name_empty) else null,
                        )

                        PosOutlinedTextField(
                            value = password.value,
                            onValueChange = {
                                password.value = it
                                passwordError.value = it.isEmpty()
                            },
                            isError = passwordError.value,
                            label = stringResource(uiString.core_ui_employee_password_hint),
                            visualTransformation = PasswordVisualTransformation(),
                            supportingText = if (passwordError.value) stringResource(R.string.feature_login_employee_error_password_empty) else null,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    if (uiState.inProgressLoginEmployee) {
                        PosLoadingWheel(
                            "LoadingLoginEmployee",
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                enabled = uiState.inProgressLoginEmployee.not(),
                onClick = {
                    if (name.value.isEmpty() || userAdmin.value.isEmpty() || password.value.isEmpty()) {
                        nameError.value = name.value.isEmpty()
                        userAdminError.value = userAdmin.value.isBlank()
                        passwordError.value = password.value.isEmpty()
                    } else {
                        onLoginEmployeeClick(userAdmin.value, name.value, password.value)
                    }
                },
            ) {
                Text(stringResource(R.string.feature_login_employee_login_action_login))
            }
        },
    )
}


@Composable
private fun LaunchedScanBarcode(
    isScanLauncher: Boolean,
    context: Context,
    onResultScan: (String) -> Unit,
    onFailureScanEmpty: (Int) -> Unit,
) {
    val scanBarcodeLauncher = rememberLauncherForActivityResult(
        contract = ScanContract(),
        onResult = { result ->
            result.contents.let {
                if (it == null) {
                    onFailureScanEmpty(com.casecode.pos.core.ui.R.string.core_ui_scan_result_empty)
                } else {
                    onResultScan(it)
                }
            }
        },
    )

    // Launch activity result request within the effect
    LaunchedEffect(isScanLauncher) {
        if (isScanLauncher) {

            scanBarcodeLauncher.launch(
                ScanOptions().startScanningBarcode(
                    context,
                    R.string.feature_login_employee_hint_scan_admin_id,
                ),
            )


        }
    }
}

@com.casecode.pos.core.ui.DevicePreviews
@Composable
fun LoginInEmployeeDialogPreview() {
    POSTheme {
        PosBackground {
            LoginInEmployeeDialog(
                uiState = LoginEmployeeUiState(inProgressLoginEmployee = true),
                showMessage = {},
                onShowMessage = {},
                onLoginEmployeeClick = { _, _, _ -> },
                {},
            )
        }
    }
}

@com.casecode.pos.core.ui.DevicePreviews
@Composable
fun LoginInEmployeeDialogLoadingPreview() {
    POSTheme {
        PosBackground {
            LoginInEmployeeDialog(
                uiState = LoginEmployeeUiState(inProgressLoginEmployee = true),
                showMessage = {},
                onShowMessage = {},
                onLoginEmployeeClick = { _, _, _ -> },
                {},
            )
        }
    }
}