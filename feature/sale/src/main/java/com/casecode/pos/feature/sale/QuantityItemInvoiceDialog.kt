package com.casecode.pos.feature.sale

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun QuantityDialog(
    oldQuantity: Double,
    inStock: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit,
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
                    onValueChange = {quantity.value = it

                        quantityError.value = it.isEmpty() || it.toDouble() > inStock || it.toDouble() <= 0
                                    },
                    isError = quantityError.value,
                    label = { Text(text = stringResource(com.casecode.pos.core.ui.R.string.core_ui_item_quantity_format)) },
                    supportingText ={ Text(if (quantityError.value)
                    {
                        when{
                            quantity.value.toDouble() <= 0.0-> stringResource(R.string.feature_sale_error_quantity_greater_than_zero)
                            quantity.value.toDouble() > inStock -> stringResource(R.string.feature_sale_error_quantity_less_than) + inStock
                            else -> ""
                        }
                    } else "")},

                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if(!quantityError.value){
                        onConfirm(quantity.value.toDouble())
                    }
                },
            ) {
                Text(stringResource(com.casecode.pos.core.ui.R.string.core_ui_dialog_ok_button_text))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text(stringResource(com.casecode.pos.core.ui.R.string.core_ui_dialog_cancel_button_text))
            }
        },
    )
}