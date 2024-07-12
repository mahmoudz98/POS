package com.casecode.pos.feature.invoice

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val INVOICES_ROUTE = "invoices_route"
const val INVOICE_DETAILS_ROUTE = "invoice_details_route"

fun NavGraphBuilder.invoicesGraph(navController: NavController, onMenuClick:()->Unit) {
    invoicesScreen(onMenuClick, navController)
    invoiceDetailsScreen(navController)

}

private fun NavGraphBuilder.invoicesScreen(
    onMenuClick: () -> Unit,
    navController: NavController,
) {
    composable(route = INVOICES_ROUTE) {
        InvoicesRoute(
            onMenuClick = onMenuClick,
            onInvoiceDetailsClick = {
                navController.navigateToInvoiceDetails()
            },
        )
    }
}

private fun NavGraphBuilder.invoiceDetailsScreen(navController: NavController) {
    composable(route = INVOICE_DETAILS_ROUTE,
        enterTransition = {
            fadeIn(
                animationSpec = tween(
                    300, easing = LinearEasing
                )
            ) + slideIntoContainer(
                animationSpec = tween(300, easing = EaseIn),
                towards = AnimatedContentTransitionScope.SlideDirection.Start
            )
        },
        exitTransition = {
            fadeOut(
                animationSpec = tween(
                    300, easing = LinearEasing
                )
            ) + slideOutOfContainer(
                animationSpec = tween(300, easing = EaseOut),
                towards = AnimatedContentTransitionScope.SlideDirection.End
            )
        }
        ) {
        val parentEntry = remember(it) {
            navController.getBackStackEntry(INVOICES_ROUTE)
        }
        val viewModel: InvoicesViewModel = hiltViewModel(parentEntry)

        InvoiceDetailsRoute(
            viewModel,
            onBackClick = {
                navController.popBackStack()
            },
        )
    }
}

fun NavController.navigateToInvoices(navOptions: NavOptions) = navigate(INVOICES_ROUTE, navOptions)
fun NavController.navigateToInvoiceDetails() = navigate(INVOICE_DETAILS_ROUTE)