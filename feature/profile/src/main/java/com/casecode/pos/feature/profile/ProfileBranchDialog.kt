package com.casecode.pos.feature.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.casecode.pos.core.designsystem.component.PosOutlinedTextField

@Composable
fun AddBranchDialog(
    viewModel: ProfileViewModel = hiltViewModel(),
    onDismissRequest: () -> Unit,
) {
    var branchName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    val nameError = remember { mutableStateOf(false) }
    val phoneError = remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = stringResource(com.casecode.pos.core.ui.R.string.core_ui_add_branch_title),
            )
        },
        text = {
            Column {
                PosOutlinedTextField(
                    value = branchName,
                    onValueChange = {
                        branchName = it
                        nameError.value = it.isEmpty()
                    },
                    isError = nameError.value,
                    label = stringResource(id = com.casecode.pos.core.ui.R.string.core_ui_branch_name_hint),
                    supportingText =
                        if (nameError.value) {
                            stringResource(
                                com.casecode.pos.core.ui.R.string.core_ui_error_branch_name_empty,
                            )
                        } else {
                            null
                        },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                PosOutlinedTextField(
                    value = phoneNumber,
                    onValueChange = {
                        phoneNumber = it
                        phoneError.value = it.isEmpty()
                    },
                    isError = phoneError.value,
                    supportingText =
                        if (nameError.value) {
                            stringResource(
                                com.casecode.pos.core.ui.R.string.core_ui_error_phone_empty,
                            )
                        } else {
                            null
                    },
                    label = stringResource(id = com.casecode.pos.core.ui.R.string.core_ui_error_phone_invalid),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (branchName.isEmpty() || phoneNumber.isEmpty()) {
                        nameError.value = branchName.isEmpty()
                        phoneError.value = phoneNumber.isEmpty()
                    } else {
                        viewModel.addBranch(branchName, phoneNumber)
                        onDismissRequest()
                    }
                },
            ) {
                Text(text = stringResource(com.casecode.pos.core.ui.R.string.core_ui_add_branch_button_text))
            }
        },
    )
}