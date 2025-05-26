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
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import com.casecode.pos.InitialDestinationState
import com.casecode.pos.core.designsystem.component.PosLoadingWheel
import com.casecode.pos.feature.signin.navigation.SignInRoute
import com.casecode.pos.feature.signin.navigation.navigateToSignIn
import com.casecode.pos.feature.signin.navigation.signInScreen
import com.casecode.pos.feature.stepper.navigation.StepperRoute
import com.casecode.pos.feature.stepper.navigation.navigateToStepper
import com.casecode.pos.feature.stepper.navigation.stepperScreen
import com.casecode.pos.ui.MainAppState

@Composable
fun PosRootScreen(appState: MainAppState, modifier: Modifier = Modifier) {
    if (appState.initialDestinationState is InitialDestinationState.Loading) {
        Box(modifier = modifier.fillMaxSize()) {
            PosLoadingWheel(contentDesc = "root", modifier = Modifier.align(Alignment.Center))
        }
    } else {
        PosRootNavHost(appState = appState, modifier = modifier)
    }
}

@Composable
fun PosRootNavHost(appState: MainAppState, modifier: Modifier = Modifier) {
    val startDestination = when (appState.initialDestinationState) {
        InitialDestinationState.ErrorLogin -> SignInRoute
        InitialDestinationState.LoginBySaleEmployee,
        InitialDestinationState.LoginByAdminEmployee, InitialDestinationState.LoginByAdmin,
        -> MainAppRoute

        InitialDestinationState.NotCompleteBusiness -> StepperRoute
        else -> Unit
    }
    NavHost(
        navController = appState.rootNavController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { rootEnterTransition(initialState, targetState) },
        exitTransition = { rootExitTransition(initialState, targetState) },
        popEnterTransition = { rootPopEnterTransition(initialState, targetState) },
        popExitTransition = { rootPopExitTransition(initialState, targetState) },
    ) {
        signInScreen(
            // This is an extension function on NavGraphBuilder
            onSignInSuccessNavigateToMain = {
                appState.rootNavController.navigateToMain(
                    defaultSingleTopNavOptions(),
                )
            },
            onSignInSuccessNavigateToStepper = {
                appState.rootNavController.navigateToStepper(
                    defaultSingleTopNavOptions(),
                )
            },
        )
        stepperScreen(
            onStepperCompleteToHome = {
                appState.rootNavController.navigateToMain(
                    defaultSingleTopNavOptions(),
                )
            },
            onBackToSignIn = { // Going back from Stepper to SignIn
                appState.rootNavController.navigateToSignIn(
                    defaultSingleTopNavOptions(),

                )
            },
        )

        mainScreen(appState)
    }
}

private fun rootEnterTransition(
    initial: NavBackStackEntry,
    target: NavBackStackEntry,
): EnterTransition {
    val initialRoute = initial.destination.route
    val targetRoute = target.destination.route

    return when {
        // Sign In -> Stepper (Forward)
        initialRoute == SignInRoute.toString() && targetRoute == StepperRoute.toString() ->
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(SHARED_AXIS_DURATION_MS),
            ) + fadeIn(tween(SHARED_AXIS_DURATION_MS))

        // Stepper -> Main App OR Sign In -> Main App (Elevating to main context)
        (initialRoute == StepperRoute.toString() || initialRoute == SignInRoute.toString()) && targetRoute == MainAppRoute.toString() ->
            fadeIn(animationSpec = tween(ROOT_TRANSITION_DURATION_MS)) + scaleIn(
                initialScale = 0.9f,
                animationSpec = tween(ROOT_TRANSITION_DURATION_MS),
            )

        else -> fadeIn(animationSpec = tween(ROOT_TRANSITION_DURATION_MS)) // Default for other cases
    }
}

private fun rootExitTransition(
    initial: NavBackStackEntry,
    target: NavBackStackEntry,
): ExitTransition {
    val initialRoute = initial.destination.route
    val targetRoute = target.destination.route

    return when {
        // Sign In -> Stepper (Forward)
        initialRoute == SignInRoute.toString() && targetRoute == StepperRoute.toString() ->
            slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(SHARED_AXIS_DURATION_MS),
            ) + fadeOut(tween(SHARED_AXIS_DURATION_MS))

        // Stepper -> Main App OR Sign In -> Main App (Elevating to main context)
        (initialRoute == StepperRoute.toString() || initialRoute == SignInRoute.toString()) && targetRoute == MainAppRoute.toString() ->
            fadeOut(animationSpec = tween(ROOT_TRANSITION_DURATION_MS))

        else -> fadeOut(animationSpec = tween(ROOT_TRANSITION_DURATION_MS)) // Default for other cases
    }
}

private fun rootPopEnterTransition(
    initial: NavBackStackEntry,
    target: NavBackStackEntry,
): EnterTransition {
    val initialRoute = initial.destination.route // The screen being popped from
    val targetRoute =
        target.destination.route // The screen being navigated to (the one below on stack)

    return when {
        // Stepper <- Main (if main could pop to stepper, unlikely in root setup)
        // Sign In <- Stepper (Back)
        initialRoute == StepperRoute.toString() && targetRoute == SignInRoute.toString() ->
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(SHARED_AXIS_DURATION_MS),
            ) + fadeIn(tween(SHARED_AXIS_DURATION_MS))

        else -> fadeIn(animationSpec = tween(ROOT_TRANSITION_DURATION_MS))
    }
}

private fun rootPopExitTransition(
    initial: NavBackStackEntry,
    target: NavBackStackEntry,
): ExitTransition {
    val initialRoute = initial.destination.route
    val targetRoute = target.destination.route

    return when {
        // Sign In <- Stepper (Back)
        initialRoute == StepperRoute.toString() && targetRoute == SignInRoute.toString() ->
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(SHARED_AXIS_DURATION_MS),
            ) + fadeOut(tween(SHARED_AXIS_DURATION_MS))

        else -> fadeOut(animationSpec = tween(ROOT_TRANSITION_DURATION_MS))
    }
}

// --- Animation Constants ---
private const val ROOT_TRANSITION_DURATION_MS = 400 // Slightly longer for context shift
private const val SHARED_AXIS_DURATION_MS = 300