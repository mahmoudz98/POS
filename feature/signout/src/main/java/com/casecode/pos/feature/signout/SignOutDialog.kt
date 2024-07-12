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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.DynamicAsyncImage


@Composable
fun SignOutDialog(
    authViewModel: SignOutViewModel = hiltViewModel(),
    onSignOut: () -> Unit,
    onDismiss: () -> Unit,
) {
    val currentUser by authViewModel.userUiState.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    var isSignOut by remember { mutableStateOf(false) }

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 80.dp),
        onDismissRequest = onDismiss,
        title = {
            Image(
                painter = painterResource(id = R.drawable.ic_logo_google),
                contentDescription = "Google Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center),
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                    DynamicAsyncImage(
                        imageUrl = currentUser?.photoUrl,
                        placeholder = painterResource(id = R.drawable.ic_google),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp).clip(CircleShape),
                    )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = currentUser?.displayName ?: "Mahmoud",
                    style = MaterialTheme.typography.bodyLarge,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = currentUser?.email ?: "mahmoud99239@gmail.com",
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    isSignOut = true
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.error,
                ),
            ) {
                Text(stringResource(com.casecode.pos.core.ui.R.string.core_ui_dialog_ok_button_text))
            }
        },

        )
    LaunchedEffect(isSignOut) {
        if(isSignOut){
        authViewModel.signOut().await()
            onSignOut()

        }

    }
}