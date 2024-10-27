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
package com.casecode.pos.feature.sale

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.casecode.pos.core.designsystem.component.PosTextButton
import com.casecode.pos.core.ui.R.string as uiString

@Composable
fun QuantityDialog(
    oldQuantity: Int,
    inStock: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
) {
    val quantity =
        rememberSaveable { mutableStateOf(oldQuantity.toString()) }
    val quantityError = remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.feature_sale_dialog_change_quantity_title)) },
        text = {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = quantity.value,
                    onValueChange = {
                        quantity.value = it

                        quantityError.value = it.isEmpty() || it.toDouble() > inStock || it.toDouble() <= 0
                    },
                    isError = quantityError.value,
                    label = { Text(text = stringResource(uiString.core_ui_item_quantity_label)) },
                    supportingText = {
                        Text(
                            if (quantityError.value) {
                                when {
                                    quantity.value.toDouble() <= 0.0 ->
                                        stringResource(
                                            R.string.feature_sale_error_quantity_greater_than_zero,
                                        )

                                    quantity.value.toDouble() > inStock ->
                                        stringResource(R.string.feature_sale_error_quantity_less_than) +
                                            inStock

                                    else -> ""
                                }
                            } else {
                                ""
                            },
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }
        },
        confirmButton = {
            PosTextButton(
                onClick = {
                    if (!quantityError.value) {
                        onConfirm(quantity.value.toInt())
                    }
                },
            ) {
                Text(stringResource(uiString.core_ui_dialog_ok_button_text))
            }
        },
        dismissButton = {
            PosTextButton(
                onClick = onDismiss,
            ) {
                Text(stringResource(uiString.core_ui_dialog_cancel_button_text))
            }
        },
    )
}