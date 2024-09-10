package com.casecode.pos.feature.signin

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat.startActivity
import com.casecode.pos.core.designsystem.component.PosTextButton

@Composable
internal fun DownloadPlayServiceDialog(
    context: Context,
    onDismiss: () -> Unit,
) {

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = stringResource(R.string.feature_signin_google_play_services_required)) },
        text = { Text(text = stringResource(R.string.feature_signin_google_play_services_message)) },
        confirmButton = {
            PosTextButton(
                onClick = {
                    openGooglePlayStore(context)
                    onDismiss()
                },
                text = { Text(stringResource(R.string.feature_signin_action_download)) },
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
    val playStoreIntent =
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gms"),
        )
    try {
        startActivity(context, playStoreIntent, null)
    } catch (_: ActivityNotFoundException) {
        // Handle the case where the Play Store app is not installed
        val webIntent =
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gms"),
            )
        startActivity(context, webIntent, null)
    }
}