package com.casecode.pos.ui.signIn

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.R
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosOutlinedButton
import com.casecode.pos.core.designsystem.component.PosTextButton
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

@Composable
fun SignInScreen(viewModel: SignInActivityViewModel) {
    val uiState by viewModel.signInUiState.collectAsStateWithLifecycle()
    var showDialogLoginEmployee by rememberSaveable { mutableStateOf(false) }
    var showDownloadGooglePlay by remember { mutableStateOf(false) }
    val context = LocalContext.current

    SignInScreen(
        uiState = uiState,
        onSignInCLick = {
            if (isGooglePlayServicesAvailable(context)) {
                viewModel.signIn()
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
        com.casecode.pos.feature.login_employee.LoginInEmployeeDialog {
            showDialogLoginEmployee = false
        }
    }
    if (showDownloadGooglePlay) {
        ShowAlternativeSignInDialog(context) { showDownloadGooglePlay = false }
    }
}

@Composable
fun SignInScreen(
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

    uiState.userMessage?.let { message ->
        val snackbarText = stringResource(message)
        LaunchedEffect(snackState, uiState, message, snackbarText) {
            snackState.showSnackbar(snackbarText)
            onMessageShown()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (!isLandscape) {
                Spacer(modifier = Modifier.height(64.dp))
            }
            Image(
                painter = painterResource(id = R.drawable.ic_point_of_sale_24),
                contentDescription = null,
                modifier = Modifier.wrapContentSize(),
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                )
                if (!isLandscape) {
                    Text(
                        text = stringResource(id = R.string.pos),
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f)) // Push buttons to the bottom

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.sign_in_hint),
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = modifier.height(16.dp))
                PosOutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    onClick = onSignInCLick,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.ic_google),
                            contentDescription = null,
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(text = stringResource(id = R.string.sign_in_google_title))
                    }
                }

                Spacer(modifier = modifier.height(8.dp)) // Space between buttons

                PosTextButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    onClick = onLoginEmployeeClick,
                ) {
                    Text(stringResource(id = R.string.sign_in_employee_option))
                }
            }

            Spacer(modifier = Modifier.height(16.dp)) // Bottom padding
        }
    }
}

private fun isGooglePlayServicesAvailable(context: Context): Boolean {
    val apiAvailability = GoogleApiAvailability.getInstance()
    val resultCode = apiAvailability.isGooglePlayServicesAvailable(context)
    return resultCode == ConnectionResult.SUCCESS
}

@Composable
private fun ShowAlternativeSignInDialog(context: Context, onDismiss: () -> Unit) {
    // Display a dialog or message informing the user about the lack of Google Play services
    // and provide an option to download them from the Play Store.
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = stringResource(R.string.google_play_services_required)) },
        text = { Text(text = stringResource(R.string.google_play_services_message)) },
        confirmButton = {
            PosTextButton(
                onClick = { openGooglePlayStore(context); onDismiss() },
                text = { Text(stringResource(R.string.action_download)) },
            )
        },
        dismissButton = {
            PosTextButton(
                onClick = { onDismiss() },
                text = { Text(stringResource(com.casecode.pos.core.ui.R.string.core_ui_dialog_cancel_button_text)) },
            )
        },
    )

}

private fun openGooglePlayStore(context: Context) {
    val playStoreIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gms"),
    )
    try {
        startActivity(context, playStoreIntent, null)
    } catch (_: ActivityNotFoundException) {
        // Handle the case where the Play Store app is not installed
        val webIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gms"),
        )
        startActivity(context, webIntent, null)
    }
}

@com.casecode.pos.core.ui.DevicePreviews
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