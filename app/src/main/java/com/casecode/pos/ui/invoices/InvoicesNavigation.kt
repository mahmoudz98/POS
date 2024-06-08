package com.casecode.pos.ui.invoices

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val INVOICES_ROUTE = "invoices_route"

fun NavGraphBuilder.invoicesScreen(onTopicClick: (String) -> Unit) {
    composable(route = INVOICES_ROUTE) {
        InvoicesScreen("POS")
    }
}

fun NavController.navigateToInvoices(navOptions: NavOptions) = navigate(INVOICES_ROUTE, navOptions)