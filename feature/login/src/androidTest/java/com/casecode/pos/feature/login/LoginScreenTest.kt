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
package com.casecode.pos.feature.login

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test

class LoginScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    private val signInButtonMatcher by lazy {
        hasText(
            composeTestRule.activity.resources.getString(R.string.feature_login_button_owner_google),
        )
    }

    @Test
    fun circularProgressIndicator_whenScreenIsNotLoading_doesNotExist() {
        composeTestRule.setContent {
            LoginScreen(
                uiState = LoginUiState.Idle,
                onLoginEmployeeClick = {},
                onOwnerLoginClick = {},
            )
        }
        composeTestRule.onNodeWithContentDescription("loginLoading").assertDoesNotExist()
    }

    @Test
    fun circularProgressIndicator_whenScreenIsLoading_exists() {
        composeTestRule.setContent {
            LoginScreen(
                uiState = LoginUiState.Loading,
                onLoginEmployeeClick = {},
                onOwnerLoginClick = {},
            )
        }
        composeTestRule.onNodeWithContentDescription("loginLoading").assertExists()
    }

    @Test
    fun signInButton_whenClicked_isDisplayed() = runTest {
        composeTestRule.setContent {
            LoginScreen(
                uiState = LoginUiState.Idle,
                onLoginEmployeeClick = {},
                onOwnerLoginClick = {},
            )
        }
        composeTestRule.onNode(signInButtonMatcher).assertIsDisplayed().performClick()
    }
}