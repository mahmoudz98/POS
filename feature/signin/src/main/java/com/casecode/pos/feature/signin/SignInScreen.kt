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
package com.casecode.pos.feature.signin

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosLoadingWheel
import com.casecode.pos.core.designsystem.component.PosOutlinedButton
import com.casecode.pos.core.designsystem.component.PosTextButton
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.data.LoginStateResult
import com.casecode.pos.core.ui.DevicePreviews
import com.casecode.pos.core.ui.moveToMainActivity
import com.casecode.pos.core.ui.moveToStepperActivity
import com.casecode.pos.feature.login.employee.LoginInEmployeeDialog
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.casecode.pos.core.ui.R as uiR

@Composable
fun SignInScreen(viewModel: SignInActivityViewModel) {
    val uiState by viewModel.signInUiState.collectAsStateWithLifecycle()
    val signUiState by viewModel.loginStateResult.collectAsStateWithLifecycle()
    var showDialogLoginEmployee by rememberSaveable { mutableStateOf(false) }
    var showDownloadGooglePlay by remember { mutableStateOf(false) }
    val context = LocalContext.current
    DisposableEffect(signUiState) {
        when (signUiState) {
            is LoginStateResult.NotCompleteBusiness -> {
                moveToStepperActivity(context)
            }

            is LoginStateResult.EmployeeLogin, is LoginStateResult.SuccessLoginAdmin -> {
                moveToMainActivity(context)
            }

            else -> {}
        }
        onDispose {}
    }

    SignInScreen(
        uiState = uiState,
        onSignInCLick = {
            if (viewModel.isGooglePlayServicesAvailable()) {
                viewModel.signIn {
                    retrieveGoogleIdToken(
                        context as Activity,
                        viewModel.googleIdOption,
                    )
                }
            } else {
                showDownloadGooglePlay = true
            }
        },
        onLoginEmployeeClick = {
            showDialogLoginEmployee = true
        },
        onMessageShown = viewModel::snackbarMessageShown,
    )

    if (showDialogLoginEmployee) {
        LoginInEmployeeDialog {
            showDialogLoginEmployee = false
        }
    }
    if (showDownloadGooglePlay) {
        DownloadPlayServiceDialog(context) { showDownloadGooglePlay = false }
    }
}

@Composable
internal fun SignInScreen(
    modifier: Modifier = Modifier,
    uiState: SignInActivityUiState,
    onSignInCLick: () -> Unit,
    onLoginEmployeeClick: () -> Unit,
    onMessageShown: () -> Unit,
) {
    val snackState = remember { SnackbarHostState() }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    SnackbarHost(
        hostState = snackState,
        modifier
            .padding(16.dp)
            .zIndex(1f),
    )

    Box(
        modifier =
        modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (uiState.isLoading) {
            PosLoadingWheel("SignInLoading")
        }
        Column(
            modifier =
            modifier
                .fillMaxSize()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (!isLandscape) {
                Spacer(modifier = Modifier.height(64.dp))
            }
            Image(
                painter = painterResource(id = R.drawable.feature_signin_ic_point_of_sale_24),
                contentDescription = null,
                modifier = Modifier.wrapContentSize(),
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.feature_signin_app_name),
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                )
                if (!isLandscape) {
                    Text(
                        text = stringResource(id = R.string.feature_signin_pos),
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.feature_signin_hint),
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = modifier.height(16.dp))
                PosOutlinedButton(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    onClick = onSignInCLick,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(uiR.drawable.core_ui_ic_google),
                            contentDescription = null,
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(text = stringResource(id = R.string.feature_signin_google_title))
                    }
                }

                Spacer(modifier = modifier.height(8.dp))

                PosTextButton(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    onClick = onLoginEmployeeClick,
                ) {
                    Text(stringResource(id = R.string.feature_signin_employee_option))
                }
            }

            Spacer(modifier = Modifier.height(16.dp)) // Bottom padding
        }
    }
    uiState.userMessage?.let { message ->
        val snackbarText = stringResource(message)
        LaunchedEffect(snackState, uiState, message, snackbarText) {
            snackState.showSnackbar(snackbarText)
            onMessageShown()
        }
    }
}

private suspend fun retrieveGoogleIdToken(
    activityContext: Context,
    googleIdOption: GetGoogleIdOption,
): String {
    val credentialRequest =
        GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()
    val credential =
        CredentialManager.create(activityContext)
            .getCredential(request = credentialRequest, context = activityContext)
    val googleIdTokenCredentialRequest =
        GoogleIdTokenCredential.createFrom(credential.credential.data)
    return googleIdTokenCredentialRequest.idToken
}

@DevicePreviews
@Composable
fun SignInScreenPreview() {
    POSTheme {
        PosBackground {
            SignInScreen(
                uiState = SignInActivityUiState(),
                onSignInCLick = {},
                onLoginEmployeeClick = {},
                onMessageShown = {},
            )
        }
    }
}