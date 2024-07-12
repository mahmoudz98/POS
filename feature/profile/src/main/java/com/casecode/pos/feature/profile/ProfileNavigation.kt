package com.casecode.pos.feature.profile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val PROFILE_ROUTE = "profile_route"

fun NavGraphBuilder.profileScreen(onBackClick: () -> Unit) {
    composable(route = PROFILE_ROUTE) {
        ProfileRoute(onBackClick = onBackClick)
    }
}

fun NavController.navigateToProfile(navOptions: NavOptions) = navigate(PROFILE_ROUTE, navOptions)