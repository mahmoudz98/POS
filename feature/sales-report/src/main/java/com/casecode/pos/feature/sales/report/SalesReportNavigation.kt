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
package com.casecode.pos.feature.sales.report

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
import kotlinx.serialization.Serializable

@Serializable
data object SalesReportGraph

@Serializable
data object SalesReportRoute

@Serializable
data object SalesReportDetailsRoute

fun NavGraphBuilder.salesReportGraph(
    navController: NavController,
    onBackClick: () -> Unit,
    onSalesReportDetailsClick: () -> Unit,
) {
    navigation<SalesReportGraph>(
        startDestination = SalesReportRoute,
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
    composable<SalesReportRoute> {
        val viewModel = it.sharedViewModel<SalesReportViewModel>(navController)
        SalesReportScreen(
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
    composable<SalesReportDetailsRoute>(
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
        val viewModel = it.sharedViewModel<SalesReportViewModel>(navController)
        SalesReportDetailsRoute(
            viewModel,
            onBackClick = onBackClick,
        )
    }
}

@Composable
inline fun <reified VM : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavController,
): VM {
    val parentEntry =
        remember(this) {
            navController.getBackStackEntry(SalesReportGraph)
        }
    return hiltViewModel(parentEntry)
}

fun NavController.navigateToSalesReport(navOptions: NavOptions? = null) =
    navigate(SalesReportGraph, navOptions)

fun NavController.navigateToSalesReportDetails() = navigate(SalesReportDetailsRoute)