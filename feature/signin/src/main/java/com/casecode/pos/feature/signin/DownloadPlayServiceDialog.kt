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