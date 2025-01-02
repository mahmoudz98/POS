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
package com.casecode.pos.core.designsystem.component

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation

/**
 * A composable function that provides a filled text field with Point of Sale (POS) styling.
 *
 * This text field is designed to be used in POS systems and has a transparent background.
 *
 * @param modifier The modifier to be applied to the text field.
 * @param value The current value of the text field.
 * @param onValueChange A callback that is invoked when the text field's value changes.
 * @param isError Indicates if the text field is in an error state. Defaults to false.
 * @param readOnly Indicates if the text field is read-only. Defaults to false.
 * @param keyboardOptions Software keyboard options that contains configuration such as the type of keyboard to be used.
 * @param keyboardActions When the enter key is pressed, the specified action will be invoked.
 * @param label The optional label to be displayed in the text field.
 */
@Composable
fun PosFilledTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    label: String? = null,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        readOnly = readOnly,
        isError = isError,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        label = label?.let { { Text(text = it) } },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
        ),
    )
}

/**
 * A reusable composable function that provides a styled OutlinedTextField for use in a Point of Sale (POS) system.
 *
 * This function encapsulates the common styling and functionality of an OutlinedTextField, making it easier to use
 * consistently throughout a POS application.
 *
 * @param modifier The modifier to be applied to the OutlinedTextField.
 * @param value The current value of the text field.
 * @param onValueChange A callback that is invoked when the text field's value changes.
 * @param isError A boolean indicating whether the text field is in an error state.
 * @param label The label to be displayed for the text field.
 * @param keyboardOptions The keyboard options to be used for the text field. Defaults to [KeyboardOptions.Default].
 * @param leadingIcon An optional composable function that provides a leading icon for the text field.
 * @param supportingText An optional string to be displayed as supporting text below the text field.
 *  This text is only shown if [isError] is true.
 */
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

/**
 * A custom composable function that wraps Jetpack Compose's `OutlinedTextField` with
 * some default styling and functionality suitable for a Point of Sale (POS) system.
 *
 * @param modifier Modifier to be applied to the OutlinedTextField.
 * @param value The current text value of the TextField.
 * @param onValueChange Callback function that is triggered when the text value changes.
 * @param isError Indicates if the TextField is in an error state. Defaults to false.
 * @param readOnly Indicates if the TextField is read-only. Defaults to false.
 * @param label The label to be displayed for the TextField.
 * @param enabled Controls the enabled state of the TextField. Defaults to true.
 * @param keyboardOptions Keyboard options to be applied to the TextField. Defaults to KeyboardOptions.Default.
 * @param keyboardActions Keyboard actions to be applied to the TextField. Defaults to KeyboardActions.Default.
 * @param visualTransformation The visual transformation to be applied to the text. Defaults to VisualTransformation.None.
 * @param leadingIcon Composable function to display a leading icon in the TextField.
 * @param trailingIcon Composable function to display a trailing icon in the TextField.
 * @param supportingText Text to be displayed below the TextField as supporting information.
 */
@Composable
fun PosOutlinedTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    readOnly: Boolean = false,
    label: String,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: String? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = isError,
        readOnly = readOnly,
        enabled = enabled,
        singleLine = true,
        supportingText = {
            if (supportingText != null) {
                Text(supportingText)
            }
        },
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        keyboardOptions = keyboardOptions,
        modifier = modifier,
    )
}