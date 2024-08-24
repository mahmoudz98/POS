package com.casecode.pos.feature.stepper.branches

import android.content.Context
import android.telephony.TelephonyManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosOutlinedTextField
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.data.users.Branch
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
            Column {
                PosOutlinedTextField(
                    value = branchName,
                    onValueChange = {
                        branchName = it
                        nameError = it.isEmpty()
                    },
                    isError = nameError,
                    label = stringResource(id = uiString.core_ui_branch_name_hint),
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next,
                        ),
                    supportingText = if (nameError) stringResource(uiString.core_ui_error_branch_name_empty) else null,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                )
                Spacer(modifier = Modifier.height(8.dp))
                PosOutlinedTextField(
                    value = phoneNumber,
                    onValueChange = {
                        phoneNumber = it
                        phoneError = validatePhoneNumber(it, countryIsoCode)
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
                    label = stringResource(id = uiString.core_ui_hint_phone_number_branch),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            Button(
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

@com.casecode.pos.core.ui.DevicePreviews
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

@com.casecode.pos.core.ui.DevicePreviews
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