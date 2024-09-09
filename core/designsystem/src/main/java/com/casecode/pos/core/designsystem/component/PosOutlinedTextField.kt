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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation

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
    readOnly: Boolean = false,
    label: String,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon:
    @Composable()
    (() -> Unit)? = null,
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
            if (supportingText != null && isError) {
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