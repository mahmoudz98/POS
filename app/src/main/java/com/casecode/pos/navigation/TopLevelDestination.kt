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

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.casecode.pos.R
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.ui.R.string.core_ui_employees_title
import com.casecode.pos.feature.employee.EmployeesRoute
import com.casecode.pos.feature.employee.navigateToEmployees
import com.casecode.pos.feature.item.navigation.ItemsGraph
import com.casecode.pos.feature.item.navigation.ItemsSaleGraph
import com.casecode.pos.feature.item.navigation.navigateToItemsGraph
import com.casecode.pos.feature.item.navigation.navigateToItemsSaleGraph
import com.casecode.pos.feature.sale.SaleRoute
import com.casecode.pos.feature.sale.navigateToSale
import com.casecode.pos.feature.setting.SettingGraph
import com.casecode.pos.feature.setting.SettingRoute
import com.casecode.pos.feature.setting.navigateToSettings
import com.casecode.pos.feature.statistics.ReportsRoute
import com.casecode.pos.feature.statistics.navigateToReports
import kotlin.reflect.KClass

interface TopLevelDestination {
    val selectedIcon: ImageVector
    val unselectedIcon: ImageVector
    val titleTextId: Int
    val route: KClass<*>

    fun navigate(navController: NavHostController, navOptions: NavOptions) {
        navigateTo(navController, navOptions)
    }
}

private fun TopLevelDestination.navigateTo(
    navController: NavHostController,
    navOptions: NavOptions,
) {
    when (this.route) {
        SaleRoute::class -> navController.navigateToSale(navOptions)
        ReportsRoute::class -> navController.navigateToReports(navOptions)
        ItemsGraph::class -> navController.navigateToItemsGraph(navOptions)
        ItemsSaleGraph::class -> navController.navigateToItemsSaleGraph(navOptions)
        EmployeesRoute::class -> navController.navigateToEmployees(navOptions)
        SettingRoute::class -> navController.navigateToSettings(navOptions)
    }
}

enum class AdminTopLevelDestination(
    override val selectedIcon: ImageVector,
    override val unselectedIcon: ImageVector,
    override val titleTextId: Int,
    override val route: KClass<*>,
) : TopLevelDestination {
    POS(PosIcons.Pos, PosIcons.Pos, R.string.pos, SaleRoute::class),
    REPORTS(PosIcons.Reports, PosIcons.Reports, R.string.reports_title, ReportsRoute::class),
    ITEMS(PosIcons.Items, PosIcons.Items, R.string.menu_items, ItemsGraph::class),
    EMPLOYEES(PosIcons.Employee, PosIcons.Employee, core_ui_employees_title, EmployeesRoute::class),
    SETTINGS(
        PosIcons.Settings,
        PosIcons.Settings,
        R.string.settings_title,
        SettingRoute::class,
    ),
}

enum class SaleTopLevelDestination(
    override val selectedIcon: ImageVector,
    override val unselectedIcon: ImageVector,
    @StringRes override val titleTextId: Int,
    override val route: KClass<*>,
) : TopLevelDestination {
    POS(PosIcons.Pos, PosIcons.Pos, R.string.pos, SaleRoute::class),
    REPORTS(PosIcons.Reports, PosIcons.Reports, R.string.reports_title, ReportsRoute::class),
    ITEMS(PosIcons.Items, PosIcons.Items, R.string.menu_items, ItemsSaleGraph::class),
    SETTINGS(
        PosIcons.Settings,
        PosIcons.Settings,
        R.string.settings_title,
        SettingGraph::class,
    ),
}