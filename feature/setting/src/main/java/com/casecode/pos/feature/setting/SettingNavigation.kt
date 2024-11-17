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

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.casecode.pos.feature.setting.printer.PrinterInfoScreen
import com.casecode.pos.feature.setting.printer.PrinterScreen
import kotlinx.serialization.Serializable

@Serializable
data object SettingGraph

@Serializable
data object SettingRoute

@Serializable
data object PrinterRoute

@Serializable
data object PrinterInfoRoute

fun NavGraphBuilder.settingsGraph(
    navController: NavController,
    onEmployeesScreenClick: () -> Unit,
    onSignOutClick: () -> Unit,
) {
    navigation<SettingGraph>(startDestination = SettingRoute) {
        settingScreen(
            onEmployeesScreenClick = onEmployeesScreenClick,
            onPrinterScreenClick = { navController.navigateToPrinter() },
            onSignOutClick = onSignOutClick,
        )
        printerScreen(
            onBackClick = navController::popBackStack,
            onPrinterInfoClick = navController::navigateToPrinterInfo,
        )
        printerInfoScreen(onBackClick = navController::popBackStack)
    }
}

fun NavGraphBuilder.settingScreen(
    onEmployeesScreenClick: () -> Unit,
    onPrinterScreenClick: () -> Unit,
    onSignOutClick: () -> Unit,
) {
    composable<SettingRoute> {
        SettingScreen(
            onEmployeesScreenClick = onEmployeesScreenClick,
            onPrinterScreenClick = onPrinterScreenClick,
            onSignOutClick = onSignOutClick,
        )
    }
}

private fun NavGraphBuilder.printerScreen(
    onBackClick: () -> Unit,
    onPrinterInfoClick: () -> Unit,
) {
    composable<PrinterRoute> {
        PrinterScreen(onBackClick = onBackClick, onPrinterInfoClick = onPrinterInfoClick)
    }
}

private fun NavGraphBuilder.printerInfoScreen(onBackClick: () -> Unit) {
    composable<PrinterInfoRoute> {
        PrinterInfoScreen(onNavigateBack = onBackClick)
    }
}

fun NavController.navigateToSettings(navOptions: NavOptions) = navigate(SettingGraph, navOptions)

fun NavController.navigateToPrinter() = navigate(PrinterRoute)

fun NavController.navigateToPrinterInfo() = navigate(PrinterInfoRoute)