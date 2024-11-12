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
import com.casecode.pos.feature.employee.EmployeesRoute
import com.casecode.pos.feature.inventory.navigation.InventoryRoute
import com.casecode.pos.feature.sale.navigation.SaleRoute
import com.casecode.pos.feature.setting.SettingRoute
import com.casecode.pos.feature.statistics.ReportsRoute
import kotlin.reflect.KClass
import com.casecode.pos.core.ui.R as uiR
import com.casecode.pos.feature.inventory.R as inventoryR
import com.casecode.pos.feature.reports.R as reportsR
import com.casecode.pos.feature.setting.R as settingR

enum class AdminTopLevelDestination(
    override val selectedIcon: ImageVector,
    override val unselectedIcon: ImageVector,
    override val titleTextId: Int,
    override val route: KClass<*>,
) : TopLevelDestination {
    POS(PosIcons.Pos, PosIcons.Pos, R.string.pos, SaleRoute::class),
    REPORTS(
        PosIcons.Reports,
        PosIcons.Reports,
        reportsR.string.feature_reports_title,
        ReportsRoute::class,
    ),
    INVENTORY(PosIcons.Inventory, PosIcons.InventoryBorder, inventoryR.string.feature_inventory_title, InventoryRoute::class),
    EMPLOYEES(
        PosIcons.Employee,
        PosIcons.Employee,
        uiR.string.core_ui_employees_title,
        EmployeesRoute::class,
    ),
    SETTINGS(
        PosIcons.Settings,
        PosIcons.Settings,
        settingR.string.feature_setting_title,
        SettingRoute::class,
    ),
}