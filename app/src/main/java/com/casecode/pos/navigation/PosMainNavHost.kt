/*
 * Designed and developed 2024 by Mahmood Abdalhafeez
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.casecode.pos.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.casecode.pos.InitialDestinationState
import com.casecode.pos.feature.signin.navigation.SignInRoute
import com.casecode.pos.feature.signin.navigation.navigateToSignIn
import com.casecode.pos.feature.signin.navigation.signInScreen
import com.casecode.pos.feature.stepper.navigation.StepperRoute
import com.casecode.pos.feature.stepper.navigation.navigateToStepper
import com.casecode.pos.feature.stepper.navigation.stepperScreen
import com.casecode.pos.ui.MainAppState

@Composable
fun PosMainNavHost(appState: MainAppState, startGraphDestination: Any) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = startGraphDestination,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
    ) {
        signInScreen(
            onSignInSuccessNavigateToMain = {
                navController.navigateToMainGraph(
                    navOptions {
                        popUpTo(SignInRoute) { inclusive = true }
                        launchSingleTop = true
                    },
                )
            },
            onSignInSuccessNavigateToStepper = {
                navController.navigateToStepper(
                    navOptions {
                        popUpTo(SignInRoute) { inclusive = true }
                        launchSingleTop = true
                    },
                )
            },
            enterTransition = {
                when (targetState.destination.route) {
                    StepperRoute.toString() -> slideInHorizontally(
                        initialOffsetX = { -it },
                        animationSpec = defaultTween(),
                    )

                    else -> fadeIn(animationSpec = slowTween())
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    StepperRoute.toString() -> slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = defaultTween(),
                    )

                    else -> contextShiftExit()
                }
            },
        )

        stepperScreen(
            onStepperCompleteToHome = {
                navController.navigateToMainGraph(
                    navOptions {
                        popUpTo(StepperRoute) { inclusive = true }
                        launchSingleTop = true
                    },
                )
            },
            onBackToSignIn = {
                navController.navigateToSignIn(
                    navOptions {
                        popUpTo(StepperRoute) { inclusive = true }
                        launchSingleTop = true
                    },
                )
            },
            enterTransition = { flowTransition() },
            exitTransition = {
                when (targetState.destination.route) {
                    SignInRoute.toString() -> slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = defaultTween(),
                    )

                    else -> fadeOut(animationSpec = slowTween())
                }
            },
        )

        homeAdminGraph(
            appState = appState,
            enterTransition = { contextShiftEnter() },
            exitTransition = {
                if (targetState.destination.route == SignInRoute.toString()) {
                    null
                } else {
                    contextShiftExit()
                }
            },
        )
        homeSaleGraph(
            appState = appState,
            enterTransition = { contextShiftEnter() },
            exitTransition = {
                if (targetState.destination.route == SignInRoute.toString()) {
                    null
                } else {
                    contextShiftExit()
                }
            },
        )
    }
}

fun InitialDestinationState.determineStartGraph(): Any {
    return when (this) {
        InitialDestinationState.Loading -> SignInRoute
        InitialDestinationState.ErrorLogin,
        InitialDestinationState.LoginByNoneEmployee,
        -> SignInRoute

        is InitialDestinationState.NotCompleteBusiness -> StepperRoute
        is InitialDestinationState.LoginByAdmin,
        is InitialDestinationState.LoginByAdminEmployee,
        -> AdminHomeGraphRoute

        is InitialDestinationState.LoginBySaleEmployee -> SaleHomeGraphRoute
    }
}