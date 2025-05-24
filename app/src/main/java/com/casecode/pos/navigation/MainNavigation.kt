package com.casecode.pos.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.casecode.pos.ui.MainAppState
import com.casecode.pos.ui.MainScreen
import kotlinx.serialization.Serializable

@Serializable
data object MainAppRoute

fun NavGraphBuilder.mainScreen(appState: MainAppState){
    composable<MainAppRoute>{
        MainScreen(appState  = appState)
    }
}
fun NavController.navigateToMain(navOptions: NavOptions) = navigate(MainAppRoute, navOptions)
