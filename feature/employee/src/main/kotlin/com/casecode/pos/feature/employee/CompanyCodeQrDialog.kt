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
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowDpSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.DynamicAsyncQrCodeImage
import com.casecode.pos.core.designsystem.component.PosTextButton
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.ui.utils.encodeAsBitmap

@Composable
internal fun CompanyCodeQrDialog(
    viewModel: CompanyCodeViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
) {
    val companyCode by viewModel.companyCode.collectAsStateWithLifecycle()

    CompanyCodeQrDialog(companyCode = companyCode, onDismiss = onDismiss)
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun CompanyCodeQrDialog(
    companyCode: String?,
    currentSize: DpSize = currentWindowDpSize(),
    onDismiss: () -> Unit,
) {
    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = currentSize.width - 80.dp),
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                stringResource(R.string.feature_employee_dialog_title_company_code),
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
                Text(text = stringResource(R.string.feature_employee_dialog_message_company_code))
                Spacer(modifier = Modifier.height(8.dp))
                val qrCodeBitmap = remember(companyCode) {
                    companyCode?.takeIf { it.isNotBlank() }?.encodeAsBitmap()
                }
                DynamicAsyncQrCodeImage(
                    modifier = Modifier.size(120.dp),
                    data = qrCodeBitmap,
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
        CompanyCodeQrDialog(companyCode = "Mahdas@#$@#", onDismiss = {})
    }
}
