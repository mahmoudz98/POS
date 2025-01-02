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
package com.casecode.pos.feature.login.employee

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.casecode.pos.core.designsystem.theme.POSTheme
import org.junit.Rule
import org.junit.Test

class LoginInEmployeeDialogKtTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun circleProgressIndicator_whenInProgressLoginEmployeeIsTrue_assertExists() {
        // Verify that the dialog launches successfully when triggered and that all UI components are displayed correctly (title, text fields, buttons, etc.).
        // TODO implement test
        composeTestRule.setContent {
            LoginInEmployeeDialog(
                uiState = LoginEmployeeUiState(inProgressLoginEmployee = true),
                showMessage = {},
                onShowMessage = {},
                onLoginEmployeeClick = { _, _, _ -> },
                onDismiss = {},
            )
        }
        composeTestRule.onNodeWithContentDescription("LoadingLoginEmployee").assertExists()
    }

    @Test
    fun loginButtonClick_whenInputFieldsAreEmpty_assertErrorMessages() {
        composeTestRule.setContent {
            POSTheme {
                LoginInEmployeeDialog(
                    uiState = LoginEmployeeUiState(),
                    showMessage = {},
                    onShowMessage = {},
                    onLoginEmployeeClick = { _, _, _ -> },
                    onDismiss = {},
                )
            }
        }
        // Attempt to click login with empty input fields and check validation error messages
        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(
                    R.string.feature_login_employee_login_action_login,
                ),
            )
            .performClick()
        // Check for error messages on empty input fields
        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(
                    R.string.feature_login_employee_login_error_uid_empty,
                ),
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(
                    R.string.feature_login_employee_error_name_empty,
                ),
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(
                    R.string.feature_login_employee_error_password_empty,
                ),
            )
            .assertIsDisplayed()
    }
}