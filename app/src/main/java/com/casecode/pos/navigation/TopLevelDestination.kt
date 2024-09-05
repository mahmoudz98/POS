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
package com.casecode.pos.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.casecode.pos.R
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.ui.R.string.core_ui_employees_title

enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val titleTextId: Int,
) {
    POS(PosIcons.Pos, PosIcons.Pos, R.string.pos),
    REPORTS(PosIcons.Reports, PosIcons.Reports, R.string.reports_title),

    ITEMS(
        PosIcons.Items,
        PosIcons.Items,
        R.string.menu_items,
    ),
    EMPLOYEES(
        PosIcons.Employee,
        PosIcons.Employee,
        core_ui_employees_title,
    ),
    SETTINGS(
        PosIcons.Settings,
        PosIcons.Settings,
        R.string.settings_title,
    ),
}