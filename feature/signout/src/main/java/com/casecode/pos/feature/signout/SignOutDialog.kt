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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.DynamicAsyncImage
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.data.users.FirebaseUser
import com.casecode.pos.core.ui.R

@Composable
fun SignOutDialog(
    authViewModel: SignOutViewModel = hiltViewModel(),
    onSignOut: () -> Unit,
    onDismiss: () -> Unit,
) {
    val currentUser by authViewModel.userUiState.collectAsStateWithLifecycle()
    var isSignOut by remember { mutableStateOf(false) }

    SignOutDialog(onDismiss, currentUser) { isSignOut = true }
    LaunchedEffect(isSignOut) {
        if (isSignOut) {
            authViewModel.signOut().await()
            onSignOut()
        }
    }
}

@Composable
private fun SignOutDialog(
    onDismiss: () -> Unit,
    currentUser: FirebaseUser?,
    onSignOut: () -> Unit,
) {
    val configuration = LocalConfiguration.current

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 80.dp),
        onDismissRequest = onDismiss,
        title = {
            Image(
                painter = painterResource(id = R.drawable.core_ui_ic_google),
                contentDescription = "Google Logo",
                modifier =
                Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center),
            )
        },
        text = {
            Column(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                DynamicAsyncImage(
                    imageUrl = currentUser?.photoUrl,
                    placeholder = painterResource(id = R.drawable.core_ui_ic_google),
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = currentUser?.displayName ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = currentUser?.email ?: "",
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSignOut()
                },
                colors =
                ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.error,
                ),
            ) {
                Text(stringResource(R.string.core_ui_dialog_ok_button_text))
            }
        },
    )
}

@Preview
@Composable
fun SignOutDialogPreview() {
    POSTheme {
        SignOutDialog(
            onDismiss = {},
            currentUser = FirebaseUser("email", "name", "photoUrl"),
            onSignOut = {},
        )
    }
}