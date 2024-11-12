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
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.casecode.pos.feature.employee.EmployeesRoute
import com.casecode.pos.feature.employee.navigateToEmployees
import com.casecode.pos.feature.inventory.navigation.InventoryRoute
import com.casecode.pos.feature.inventory.navigation.navigateToInventory
import com.casecode.pos.feature.item.navigation.ItemsSaleGraph
import com.casecode.pos.feature.item.navigation.navigateToItemsSaleGraph
import com.casecode.pos.feature.sale.navigation.SaleRoute
import com.casecode.pos.feature.sale.navigation.navigateToSale
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
    private fun TopLevelDestination.navigateTo(
        navController: NavHostController,
        navOptions: NavOptions,
    ) {
        when (this.route) {
            SaleRoute::class -> navController.navigateToSale(navOptions)
            ReportsRoute::class -> navController.navigateToReports(navOptions)
            InventoryRoute::class -> navController.navigateToInventory(navOptions)
            // TODO: use move to sale nav host
            ItemsSaleGraph::class -> navController.navigateToItemsSaleGraph(navOptions)
            EmployeesRoute::class -> navController.navigateToEmployees(navOptions)
            SettingRoute::class -> navController.navigateToSettings(navOptions)
        }
    }
}