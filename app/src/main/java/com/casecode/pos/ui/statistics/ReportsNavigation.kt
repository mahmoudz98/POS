package com.casecode.pos.ui.statistics

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val REPORTS_ROUTE = "reports_route"

fun NavGraphBuilder.reportsScreen() {
    composable(
        route = REPORTS_ROUTE,

    ) {
        ReportsScreen("POS")
    }
}

fun NavController.navigateToReports(navOptions: NavOptions) =
    navigate(REPORTS_ROUTE, navOptions)