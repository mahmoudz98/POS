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

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosExposedDropdownMenuBox(
    label: @Composable () -> Unit,
    readOnly: Boolean = false,
    menuAnchorType: MenuAnchorType = MenuAnchorType.PrimaryNotEditable,
    currentSelectedValue: String,
    items: List<String>,
    actionOnClick: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded != expanded }) {
        OutlinedTextField(
            modifier =
            Modifier
                .menuAnchor(menuAnchorType)
                .fillMaxWidth(),
            label = label,
            readOnly = readOnly,
            value = currentSelectedValue,
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { selectedItem ->
                DropdownMenuItem(
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    onClick = {
                        expanded = false
                        actionOnClick(selectedItem)
                    },
                    text = { Text(selectedItem) },
                )
            }
        }
    }
}