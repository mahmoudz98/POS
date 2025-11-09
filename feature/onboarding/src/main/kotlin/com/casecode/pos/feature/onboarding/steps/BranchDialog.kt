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
package com.casecode.pos.feature.onboarding.steps

import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowDpSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.casecode.pos.core.ui.BranchDialogContent
import com.casecode.pos.core.ui.utils.validatePhoneNumber
import com.casecode.pos.core.ui.R.string as uiString

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun BranchDialog(
    isoCode: String,
    onAddBranch: (branchName: String, phoneNumber: String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var branchName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf<Int?>(null) }
    val currentSize = currentWindowDpSize()

    val onDoneClickTriggered = {
        val validatePhoneNumber = validatePhoneNumber(phoneNumber, isoCode)
        if (branchName.isEmpty() || validatePhoneNumber != null) {
            nameError = branchName.isEmpty()
            phoneError = validatePhoneNumber
        } else {
            onAddBranch(branchName, phoneNumber)
            keyboardController?.hide()
            onDismissRequest()
        }
    }

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = currentSize.width - 80.dp),
        onDismissRequest = {
            focusRequester.freeFocus()
            keyboardController?.hide()
            onDismissRequest()
        },
        title = {
            Text(text = stringResource(uiString.core_ui_add_branch_title))
        },
        text = {
            BranchDialogContent(
                branchName = branchName,
                hasNameError = nameError,
                onNameChange = {
                    branchName = it
                    nameError = it.isEmpty()
                },
                phoneNumber = phoneNumber,
                onPhoneNumberChange = {
                    phoneNumber = it
                    phoneError = validatePhoneNumber(it, isoCode)
                },
                phoneError = phoneError,
                focusRequester = focusRequester,
                onDoneClickTriggered = onDoneClickTriggered,
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onDoneClickTriggered() },
            ) {
                Text(text = stringResource(uiString.core_ui_add_branch_button_text))
            }
        },
    )
}
