package com.casecode.pos.feature.item

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.casecode.pos.core.designsystem.component.PosTextButton

@Composable
fun DeleteItemDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val configuration = LocalConfiguration.current
    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 80.dp),
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                stringResource(R.string.feature_item_dialog_delete_item_title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        },
        text = {
            Text(
                stringResource(R.string.feature_item_dialog_delete_item_message),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        },
        confirmButton = {
            PosTextButton(onClick = onConfirm) {
                Text(stringResource(R.string.feature_item_dialog_delete_button_text))
            }
        },
        dismissButton = {
            PosTextButton(onClick = onDismiss) { Text(stringResource(com.casecode.pos.core.ui.R.string.core_ui_dialog_cancel_button_text)) }
        },
    )
}