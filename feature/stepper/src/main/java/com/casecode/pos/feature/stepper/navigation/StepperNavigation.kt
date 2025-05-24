package com.casecode.pos.feature.stepper.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.casecode.pos.feature.stepper.StepperScreen
import kotlinx.serialization.Serializable


@Serializable
data object StepperRoute

fun NavGraphBuilder.stepperScreen(
    onStepperCompleteToHome: () -> Unit,
    onBackToSignIn: () -> Unit,
) {

    composable<StepperRoute> {

        StepperScreen(
            onStepperCompleteToHome = onStepperCompleteToHome,
            onBackToSignIn = onBackToSignIn,
        )
    }
}


fun NavController.navigateToStepper(navOptions: NavOptions) = navigate(StepperRoute, navOptions)