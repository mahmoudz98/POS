package com.casecode.pos.ui.pos

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val POS_ROUTE = "pos_route"

fun NavGraphBuilder.posScreen() {
    composable(
        route = POS_ROUTE,

    ) {
        PosScreen()
    }
}

fun NavController.navigateToPos(navOptions: NavOptions) = navigate(POS_ROUTE, navOptions)