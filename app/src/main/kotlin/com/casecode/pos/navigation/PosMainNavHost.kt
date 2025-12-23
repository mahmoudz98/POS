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
import com.casecode.pos.InitialDestinationState
import com.casecode.pos.feature.login.employee.navigation.loginInEmployeeDialog
import com.casecode.pos.feature.login.employee.navigation.navigateToLoginEmployee
import com.casecode.pos.feature.login.navigation.LoginRoute
import com.casecode.pos.feature.login.navigation.loginScreen
import com.casecode.pos.feature.onboarding.navigation.OnboardingRoute
import com.casecode.pos.feature.onboarding.navigation.onboardingScreen
import com.casecode.pos.ui.MainAppState
import timber.log.Timber

@Composable
fun PosMainNavHost(
    appState: MainAppState,
    startGraphDestination: Any,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = startGraphDestination,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
    ) {
        loginScreen(
            onLoginEmployeeClick = {
                navController.navigateToLoginEmployee()
            },
            onShowSnackbar = onShowSnackbar,
            enterTransition = {
                when (targetState.destination.route) {
                    OnboardingRoute.toString() -> slideInHorizontally(
                        initialOffsetX = { -it },
                        animationSpec = defaultTween(),
                    )

                    else -> fadeIn(animationSpec = slowTween())
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    OnboardingRoute.toString() -> slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = defaultTween(),
                    )

                    else -> contextShiftExit()
                }
            },
        )
        loginInEmployeeDialog {
            appState.navController.popBackStack()
        }
        onboardingScreen(
            onShowSnackbar = { onShowSnackbar(it, null) },
            enterTransition = { flowTransition() },
            exitTransition = {
                when (targetState.destination.route) {
                    LoginRoute.toString() -> slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = defaultTween(),
                    )

                    else -> fadeOut(animationSpec = slowTween())
                }
            },
        )

        homeAdminGraph(
            appState = appState,
            onShowSnackbar = onShowSnackbar,
            enterTransition = { contextShiftEnter() },
            exitTransition = {
                if (targetState.destination.route == LoginRoute.toString()) {
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
                if (targetState.destination.route == LoginRoute.toString()) {
                    null
                } else {
                    contextShiftExit()
                }
            },
        )
    }
}

fun InitialDestinationState.determineStartGraph(): Any {
    Timber.d("determineStartGraph: $this")

    return when (this) {
        InitialDestinationState.Loading -> LoginRoute
        InitialDestinationState.SignOut,
        InitialDestinationState.LoginByNoneEmployee,
        -> LoginRoute

        is InitialDestinationState.NotCompleteBusiness -> OnboardingRoute
        is InitialDestinationState.LoginByAdmin,
        is InitialDestinationState.LoginByAdminEmployee,
        -> AdminHomeGraphRoute

        is InitialDestinationState.LoginBySaleEmployee -> SaleHomeGraphRoute
    }
}
