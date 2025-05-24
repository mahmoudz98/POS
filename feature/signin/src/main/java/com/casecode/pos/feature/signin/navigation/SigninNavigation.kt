package com.casecode.pos.feature.signin.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.casecode.pos.feature.signin.SignInScreen
import kotlinx.serialization.Serializable


@Serializable
data object SignInRoute

fun NavGraphBuilder.signInScreen(
    onSignInSuccessNavigateToMain: () -> Unit,
    onSignInSuccessNavigateToStepper: () -> Unit,
) {

        composable<SignInRoute> {
            SignInScreen(
                onSignInSuccessNavigateToMain = onSignInSuccessNavigateToMain,
                onSignInSuccessNavigateToStepper = onSignInSuccessNavigateToStepper,
            )
        }
    }


fun NavController.navigateToSignIn(navOptions: NavOptions) = navigate(SignInRoute, navOptions)