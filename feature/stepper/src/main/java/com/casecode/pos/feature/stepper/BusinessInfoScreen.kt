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
package com.casecode.pos.feature.stepper

import android.content.Context
import android.telephony.TelephonyManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosOutlinedTextField
import com.casecode.pos.core.designsystem.component.PosTextButton
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.ui.validateEmail
import com.casecode.pos.core.ui.validatePhoneNumber
import com.casecode.pos.core.ui.R.string as uiString

@Composable
fun BusinessInfoScreen(viewModel: StepperBusinessViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val countryIsoCode = telephonyManager.networkCountryIso.uppercase()
    BusinessInfoScreen(
        uiState = uiState,
        countryIsoCode = countryIsoCode,
    ) { storeType, email, phone ->
        viewModel.setBusinessInfo(storeType, email, phone)
        viewModel.nextStep()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BusinessInfoScreen(
    modifier: Modifier = Modifier,
    uiState: StepperBusinessUiState,
    countryIsoCode: String,
    onNextButtonClick: (storeType: String, email: String, phone: String) -> Unit,
) {
    var storeType by rememberSaveable { mutableStateOf(uiState.storeType) }
    var email by rememberSaveable { mutableStateOf(uiState.emailBusiness) }
    var phone by rememberSaveable { mutableStateOf(uiState.phoneBusiness) }
    var storeTypeError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<Int?>(null) }
    var phoneError by remember { mutableStateOf<Int?>(null) }
    var expanded by remember { mutableStateOf(false) }
    val storeTypes = stringArrayResource(R.array.feature_stepper_business_store_types)
    Column(
        modifier =
        modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier =
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
            ) {
                PosOutlinedTextField(
                    readOnly = true,
                    modifier =
                    Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth(),
                    label = stringResource(id = uiString.core_ui_store_type_hint),
                    value = storeType,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded,
                        )
                    },
                    onValueChange = { },
                    isError = storeTypeError,
                    supportingText =
                    if (storeTypeError) {
                        stringResource(
                            id = R.string.feature_stepper_error_add_business_store_type_empty_message,
                        )
                    } else {
                        null
                    },
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    },
                ) {
                    storeTypes.forEach { type ->
                        DropdownMenuItem(
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            onClick = {
                                storeType = type
                                expanded = false
                                storeTypeError = type.isEmpty()
                            },
                            text = { Text(type) },
                        )
                    }
                }
            }

            PosOutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = validateEmail(it)
                },
                label = stringResource(id = uiString.core_ui_work_email_hint),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = PosIcons.Email,
                        contentDescription = null,
                    )
                },
                keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
                isError = emailError != null,
                supportingText = emailError?.let { stringResource(it) },
            )
            PosOutlinedTextField(
                value = phone,
                onValueChange = {
                    phone = it
                    phoneError = validatePhoneNumber(it, countryIsoCode)
                },
                keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done,
                ),
                label = stringResource(id = uiString.core_ui_work_phone_number_hint),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = PosIcons.Phone,
                        contentDescription = null,
                    )
                },
                isError = phoneError != null,
                supportingText = phoneError?.let { stringResource(it) },
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            PosTextButton(
                onClick = {
                    val validateEmail = validateEmail(email)
                    val validatePhoneNumber = validatePhoneNumber(phone, countryIsoCode)
                    if (storeType.isEmpty() || validateEmail != null || validatePhoneNumber != null) {
                        storeTypeError = storeType.isEmpty()
                        emailError = validateEmail
                        phoneError = validatePhoneNumber
                    } else {
                        onNextButtonClick(storeType, email, phone)
                    }
                },
                text = { Text(stringResource(id = R.string.feature_stepper_next_button_text)) },
                trainingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                        contentDescription = null,
                    )
                },
                modifier =
                Modifier
                    .wrapContentSize(),
            )
        }
    }
}

@com.casecode.pos.core.ui.DevicePreviews
@Composable
fun BusinessInfoScreenPreview() {
    POSTheme {
        PosBackground {
            BusinessInfoScreen(
                uiState = StepperBusinessUiState(),
                countryIsoCode = "us",
                onNextButtonClick = { _, _, _ -> },
            )
        }
    }
}