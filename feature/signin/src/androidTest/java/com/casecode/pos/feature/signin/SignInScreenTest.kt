package com.casecode.pos.feature.signin

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test

class SignInScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    private val signInButtonMatcher by lazy {
        hasText(
            composeTestRule.activity.resources.getString(R.string.feature_signin_google_title),
        )
    }

    @Test
    fun circularProgressIndicator_whenScreenIsNotLoading_doesNotExist() {
        composeTestRule.setContent {
            SignInScreen(
                uiState = SignInActivityUiState(isLoading = false),
                onSignInCLick = {},
                onLoginEmployeeClick = {},
                onMessageShown = {},
            )
        }
        composeTestRule.onNodeWithContentDescription("SignInLoading").assertDoesNotExist()
    }

    @Test
    fun circularProgressIndicator_whenScreenIsLoading_exists() {
        composeTestRule.setContent {
            SignInScreen(
                uiState = SignInActivityUiState(isLoading = true),
                onSignInCLick = {},
                onLoginEmployeeClick = {},
                onMessageShown = {},
            )
        }
        composeTestRule.onNodeWithContentDescription("SignInLoading").assertExists()
    }


    @Test
    fun signInButton_whenClicked_isDisplayed() = runTest {
        composeTestRule.setContent {
            SignInScreen(
                uiState = SignInActivityUiState(isLoading = false),
                onSignInCLick = { },
                onLoginEmployeeClick = {},
                onMessageShown = {},
            )
        }
        composeTestRule.onNode(signInButtonMatcher).assertIsDisplayed().performClick()

    }

    @Test
    fun whenHasMessage_snackbarIsDisplayed() {
        composeTestRule.setContent {
            SignInScreen(
                uiState = SignInActivityUiState(userMessage = com.casecode.pos.core.ui.R.string.core_ui_error_network),
                onSignInCLick = {},
                onLoginEmployeeClick = {},
                onMessageShown = {},
            )
        }
        composeTestRule.onNodeWithText(
            composeTestRule.activity.resources.getString(com.casecode.pos.core.ui.R.string.core_ui_error_network),
        ).assertIsDisplayed()
    }

}
