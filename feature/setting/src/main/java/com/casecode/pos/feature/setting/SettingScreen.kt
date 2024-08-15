package com.casecode.pos.feature.setting

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.PosOutlinedButton
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme

@Composable
fun SettingRoute(
    viewModel: SettingsViewModel = hiltViewModel(),
    onPrinterClick: () -> Unit,
    onSignOutClick: () -> Unit,
) {
    val user by viewModel.userUiState.collectAsStateWithLifecycle()
    SettingScreen(
        onPrinterClick = onPrinterClick,
        onSignOutClick = onSignOutClick,
        emailUser = user?.email ?: "",
    )
}

@Composable
fun SettingScreen(
    onPrinterClick: () -> Unit,
    modifier: Modifier = Modifier,
    emailUser: String,
    onSignOutClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {

        Column(modifier = Modifier.align(Alignment.TopStart)) {
            LocaleLanguageDropdownMenu()
            Spacer(modifier = modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPrinterClick()},
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.padding(8.dp),
                    imageVector = PosIcons.Print,
                    contentDescription = null,
                )
                Text(
                    text = stringResource(R.string.feature_settings_printer_title),
                )
            }

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocaleLanguageDropdownMenu() {
    val localeOptions = mapOf(
        R.string.feature_setting_language_english to "en",
        R.string.feature_setting_language_arabic to "ar",
    ).mapKeys { stringResource(it.key) }
    val currentLanguageTag =
        ConfigurationCompat.getLocales(LocalConfiguration.current)[0]?.toLanguageTag() ?: ""
    val currentFind = localeOptions.entries.find { it.value == currentLanguageTag.take(2) }?.key
    var currentLanguage by remember { mutableStateOf(currentFind ?: "") }

    // Observe changes in the current locale
    val currentLocale = ConfigurationCompat.getLocales(LocalConfiguration.current)[0]
    LaunchedEffect(currentLocale) {
        val newLanguageTag = currentLocale?.toLanguageTag() ?: ""
        val newLanguage = localeOptions.entries.find { it.value == newLanguageTag.take(2) }?.key
        currentLanguage = newLanguage ?: currentLanguage
    }
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            readOnly = true,
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
            label = { Text(stringResource(R.string.feature_settings_language_current_hint)) },
            value = currentLanguage,

            onValueChange = { },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
        ) {
            localeOptions.keys.forEach { selectionLocale ->
                val isSelected = currentLanguage == selectionLocale

                DropdownMenuItem(
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    onClick = {
                        expanded = false
                        // set app locale given the user's selected locale
                        AppCompatDelegate.setApplicationLocales(
                            LocaleListCompat.forLanguageTags(
                                localeOptions[selectionLocale],
                            ),
                        )
                    },
                    modifier = Modifier.background(if (isSelected) MaterialTheme.colorScheme.outlineVariant else Color.Transparent),
                    text = { Text(selectionLocale) },

                    )
            }
        }
    }
}


@Composable
fun PrinterRow(onClick: () -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = PosIcons.Print,
                contentDescription = stringResource(R.string.feature_setting_add_printer),
                modifier = Modifier
                    .padding(8.dp)
                    .size(24.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.feature_settings_printer_title))
        }
        IconButton(onClick = onClick) {
            Icon(
                imageVector = PosIcons.Add,
                contentDescription = stringResource(R.string.feature_setting_add_printer),
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingPreview() {
    POSTheme {
        SettingScreen(onPrinterClick = {}, onSignOutClick = {}, emailUser = "")
    }

}