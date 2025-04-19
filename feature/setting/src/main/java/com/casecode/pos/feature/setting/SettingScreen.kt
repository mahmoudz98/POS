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
package com.casecode.pos.feature.setting

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.PosExposeDropdownMenuBox
import com.casecode.pos.core.designsystem.component.PosOutlinedButton
import com.casecode.pos.core.designsystem.component.PosTextButton
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.ui.R as uiR

@Composable
fun SettingScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onEmployeesScreenClick: () -> Unit,
    onPrinterScreenClick: () -> Unit,
    onSignOutClick: () -> Unit,
) {
    val user by viewModel.userUiState.collectAsStateWithLifecycle()
    SettingScreen(
        onEmployeesClick = onEmployeesScreenClick,
        onPrintersClick = onPrinterScreenClick,
        onSignOutClick = onSignOutClick,
        emailUser = user?.email ?: "",
    )
}

@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    emailUser: String,
    onEmployeesClick: () -> Unit,
    onPrintersClick: () -> Unit,
    onSignOutClick: () -> Unit,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        Column(modifier = Modifier.align(Alignment.TopStart)) {
            SectionLanguages()
            Spacer(modifier = modifier.height(16.dp))
            PosTextButton(
                onClick = onEmployeesClick,
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(uiR.string.core_ui_employees_title),
                leadingIcon = PosIcons.Employee,
            )
            PosTextButton(
                onClick = onPrintersClick,
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.feature_settings_printer_title),
                leadingIcon = PosIcons.Print,
            )
        }
        Column(modifier = Modifier.align(Alignment.BottomStart)) {
            Text(text = emailUser, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)

            PosOutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onSignOutClick,
                text = { Text(stringResource(R.string.feature_setting_sign_out_button_text)) },
            )
        }
    }
}

@Composable
private fun SectionLanguages() {
    val localeOptions =
        mapOf(
            R.string.feature_setting_language_english to "en",
            R.string.feature_setting_language_arabic to "ar",
        ).mapKeys { stringResource(it.key) }
    val currentLanguageTag =
        ConfigurationCompat.getLocales(LocalConfiguration.current)[0]?.toLanguageTag() ?: ""
    val currentFind = localeOptions.entries.find { it.value == currentLanguageTag.take(2) }?.key
    var currentLanguage by remember { mutableStateOf(currentFind ?: "") }
    PosExposeDropdownMenuBox(
        currentItemSelected = currentLanguage,
        items = localeOptions.keys.toList(),
        onClickItem = {
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(localeOptions[it]),
            )
        },
        label = stringResource(R.string.feature_settings_language_current_hint),
    )
    val currentLocale = ConfigurationCompat.getLocales(LocalConfiguration.current)[0]
    LaunchedEffect(currentLocale) {
        val newLanguageTag = currentLocale?.toLanguageTag() ?: ""
        val newLanguage = localeOptions.entries.find { it.value == newLanguageTag.take(2) }?.key
        currentLanguage = newLanguage ?: currentLanguage
    }
}

@Preview(showBackground = true)
@Composable
fun SettingPreview() {
    POSTheme {
        SettingScreen(
            onEmployeesClick = {},
            onPrintersClick = {},
            onSignOutClick = {},
            emailUser = "",
        )
    }
}