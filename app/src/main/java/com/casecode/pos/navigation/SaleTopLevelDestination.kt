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

enum class SaleTopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val titleTextId: Int,
    val iconTextId: Int,
) {
    POS(
        selectedIcon = PosIcons.Pos,
        unselectedIcon = PosIcons.PosBorder,
        titleTextId = R.string.app_name,
        iconTextId = R.string.pos,
    ),
    REPORTS(
        selectedIcon = PosIcons.Reports,
        unselectedIcon = PosIcons.ReportsBorder,
        titleTextId = R.string.reports_title,
        iconTextId = R.string.reports_title,
    ),

    SETTING(
        selectedIcon = PosIcons.Settings,
        unselectedIcon = PosIcons.SettingsBorder,
        titleTextId = R.string.settings_title,
        iconTextId = R.string.settings_title,
    ),
}