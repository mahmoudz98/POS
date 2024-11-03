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
package com.casecode.pos.feature.profile

import android.content.Context
import android.telephony.TelephonyManager
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.casecode.pos.core.ui.BranchDialogContent
import com.casecode.pos.core.ui.validatePhoneNumber

@Composable
fun AddBranchDialog(
    viewModel: ProfileViewModel = hiltViewModel(),
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current
    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val countryIsoCode = telephonyManager.networkCountryIso.uppercase()
    AddBranchDialog(
        countryIsoCode = countryIsoCode,
        onAddBranch = { branchName, phoneNumber ->
            viewModel.addBranch(branchName, phoneNumber)
        },
        onDismissRequest = onDismissRequest,
    )
}

@Composable
fun AddBranchDialog(
    countryIsoCode: String,
    onAddBranch: (String, String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val configuration = LocalConfiguration.current

    var branchName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    var hasNameError by remember { mutableStateOf(false) }
    var hasPhoneError by remember { mutableStateOf<Int?>(null) }

    val onAddClickTriggered = {
        val validatePhoneNumber = validatePhoneNumber(phoneNumber, countryIsoCode)
        if (branchName.isEmpty() || validatePhoneNumber != null) {
            hasNameError = branchName.isEmpty()
            hasPhoneError = validatePhoneNumber
        } else {
            onAddBranch(branchName, phoneNumber)

            keyboardController?.hide()
            onDismissRequest()
        }
    }
    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 80.dp),
        onDismissRequest = {
            focusRequester.freeFocus()

            keyboardController?.hide()
            onDismissRequest()
        },
        title = {
            Text(
                text = stringResource(com.casecode.pos.core.ui.R.string.core_ui_add_branch_title),
            )
        },
        text = {
            BranchDialogContent(
                branchName = branchName,
                hasNameError = hasNameError,
                onNameChange = {
                    branchName = it
                    hasNameError = it.isEmpty()
                },
                phoneNumber = phoneNumber,
                onPhoneNumberChange = {
                    phoneNumber = it
                    hasPhoneError = validatePhoneNumber(it, countryIsoCode)
                },
                phoneError = hasPhoneError,
                focusRequester = focusRequester,
                onDoneClickTriggered = onAddClickTriggered,
            )
        },
        confirmButton = {
            Button(
                onClick = { onAddClickTriggered() },
            ) {
                Text(text = stringResource(com.casecode.pos.core.ui.R.string.core_ui_add_branch_button_text))
            }
        },
    )
}