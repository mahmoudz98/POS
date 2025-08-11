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
package com.casecode.pos.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.casecode.pos.core.designsystem.component.PosEmptyScreen

@Composable
fun EmployeeEmptyScreen(modifier: Modifier = Modifier) {
    PosEmptyScreen(
        modifier = modifier,
        imageRes = R.drawable.core_ui_ic_outline_inventory_120,
        titleRes = R.string.core_ui_employees_empty_title,
        messageRes = R.string.core_ui_employees_empty_message,
    )
}