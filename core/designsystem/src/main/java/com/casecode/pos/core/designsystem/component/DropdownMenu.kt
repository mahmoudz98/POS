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
package com.casecode.pos.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.util.fastFilter

/**
 * A composable function that displays a dropdown menu using ExposedDropdownMenuBox.
 *
 * @param currentItemSelected The currently selected item in the dropdown menu.
 * @param items A list of strings representing the items in the dropdown menu.
 * @param onClickItem A lambda function that is called when an item in the dropdown menu is clicked.
 *                       The selected item is passed as an argument to the lambda function.
 * @param label The label to display for the dropdown menu.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PosExposeDropdownMenuBox(
    currentItemSelected: String,
    items: List<String>,
    onClickItem: (String) -> Unit,
    label: String,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        PosOutlinedTextField(
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
            readOnly = true,
            label = label,
            value = currentItemSelected,
            onValueChange = { onClickItem(it) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },

        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
        ) {
            items.forEach { selectionLocale ->
                val isSelected = currentItemSelected == selectionLocale

                DropdownMenuItem(
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    onClick = {
                        onClickItem(selectionLocale)

                        expanded = false
                    },
                    modifier = Modifier.background(if (isSelected) MaterialTheme.colorScheme.outlineVariant else Color.Transparent),
                    text = { Text(selectionLocale) },
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PosExposeDropdownMenuBox(
    currentText: String,
    items: List<String>,
    onClickItem: (String) -> Unit,
    label: String,
    menuAnchorType: MenuAnchorType = MenuAnchorType.PrimaryNotEditable,
    readOnly: Boolean = false,
    keyboardOption: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: () -> Unit,
) {
    val filterItems = items.fastFilter { it.contains(currentText, ignoreCase = true) }
    val (allowExpanded, setExpanded) = remember { mutableStateOf(false) }

    val expanded = allowExpanded && filterItems.isNotEmpty()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = setExpanded,
    ) {
        PosOutlinedTextField(
            modifier = Modifier
                .menuAnchor(menuAnchorType)
                .fillMaxWidth(),
            readOnly = readOnly,

            label = label,
            value = currentText,
            keyboardOptions = keyboardOption,
            keyboardActions = KeyboardActions(
                onNext = {
                    onKeyboardAction()
                    setExpanded(false)
                },
                onDone = {
                    onKeyboardAction()
                    setExpanded(false)
                },
            ),

            onValueChange = { onClickItem(it) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },

            )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                setExpanded(false)
            },
        ) {
            filterItems.forEach { selectionLocale ->
                val isSelected = currentText == selectionLocale

                DropdownMenuItem(
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    onClick = {
                        onClickItem(selectionLocale)
                        setExpanded(false)
                    },
                    modifier = Modifier.background(if (isSelected) MaterialTheme.colorScheme.outlineVariant else Color.Transparent),
                    text = { Text(selectionLocale) },
                )
            }
        }
    }
}