package com.casecode.pos.design.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun PosOutlinedTextField(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    isError: Boolean = false,
    label: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    leadingIcon: @Composable (() -> Unit)? = null,
    supportingText: String? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = isError,
        singleLine = true,
        supportingText = {
            if (supportingText != null && isError) {
                Text(supportingText)
            }
        },
        leadingIcon = leadingIcon,
        keyboardOptions = keyboardOptions,
        modifier = modifier,
    )
}
@Composable
fun PosOutlinedTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    label: String,
    enabled:Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    leadingIcon: @Composable (() -> Unit)? = null,
    supportingText: String? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = isError,
        enabled = enabled,
        singleLine = true,
        supportingText = {
            if (supportingText != null && isError) {
                Text(supportingText)
            }
        },
        leadingIcon = leadingIcon,
        keyboardOptions = keyboardOptions,
        modifier = modifier,
    )
}