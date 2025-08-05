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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosOutlinedTextField
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.ui.business.toDisplayString
import com.casecode.pos.core.ui.utils.isAppInArabic
import com.casecode.pos.feature.onboarding.OnboardingEvent
import com.casecode.pos.feature.onboarding.OnboardingUiState
import com.casecode.pos.core.ui.R.string as uiString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BusinessInfoStep(
    uiState: OnboardingUiState,
    countryIsoCode: String,
    modifier: Modifier = Modifier,
    onEvent: (OnboardingEvent) -> Unit,
) {
    var businessVerticalExpanded by remember { mutableStateOf(false) }
    var countryExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        PosOutlinedTextField(
            value = uiState.onboardingData.businessName,
            onValueChange = { onEvent(OnboardingEvent.BusinessNameChanged(it)) },
            label = stringResource(id = uiString.core_ui_business_name_hint),
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.onboardingData.businessNameError != null,
            supportingText = uiState.onboardingData.businessNameError?.let { stringResource(it) },
        )

        ExposedDropdownMenuBox(
            expanded = businessVerticalExpanded,
            onExpandedChange = { businessVerticalExpanded = !businessVerticalExpanded },
            modifier = Modifier.fillMaxWidth(),
        ) {
            PosOutlinedTextField(
                readOnly = true,
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                label = stringResource(id = uiString.core_ui_business_vertical_hint),
                value = uiState.onboardingData.businessVertical?.name ?: "",
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = businessVerticalExpanded) },
                onValueChange = {},
            )
            ExposedDropdownMenu(
                expanded = businessVerticalExpanded,
                onDismissRequest = { businessVerticalExpanded = false },
            ) {
                uiState.availableVerticals.forEach { vertical ->
                    DropdownMenuItem(
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        onClick = {
                            onEvent(OnboardingEvent.BusinessVerticalSelected(vertical))
                            businessVerticalExpanded = false
                        },
                        text = { Text(vertical.toDisplayString()) },
                    )
                }
            }
        }

        PosOutlinedTextField(
            value = uiState.onboardingData.email,
            onValueChange = { onEvent(OnboardingEvent.EmailChanged(it)) },
            label = stringResource(id = uiString.core_ui_work_email_hint),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
            isError = uiState.onboardingData.emailError != null,
            supportingText = uiState.onboardingData.emailError?.let { stringResource(it) },
        )
        PosOutlinedTextField(
            leadingIcon = {
                ExposedDropdownMenuBox(
                    expanded = countryExpanded,
                    onExpandedChange = { countryExpanded = !countryExpanded },
                    modifier = Modifier
                        .widthIn(max = 140.dp)
                        .heightIn(max = 48.dp),
                ) {
                    Row {
                        Text(
                            text = uiState.countrySelected?.phoneCode ?: "",
                            modifier = Modifier.padding(start = 12.dp),
                        )
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = countryExpanded,
                            modifier = Modifier
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                .padding(end = 8.dp),
                        )
                    }
                    ExposedDropdownMenu(
                        expanded = countryExpanded,
                        onDismissRequest = { countryExpanded = false },
                    ) {
                        uiState.availableCountries.forEach { country ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "${if (isAppInArabic()) country.nameAr else country.nameEn} (${country.phoneCode})",
                                        style = MaterialTheme.typography.labelSmall,
                                    )
                                },
                                onClick = {
                                    onEvent(OnboardingEvent.CountrySelected(country))
                                    countryExpanded = false
                                },
                            )
                        }
                    }
                }
            },
            value = uiState.onboardingData.phone,
            onValueChange = { onEvent(OnboardingEvent.PhoneChanged(it)) },
            label = stringResource(id = uiString.core_ui_work_phone_number_hint),
            modifier = Modifier
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done,
            ),
            isError = uiState.onboardingData.phoneError != null,
            supportingText = uiState.onboardingData.phoneError?.let { stringResource(it) },
        )
    }
    LaunchedEffect(Unit) {
        if (uiState.countrySelected == null) {
            uiState.availableCountries.find { it.countryCode == countryIsoCode }
                .also { uiState.availableCountries[0] }
                ?.let { onEvent(OnboardingEvent.CountrySelected(it)) }
        }
    }
}

@Preview
@Composable
fun BusinessInfoStepPreview() {
    POSTheme {
        PosBackground {
            BusinessInfoStep(
                uiState = OnboardingUiState(),
                countryIsoCode = "",
                onEvent = {},
            )
        }
    }
}