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
package com.casecode.pos.feature.setting.printer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import com.casecode.pos.core.designsystem.component.PosLoadingWheel
import com.casecode.pos.core.printer.model.PrinterState
import com.casecode.pos.core.printer.R as printerR

@Composable
fun StatePrinterDialog(
    printerState: PrinterState,
    onDismiss: () -> Unit,
) {
    var shouldDismiss by remember { mutableStateOf(false) }
    AlertDialog(
        properties =
        DialogProperties(
            dismissOnBackPress = shouldDismiss,
            dismissOnClickOutside = shouldDismiss,
        ),
        onDismissRequest = {
            shouldDismiss = false
            onDismiss()
        },
        title = {
            if (printerState !is PrinterState.Error && printerState !is PrinterState.Finished) {
                Text(stringResource(printerR.string.core_printer_state_message_printing_in_progress))
            }
        },
        text = {
            Column(Modifier.fillMaxWidth()) {
                when (printerState) {
                    is PrinterState.Error, is PrinterState.Finished -> {
                        Text(
                            stringResource(
                                printerState.message
                                    ?: printerR.string.core_printer_state_bluetooth_message_error,
                            ),
                        )
                        shouldDismiss = true
                    }

                    else -> {
                        Text(
                            stringResource(
                                printerState.message
                                    ?: printerR.string.core_printer_state_message_printing,
                            ),
                        )

                        PosLoadingWheel(
                            "LoadingStatePrinter",
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                        )
                    }
                }
            }
        },
        confirmButton = {},
    )
}