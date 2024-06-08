package com.casecode.pos.ui.settings

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val SETTINGS_ROUTE = "settings_route"

fun NavGraphBuilder.settingScreen() {
    composable(route = SETTINGS_ROUTE) {
        SettingRoute()
    }
}

fun NavController.navigateToSettings(navOptions: NavOptions) =
    navigate(SETTINGS_ROUTE, navOptions)