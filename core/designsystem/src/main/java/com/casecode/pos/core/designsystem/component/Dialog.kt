package com.casecode.pos.core.designsystem.component

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.casecode.pos.core.designsystem.R

@Composable
fun PermissionDialog(messagePermission: Int, onDismiss: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {},

        text = {
            Text(stringResource(messagePermission))
        },
        confirmButton = {
            PosOutlinedButton(
                onClick = {
                    context.goToAppSetting()
                    onDismiss()
                },
            ) {
                Text(stringResource(R.string.core_designsystem_go_to_setting))
            }
        },
    )
}

fun Context.goToAppSetting() {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null),
    )
    startActivity(intent)
}