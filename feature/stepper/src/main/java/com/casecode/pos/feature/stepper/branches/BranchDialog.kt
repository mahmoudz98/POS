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
package com.casecode.pos.feature.stepper.branches

import android.content.Context
import android.telephony.TelephonyManager
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosTextButton
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.data.users.Branch
import com.casecode.pos.core.ui.BranchDialogContent
import com.casecode.pos.core.ui.DevicePreviews
import com.casecode.pos.core.ui.validatePhoneNumber
import com.casecode.pos.feature.stepper.StepperBusinessViewModel
import com.casecode.pos.core.ui.R.string as uiString

@Composable
fun BranchDialog(
    viewModel: StepperBusinessViewModel = hiltViewModel(),
    isUpdate: Boolean = false,
    onDismissRequest: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val countryIsoCode = telephonyManager.networkCountryIso.uppercase()
    BranchDialog(
        branchSelected = uiState.branchSelected,
        isUpdate = isUpdate,
        countryIsoCode = countryIsoCode,
        onClick = { branchName, phoneNumber ->
            if (isUpdate) {
                viewModel.updateBranch(branchName, phoneNumber)
            } else {
                viewModel.addBranch(branchName, phoneNumber)
            }
        },
        onDismissRequest = onDismissRequest,
    )
}

@Composable
private fun BranchDialog(
    modifier: Modifier = Modifier,
    branchSelected: Branch,
    countryIsoCode: String,
    isUpdate: Boolean,
    onClick: (String, String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val configuration = LocalConfiguration.current

    var branchName by remember { mutableStateOf(if (isUpdate) branchSelected.branchName else "") }
    var phoneNumber by remember { mutableStateOf(if (isUpdate) branchSelected.phoneNumber else "") }
    var nameError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf<Int?>(null) }
    val onDoneClickTriggered = {
        val validatePhoneNumber = validatePhoneNumber(phoneNumber, countryIsoCode)
        if (branchName.isEmpty() || validatePhoneNumber != null) {
            nameError = branchName.isEmpty()
            phoneError = validatePhoneNumber
        } else {
            onClick(branchName, phoneNumber)
            keyboardController?.hide()
            onDismissRequest()
        }
    }

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = modifier.widthIn(max = configuration.screenWidthDp.dp - 80.dp),
        onDismissRequest = {
            focusRequester.freeFocus()
            keyboardController?.hide()
            onDismissRequest()
        },
        title = {
            Text(
                text = stringResource(if (isUpdate) uiString.core_ui_update_branch_title else uiString.core_ui_add_branch_title),
            )
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
                    phoneError = validatePhoneNumber(it, countryIsoCode)
                },
                phoneError = phoneError,
                focusRequester = focusRequester,
                onDoneClickTriggered = onDoneClickTriggered,
            )
        },
        confirmButton = {
            PosTextButton(
                onClick = {
                    onDoneClickTriggered()
                },
            ) {
                Text(
                    text =
                    stringResource(
                        if (isUpdate) uiString.core_ui_update_branch_button_text else uiString.core_ui_add_branch_button_text,
                    ),
                )
            }
        },
    )
}

@DevicePreviews
@Composable
fun BranchDialogPreview() {
    POSTheme {
        PosBackground {
            BranchDialog(
                branchSelected = Branch(),
                countryIsoCode = "",
                isUpdate = false,
                onClick = { _, _ -> },
                onDismissRequest = {},
            )
        }
    }
}

@DevicePreviews
@Composable
fun BranchUpdateDialogPreview() {
    POSTheme {
        PosBackground {
            BranchDialog(
                branchSelected =
                Branch(
                    branchCode = 1,
                    branchName = "branch1",
                    phoneNumber = "0000000000",
                ),
                countryIsoCode = "",
                isUpdate = true,
                onClick = { _, _ -> },
                onDismissRequest = {},
            )
        }
    }
}