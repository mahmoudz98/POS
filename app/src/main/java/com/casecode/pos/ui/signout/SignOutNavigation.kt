package com.casecode.pos.ui.signout

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val SIGN_OUT_ROUTE = "sign_out_route"

fun NavGraphBuilder.signOutScreen(onTopicClick: (String) -> Unit) {
    composable(route = SIGN_OUT_ROUTE) {
        SignOutScreen("signOut")
    }
}

fun NavController.navigateToSignOut(navOptions: NavOptions) = navigate(SIGN_OUT_ROUTE, navOptions)