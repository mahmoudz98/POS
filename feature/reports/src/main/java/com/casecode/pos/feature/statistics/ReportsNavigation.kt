package com.casecode.pos.feature.statistics

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val STATISTICS_ROUTE = "statistics_route"

fun NavGraphBuilder.statisticsScreen() {
    composable(
        route = STATISTICS_ROUTE,

    ) {
        ReportsScreen()
    }
}

fun NavController.navigateToStatistics(navOptions: NavOptions) =
    navigate(STATISTICS_ROUTE, navOptions)