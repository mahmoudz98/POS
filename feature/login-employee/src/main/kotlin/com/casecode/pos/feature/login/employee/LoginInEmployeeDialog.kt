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
package com.casecode.pos.feature.login.employee

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowDpSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosLoadingWheel
import com.casecode.pos.core.designsystem.component.PosOutlinedTextField
import com.casecode.pos.core.designsystem.component.PosTextButton
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.ui.DevicePreviews
import com.casecode.pos.core.ui.utils.scanOptions
import com.casecode.pos.core.ui.R.string as uiString
import com.casecode.pos.feature.login.employee.R.string as fString

@Composable
fun LoginInEmployeeDialog(
    viewModel: LoginEmployeeViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
) {
    val uiState by viewModel.loginEmployeeUiState.collectAsStateWithLifecycle()
    val formState by viewModel.formState.collectAsStateWithLifecycle()

    LoginInEmployeeDialog(
        uiState = uiState,
        formState = formState,
        onFormEvent = viewModel::onFormEvent,
        showMessage = viewModel::showMessageLoginEmployee,
        onShowMessage = viewModel::snackbarMessageShownLoginEmployee,
        onDismiss = onDismiss,
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun LoginInEmployeeDialog(
    modifier: Modifier = Modifier,
    uiState: LoginEmployeeUiState,
    formState: LoginEmployeeFormState,
    onFormEvent: (LoginEmployeeFormEvent) -> Unit,
    showMessage: (Int) -> Unit,
    onShowMessage: () -> Unit,
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo(true).windowSizeClass,
    onDismiss: () -> Unit,
) {
    val isCompact =
        windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)

    val context = LocalContext.current
    val snackState = remember { SnackbarHostState() }
    val currentSize = currentWindowDpSize()
    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = currentSize.width - 80.dp),
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                stringResource(fString.feature_login_employee_title),
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
                        .fillMaxWidth()
                        .zIndex(1f),
                )
                Column {
                    PosOutlinedTextField(
                        value = formState.companyCode,
                        onValueChange = {
                            onFormEvent(LoginEmployeeFormEvent.CompanyCodeChanged(it))
                        },
                        label = stringResource(fString.feature_login_employee_label_company_code),
                        placeHolder = {
                            Text(stringResource(fString.feature_login_employee_hint_scan_company_code))
                        },
                        isError = formState.companyCodeError != null,
                        supportingText = formState.companyCodeError?.let { stringResource(it) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next,
                        ),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    context.scanOptions(
                                        onModuleDownloaded = { showMessage(it) },
                                        onModuleDownloading = { showMessage(it) },
                                        onResult = {
                                            onFormEvent(LoginEmployeeFormEvent.CompanyCodeChanged(it))
                                        },
                                        onFailure = {
                                            showMessage(it)
                                        },
                                        onCancel = {
                                            showMessage(it)
                                        },
                                    )
                                },
                            ) {
                                Icon(
                                    PosIcons.QrCodeScanner,
                                    stringResource(fString.feature_login_employee_label_company_code),
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    if (!isCompact) {
                        Row {
                            CredentialsInputs(modifier.weight(1f), formState, onFormEvent)
                        }
                    } else {
                        CredentialsInputs(modifier.fillMaxWidth(), formState, onFormEvent)
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
            PosTextButton(
                enabled = uiState.inProgressLoginEmployee.not(),
                onClick = {
                    onFormEvent(LoginEmployeeFormEvent.SubmitClicked)
                },
            ) {
                Text(stringResource(fString.feature_login_employee_login_button_text))
            }
        },
    )
    uiState.userMessage?.let { message ->
        val snackbarText = stringResource(message)
        LaunchedEffect(snackState, uiState, message, snackbarText) {
            snackState.showSnackbar(snackbarText)
            onShowMessage()
        }
    }
}

@Composable
private fun CredentialsInputs(
    modifier: Modifier,
    formState: LoginEmployeeFormState,
    onFormEvent: (LoginEmployeeFormEvent) -> Unit,
) {
    PosOutlinedTextField(
        value = formState.employeeId,
        onValueChange = {
            onFormEvent(LoginEmployeeFormEvent.EmployeeIdChanged(it))
        },
        isError = formState.employeeIdError != null,
        label = stringResource(uiString.core_ui_employee_id_hint),
        keyboardOptions =
        KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Next,
        ),
        modifier =
        modifier
            .padding(end = 8.dp),
        supportingText = formState.employeeIdError?.let { stringResource(it) },
    )
    PosOutlinedTextField(
        value = formState.password,
        onValueChange = {
            onFormEvent(LoginEmployeeFormEvent.PasswordChanged(it))
        },
        isError = formState.passwordError != null,
        label = stringResource(uiString.core_ui_employee_password_hint),
        visualTransformation = PasswordVisualTransformation(),
        supportingText = formState.passwordError?.let { stringResource(it) },
        keyboardOptions =
        KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done,
        ),
        modifier = modifier,
    )
}

@DevicePreviews
@Composable
fun LoginInEmployeeDialogPreview() {
    POSTheme {
        PosBackground {
            LoginInEmployeeDialog(
                uiState = LoginEmployeeUiState(inProgressLoginEmployee = true),
                formState = LoginEmployeeFormState(),
                onFormEvent = {},
                showMessage = {},
                onShowMessage = {},
                onDismiss = {},
            )
        }
    }
}

@DevicePreviews
@Composable
fun LoginInEmployeeDialogLoadingPreview() {
    POSTheme {
        PosBackground {
            LoginInEmployeeDialog(
                uiState = LoginEmployeeUiState(inProgressLoginEmployee = true),
                formState = LoginEmployeeFormState(),
                onFormEvent = {},
                showMessage = {},
                onShowMessage = {},
                onDismiss = {},
            )
        }
    }
}
