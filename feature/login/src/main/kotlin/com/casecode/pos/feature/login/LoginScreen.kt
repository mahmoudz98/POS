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
package com.casecode.pos.feature.login

import android.app.Activity
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass.Companion.HEIGHT_DP_MEDIUM_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosLoadingWheel
import com.casecode.pos.core.designsystem.component.PosOutlinedButton
import com.casecode.pos.core.designsystem.component.PosTextButton
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.ui.BranchSelectionDialog
import com.casecode.pos.core.ui.DevicePreviews
import com.casecode.pos.core.ui.R as uiR

@Composable
fun LoginRoute(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginEmployeeClick: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current as Activity

    LoginScreen(
        uiState = uiState,
        onOwnerLoginClick = {
            viewModel.onEvent(LoginEvent.GoogleSignInResult(context))
        },
        onLoginEmployeeClick = onLoginEmployeeClick,
    )
    if (uiState is LoginUiState.ShowPlayServicesUnavailableDialog) {
        DownloadPlayServiceDialog(
            context = context,
            onDismiss = { viewModel.onEvent(LoginEvent.PlayServicesDialogDismissed) },
        )
    }

    if (uiState is LoginUiState.BranchSelection) {
        BranchSelectionDialog(
            branches = (uiState as LoginUiState.BranchSelection).branches,
            onBranchSelected = { viewModel.onEvent(LoginEvent.BranchSelected(it)) },
            onDismissRequest = { viewModel.onEvent(LoginEvent.ErrorMessageShown) },
        )
    }

    if (uiState is LoginUiState.Error) {
        val snackbarText = stringResource((uiState as LoginUiState.Error).messageResId)
        LaunchedEffect(Unit) {
            onShowSnackbar(snackbarText, "")
            viewModel.onEvent(LoginEvent.ErrorMessageShown)
        }
    }
}

@Composable
internal fun LoginScreen(
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
    uiState: LoginUiState,
    onOwnerLoginClick: () -> Unit,
    onLoginEmployeeClick: () -> Unit,
) {
    val isCompact = windowAdaptiveInfo.windowSizeClass.isAtLeastBreakpoint(
        WIDTH_DP_MEDIUM_LOWER_BOUND,
        HEIGHT_DP_MEDIUM_LOWER_BOUND,
    ).not()

    val imageColor by animateColorAsState(
        if (isSystemInDarkTheme()) Color.White else Color.Black,
    )
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center,
    ) {
        if (uiState is LoginUiState.Loading) {
            PosLoadingWheel("loginLoading")
        }
        Column(
            modifier = modifier
                .fillMaxSize()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (isCompact) {
                Spacer(modifier = Modifier.height(40.dp))
            }

            Image(
                painter = painterResource(id = uiR.drawable.core_ui_ic_point_of_sale_24),
                contentDescription = null,
                modifier = Modifier.size(if (isCompact) 32.dp else 48.dp),
                colorFilter = ColorFilter.tint(imageColor),

            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.feature_login_app_name),
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                )
                if (isCompact) {
                    Text(
                        text = stringResource(id = R.string.feature_login_pos),
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
                    text = stringResource(id = R.string.feature_login_hint),
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = modifier.height(16.dp))
                PosOutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    onClick = onOwnerLoginClick,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(uiR.drawable.core_ui_ic_google),
                            contentDescription = null,
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(text = stringResource(id = R.string.feature_login_button_owner_google))
                    }
                }

                Spacer(modifier = modifier.height(8.dp))

                PosTextButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    onClick = onLoginEmployeeClick,
                ) {
                    Text(stringResource(id = R.string.feature_login_button_employee))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@DevicePreviews
@Composable
fun LoginScreenPreview() {
    POSTheme {
        PosBackground {
            LoginScreen(
                uiState = LoginUiState.Idle,
                onOwnerLoginClick = {},
                onLoginEmployeeClick = {},
            )
        }
    }
}

@DevicePreviews
@Composable
fun LoginScreenWithErrorPreview() {
    POSTheme {
        PosBackground {
            LoginScreen(
                uiState = LoginUiState.Error(R.string.feature_login_error_unknown),
                onOwnerLoginClick = {},
                onLoginEmployeeClick = {},
            )
        }
    }
}
