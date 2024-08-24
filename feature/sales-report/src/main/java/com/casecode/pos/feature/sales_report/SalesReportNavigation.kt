package com.casecode.pos.feature.sales_report

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation

const val SALES_REPORT_GRAPH = "sales_report_graph"
const val SALES_REPORT_ROUTE = "sales_report_route"
const val SALES_REPORT_DETAILS_ROUTE = "sales_report_details_route"

fun NavGraphBuilder.salesReportGraph(
    navController: NavController,
    onBackClick: () -> Unit,
    onSalesReportDetailsClick: () -> Unit,
) {
    navigation(
        startDestination = SALES_REPORT_ROUTE,
        route = SALES_REPORT_GRAPH,
    ) {
        salesReportScreen(navController = navController, onBackClick = onBackClick) {
            onSalesReportDetailsClick()
        }
        salesReportDetailsScreen(navController, onBackClick)
    }
}

private fun NavGraphBuilder.salesReportScreen(
    navController: NavController,
    onBackClick: () -> Unit,
    onSalesReportDetailsClick: () -> Unit,
) {
    composable(route = SALES_REPORT_ROUTE) {
        val viewModel = it.sharedViewModel<SalesReportViewModel>(navController, SALES_REPORT_GRAPH)
        SalesReportRoute(
            viewModel = viewModel,
            onBackClick = onBackClick,
            onSalesReportDetailsClick = onSalesReportDetailsClick,
        )
    }
}

private fun NavGraphBuilder.salesReportDetailsScreen(
    navController: NavController,
    onBackClick: () -> Unit,
) {
    composable(
        route = SALES_REPORT_DETAILS_ROUTE,
        enterTransition = {
            fadeIn(
                animationSpec =
                    tween(
                        300,
                        easing = LinearEasing,
                    ),
            ) +
                slideIntoContainer(
                    animationSpec = tween(300, easing = EaseIn),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                )
        },
        exitTransition = {
            fadeOut(
                animationSpec =
                    tween(
                        300,
                        easing = LinearEasing,
                    ),
            ) +
                slideOutOfContainer(
                    animationSpec = tween(300, easing = EaseOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                )
        },
    ) {
        val viewModel = it.sharedViewModel<SalesReportViewModel>(navController, SALES_REPORT_GRAPH)
        SalesReportDetailsRoute(
            viewModel,
            onBackClick = onBackClick,
        )
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavController,
    route: String,
): T {
    val parentEntry =
        remember(this) {
            navController.getBackStackEntry(route)
        }
    return hiltViewModel(parentEntry)
}

fun NavController.navigateToSalesReport(navOptions: NavOptions? = null) = navigate(SALES_REPORT_GRAPH, navOptions)

fun NavController.navigateToSalesReportDetails() = navigate(SALES_REPORT_DETAILS_ROUTE)