package com.casecode.pos.feature.statistics

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val REPORTS_ROUTE = "reports_route"

fun NavGraphBuilder.reportsScreen(
    onSalesReportClick: () -> Unit,
    onInventoryReportClick: () -> Unit,
) {
    composable(
        route = REPORTS_ROUTE,
    ) {
        ReportsScreen(
            onSalesReportClick = onSalesReportClick,
            onInventoryReportClick = onInventoryReportClick,
        )
    }
}

fun NavController.navigateToReports(navOptions: NavOptions) = navigate(REPORTS_ROUTE, navOptions)