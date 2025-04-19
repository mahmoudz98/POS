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
package com.casecode.pos.feature.item.print

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.DynamicAsyncQrCodeImage
import com.casecode.pos.core.designsystem.component.PosOutlinedTextField
import com.casecode.pos.core.designsystem.component.PosTextButton
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.data.users.Item
import com.casecode.pos.core.ui.utils.encodeAsBitmap
import com.casecode.pos.feature.item.ItemsViewModel
import com.casecode.pos.feature.item.R

@Composable
fun QRCodePrintItemDialog(viewModel: ItemsViewModel, onDismiss: () -> Unit) {
    val itemPrint = viewModel.itemSelected.collectAsStateWithLifecycle()
    QRCodePrintItemDialog(itemPrint = itemPrint.value, onDismiss = onDismiss)
}

@Composable
internal fun QRCodePrintItemDialog(itemPrint: Item?, onDismiss: () -> Unit) {
    val configuration = LocalConfiguration.current
    val name = rememberSaveable { mutableStateOf(itemPrint?.name) }
    val barcode = rememberSaveable { mutableStateOf(itemPrint?.sku) }
    val price = rememberSaveable { mutableStateOf(itemPrint?.unitPrice) }
    var printedItemCount by rememberSaveable {
        mutableIntStateOf(
            1,
        )
    } // Use State for printedItemCount

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 80.dp),
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                stringResource(R.string.feature_item_dialog_print_qr_code_title),
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
                PosOutlinedTextField(
                    value = printedItemCount.toString(),
                    onValueChange = { newValue ->
                        printedItemCount = newValue.toIntOrNull() ?: 1
                    },
                    isError = printedItemCount < 1,
                    label = stringResource(R.string.feature_item_copies_text),
                    keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = if (printedItemCount < 1) {
                        stringResource(
                            R.string.feature_item_error_copies_empty,
                        )
                    } else {
                        null
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))

                DynamicAsyncQrCodeImage(
                    modifier = Modifier.size(120.dp),
                    data = barcode.value?.encodeAsBitmap(),
                    contentDescription = null,
                )
                // Use more descriptive variable names
                val itemName = name.value ?: ""
                val itemBarcode = barcode.value.toString()
                val itemPrice = price.value.toString()

                Text(text = itemName)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = itemBarcode)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = itemPrice)
                Spacer(modifier = Modifier.height(8.dp))
            }
        },
        confirmButton = {
            PosTextButton(
                onClick = {
                    // TODO: handle print item
                },
            ) {
                Text(stringResource(R.string.feature_item_dialog_print_button_text))
            }
        },
        dismissButton = {
            PosTextButton(onClick = onDismiss) {
                Text(
                    stringResource(
                        com.casecode.pos.core.ui.R.string.core_ui_dialog_cancel_button_text,
                    ),
                )
            }
        },
    )
}

@Preview
@Composable
fun PreviewQRCodePrintItemDialog() {
    POSTheme {
        QRCodePrintItemDialog(
            Item(
                name = "Janine Whitfield",
                unitPrice = 4.5,
                quantity = 6,
                sku = "12345666",
                unitOfMeasurement = null,
                imageUrl = null,
            ),
        ) { }
    }
}