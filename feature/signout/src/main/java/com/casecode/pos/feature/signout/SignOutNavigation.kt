package com.casecode.pos.feature.signout

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog

const val SIGN_OUT_ROUTE = "sign_out_route"

fun NavGraphBuilder.signOutDialog(
    onSignOut: () -> Unit,
    onDismiss: () -> Unit,
) {
    dialog(route = SIGN_OUT_ROUTE) {
        SignOutDialog(onSignOut = onSignOut, onDismiss = onDismiss)
    }
}

fun NavController.navigateToSignOut() = navigate(SIGN_OUT_ROUTE)