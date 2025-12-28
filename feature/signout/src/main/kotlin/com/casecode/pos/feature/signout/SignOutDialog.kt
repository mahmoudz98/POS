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
package com.casecode.pos.feature.signout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowDpSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.PosLoadingWheel
import com.casecode.pos.core.designsystem.component.PosTextButton
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.SessionStateResult
import com.casecode.pos.core.model.business.EmployeeRole
import com.casecode.pos.core.ui.business.toDisplayString
import com.casecode.pos.core.ui.R as UiR
import com.casecode.pos.feature.signout.R as FeatureR

@Composable
fun SignOutDialog(
    viewModel: SignOutViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isSignOut by remember { mutableStateOf(false) }

    SignOutDialogContent(
        uiState = uiState,
        onDismiss = onDismiss,
        onConfirmSignOut = { isSignOut = true },
    )

    LaunchedEffect(isSignOut) {
        if (isSignOut) {
            viewModel.signOut().await()
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun SignOutDialogContent(
    uiState: SessionStateResult,
    onDismiss: () -> Unit,
    onConfirmSignOut: () -> Unit,
) {
    val currentSize = currentWindowDpSize()

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = currentSize.width - 80.dp),
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(FeatureR.string.feature_signout_title),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                when (uiState) {
                    is SessionStateResult.Loading -> {
                        PosLoadingWheel(contentDesc = "")
                    }

                    is SessionStateResult.OwnerLoggedIn -> {
                        UserInfoContent(
                            name = uiState.userName,
                            role = stringResource(UiR.string.core_ui_employee_role_owner_text),
                        )
                    }

                    is SessionStateResult.EmployeeLoggedIn -> {
                        UserInfoContent(
                            name = uiState.userName,
                            role = uiState.role.toDisplayString(),
                        )
                    }

                    is SessionStateResult.OwnerOnBoarding, is SessionStateResult.None -> {
                        Text(
                            text = stringResource(FeatureR.string.feature_signout_message),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        },
        confirmButton = {
            PosTextButton(
                onClick = onConfirmSignOut,
            ) {
                Text(
                    text = stringResource(FeatureR.string.feature_signout_action_sign_out),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
        dismissButton = {
            PosTextButton(onClick = onDismiss) {
                Text(stringResource(UiR.string.core_ui_dialog_cancel_button_text))
            }
        },
    )
}

@Composable
private fun UserInfoContent(
    name: String,
    role: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(72.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = name.take(1).uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = role,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Text(
            text = stringResource(FeatureR.string.feature_signout_confirmation),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview
@Composable
fun SignOutDialogOwnerPreview() {
    POSTheme {
        SignOutDialogContent(
            uiState = SessionStateResult.OwnerLoggedIn(
                businessId = "business123",
                activeBranchId = "branch123",
                userId = "user123",
                userName = "Mahmood Mohamed",
            ),
            onDismiss = {},
            onConfirmSignOut = {},
        )
    }
}

@Preview
@Composable
fun SignOutDialogEmployeePreview() {
    POSTheme {
        SignOutDialogContent(
            uiState = SessionStateResult.EmployeeLoggedIn(
                businessId = "business123",
                activeBranchId = "branch123",
                userId = "user123",
                userName = "Jane Doe",
                role = EmployeeRole.CASHIER,
            ),
            onDismiss = {},
            onConfirmSignOut = {},
        )
    }
}
