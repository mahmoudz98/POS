package com.casecode.pos.feature.sale

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val POS_ROUTE = "pos_route"

fun NavGraphBuilder.posScreen(onGoToItems: () -> Unit) {
    composable(
        route = POS_ROUTE,

    ) {
        PosScreen(onGoToItems = onGoToItems)
    }
}

fun NavController.navigateToPos(navOptions: NavOptions) = navigate(POS_ROUTE, navOptions)