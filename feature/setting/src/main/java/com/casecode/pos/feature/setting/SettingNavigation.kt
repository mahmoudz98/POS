package com.casecode.pos.feature.setting

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.casecode.pos.feature.setting.printer.PrinterInfoRoute
import com.casecode.pos.feature.setting.printer.PrinterRoute

const val SETTING_GRAPH = "setting_graph"
const val SETTING_ROUTE = "settings_route"
const val PRINTER_ROUTE = "printer_route"
const val PRINTER_INFO_ROUTE = "printer_info_route"
fun NavGraphBuilder.settingsGraph(navController: NavController, onSignOutClick: () -> Unit) {
    navigation(startDestination = SETTING_ROUTE, route = SETTING_GRAPH) {
        settingScreen(
            onSignOutClick = onSignOutClick,
            onPrinterClick = { navController.navigateToPrinter() },
        )
        printerScreen(
            onBackClick = navController::popBackStack,
            onPrinterInfoClick = navController::navigateToPrinterInfo,
        )
        printerInfoScreen(onBackClick = navController::popBackStack)

    }
}

fun NavGraphBuilder.settingScreen(onSignOutClick: () -> Unit, onPrinterClick: () -> Unit) {
    composable(route = SETTING_ROUTE) {
        SettingRoute(onSignOutClick = onSignOutClick, onPrinterClick = onPrinterClick)
    }
}

private fun NavGraphBuilder.printerScreen(onBackClick: () -> Unit, onPrinterInfoClick: () -> Unit) {
    composable(route = PRINTER_ROUTE) {
        PrinterRoute(onBackClick = onBackClick, onPrinterInfoClick = onPrinterInfoClick)
    }
}

private fun NavGraphBuilder.printerInfoScreen(onBackClick: () -> Unit) {
    composable(route = PRINTER_INFO_ROUTE) {
        PrinterInfoRoute(onBackClick = onBackClick)
    }
}

fun NavController.navigateToSettings(navOptions: NavOptions) =
    navigate(SETTING_GRAPH, navOptions)

fun NavController.navigateToPrinter() =
    navigate(PRINTER_ROUTE)

fun NavController.navigateToPrinterInfo() =
    navigate(PRINTER_INFO_ROUTE)