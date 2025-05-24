package com.casecode.pos.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.casecode.pos.InitialDestinationState
import com.casecode.pos.feature.signin.navigation.SignInRoute
import com.casecode.pos.feature.signin.navigation.navigateToSignIn
import com.casecode.pos.feature.signin.navigation.signInScreen
import com.casecode.pos.feature.stepper.navigation.StepperRoute
import com.casecode.pos.feature.stepper.navigation.navigateToStepper
import com.casecode.pos.feature.stepper.navigation.stepperScreen
import com.casecode.pos.ui.MainAppState

@Composable
fun PosRootNavHost(appState: MainAppState, modifier: Modifier = Modifier) {
    val startDestination = when (appState.initialDestinationState) {
        InitialDestinationState.ErrorLogin -> SignInRoute

        InitialDestinationState.LoginBySaleEmployee, InitialDestinationState.LoginByNoneEmployee,
        InitialDestinationState.LoginByAdminEmployee, InitialDestinationState.LoginByAdmin,
            -> {
            MainAppRoute
        }
        InitialDestinationState.NotCompleteBusiness -> StepperRoute
        else -> Unit
    }
    NavHost(
        navController = appState.rootNavController,
        startDestination =startDestination,
        modifier = modifier,
    ){
        signInScreen(
            onSignInSuccessNavigateToMain = {
                appState.rootNavController.navigateToMain(defaultSingleTopNavOptions())
            },
            onSignInSuccessNavigateToStepper = {
                appState.rootNavController.navigateToStepper(defaultSingleTopNavOptions())
            },
        )
        stepperScreen(
            onStepperCompleteToHome = {
                appState.rootNavController.navigateToMain(defaultSingleTopNavOptions())
            },
            onBackToSignIn = {
                appState.rootNavController.navigateToSignIn(defaultSingleTopNavOptions())
            },
        )

        mainScreen(appState)

    }
}
