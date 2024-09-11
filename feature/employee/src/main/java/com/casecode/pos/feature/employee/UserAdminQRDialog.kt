package com.casecode.pos.feature.employee

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.data.utils.encodeAsBitmap
import com.casecode.pos.core.designsystem.component.DynamicAsyncQrCodeImage
import com.casecode.pos.core.designsystem.component.PosTextButton
import com.casecode.pos.core.designsystem.theme.POSTheme

@Composable
fun UserAdminQrDialog(
    viewModel: EmployeeViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
) {
    val userAdmin = viewModel.currentUid.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.getCurrentUid()
    }
    UserAdminQrDialog(userAdmin = userAdmin.value, onDismiss = onDismiss)
}

@Composable
internal fun UserAdminQrDialog(
    userAdmin: String?,
    onDismiss: () -> Unit,
) {
    val configuration = LocalConfiguration.current

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 80.dp),
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                stringResource(R.string.feature_employee_dialog_title_user_admin),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = stringResource(R.string.feature_employee_dialog_message_user_admin))
                Spacer(modifier = Modifier.height(8.dp))
                DynamicAsyncQrCodeImage(
                    modifier = Modifier.size(120.dp),
                    data = userAdmin?.takeIf { it.isNotBlank() }?.encodeAsBitmap(),
                    contentDescription = null,
                )

            }
        },
        confirmButton = {
            PosTextButton(onClick = onDismiss) {
                Text(stringResource(com.casecode.pos.core.ui.R.string.core_ui_dialog_cancel_button_text))
            }

        },

        )
}

@Preview
@Composable
fun PreviewUserAdminQrDialog() {
    POSTheme {

        UserAdminQrDialog(userAdmin = "Mahdas@#$@#", onDismiss = {})
    }

}