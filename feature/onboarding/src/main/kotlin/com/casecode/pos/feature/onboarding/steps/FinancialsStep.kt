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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosOutlinedTextField
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.business.TaxRate
import com.casecode.pos.core.ui.utils.isAppInArabic
import com.casecode.pos.feature.onboarding.OnboardingEvent
import com.casecode.pos.feature.onboarding.OnboardingUiState
import com.casecode.pos.core.ui.R.string as uiString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FinancialsStep(
    uiState: OnboardingUiState,
    modifier: Modifier = Modifier,
    onEvent: (OnboardingEvent) -> Unit,
) {
    var currencyExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        // Currency Dropdown
        ExposedDropdownMenuBox(
            expanded = currencyExpanded,
            onExpandedChange = { currencyExpanded = !currencyExpanded },
            modifier = Modifier.fillMaxWidth(),
        ) {
            PosOutlinedTextField(
                readOnly = true,
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                label = stringResource(id = uiString.core_ui_currency_hint),
                value = uiState.onboardingData.selectedCurrency?.nameEn ?: "",
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = currencyExpanded) },
                onValueChange = {},
                isError = uiState.onboardingData.currencyError != null,
                supportingText = uiState.onboardingData.currencyError?.let { stringResource(it) },
            )
            ExposedDropdownMenu(
                expanded = currencyExpanded,
                onDismissRequest = { currencyExpanded = false },
            ) {
                uiState.availableCurrencies.forEach { currency ->
                    DropdownMenuItem(
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        onClick = {
                            onEvent(OnboardingEvent.CurrencySelected(currency))
                            currencyExpanded = false
                        },
                        text = { Text(if (isAppInArabic()) currency.nameAr else currency.nameEn) },
                    )
                }
            }
        }

        PosOutlinedTextField(
            value = uiState.onboardingData.taxRate?.rate?.toString() ?: "",
            onValueChange = {
                val taxRate = TaxRate(
                    name = "VAT",
                    rate = it.toFloat(),
                    isDefault = true,
                )
                onEvent(OnboardingEvent.TaxRateSet(taxRate))
            },
            label = stringResource(id = uiString.core_ui_tax_rate_hint),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
            ),
            isError = uiState.onboardingData.taxRateError != null,
            supportingText = uiState.onboardingData.taxRateError?.let { stringResource(it) },
        )
    }
}

@Preview
@Composable
fun FinancialsStepPreview() {
    POSTheme {
        PosBackground {
            FinancialsStep(
                uiState = OnboardingUiState(),
                onEvent = {},
            )
        }
    }
}