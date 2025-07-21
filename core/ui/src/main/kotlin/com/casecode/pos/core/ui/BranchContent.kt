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
package com.casecode.pos.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.casecode.pos.core.designsystem.component.PosOutlinedTextField

@Composable
fun BranchDialogContent(
    branchName: String,
    hasNameError: Boolean,
    onNameChange: (String) -> Unit,
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    phoneError: Int?,
    focusRequester: FocusRequester,
    onDoneClickTriggered: () -> Unit,
) {
    Column {
        PosOutlinedTextField(
            value = branchName,
            onValueChange = {
                onNameChange(it)
            },
            isError = hasNameError,
            label = stringResource(id = R.string.core_ui_branch_name_hint),
            keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            ),
            supportingText = if (hasNameError) {
                stringResource(
                    R.string.core_ui_error_branch_name_empty,
                )
            } else {
                null
            },
            modifier =
            Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
        )
        Spacer(modifier = Modifier.height(8.dp))
        PosOutlinedTextField(
            value = phoneNumber,
            onValueChange = {
                onPhoneNumberChange(it)
            },
            isError = phoneError != null,
            supportingText = phoneError?.let { stringResource(it) },
            keyboardActions =
            KeyboardActions(
                onDone = {
                    onDoneClickTriggered()
                },
            ),
            keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done,
            ),
            label = stringResource(id = R.string.core_ui_hint_phone_number_branch),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}