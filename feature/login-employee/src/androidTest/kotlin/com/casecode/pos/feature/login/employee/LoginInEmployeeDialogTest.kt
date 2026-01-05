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
import androidx.compose.ui.test.performTextInput
import com.casecode.pos.core.designsystem.theme.POSTheme
import org.junit.Rule
import org.junit.Test

class LoginInEmployeeDialogTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun loadingIndicator_whenInProgressLoginEmployee_isDisplayed() {
        composeTestRule.setContent {
            POSTheme {
                LoginInEmployeeDialog(
                    uiState = LoginEmployeeUiState(inProgressLoginEmployee = true),
                    formState = LoginEmployeeFormState(),
                    onFormEvent = {},
                    showMessage = {},
                    onShowMessage = {},
                    onDismiss = {},
                )
            }
        }
        composeTestRule.onNodeWithContentDescription("LoadingLoginEmployee").assertExists()
    }

    @Test
    fun loadingIndicator_whenNotInProgress_doesNotExist() {
        composeTestRule.setContent {
            POSTheme {
                LoginInEmployeeDialog(
                    uiState = LoginEmployeeUiState(inProgressLoginEmployee = false),
                    formState = LoginEmployeeFormState(),
                    onFormEvent = {},
                    showMessage = {},
                    onShowMessage = {},
                    onDismiss = {},
                )
            }
        }
        composeTestRule.onNodeWithContentDescription("LoadingLoginEmployee").assertDoesNotExist()
    }

    @Test
    fun allInputFields_areDisplayed() {
        composeTestRule.setContent {
            POSTheme {
                LoginInEmployeeDialog(
                    uiState = LoginEmployeeUiState(),
                    formState = LoginEmployeeFormState(),
                    onFormEvent = {},
                    showMessage = {},
                    onShowMessage = {},
                    onDismiss = {},
                )
            }
        }

        // Company code field
        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.feature_login_employee_label_company_code))
            .assertIsDisplayed()

        // Employee ID field
        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.feature_login_employee_label_uid))
            .assertIsDisplayed()

        // Password field
        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.feature_login_employee_login_label_password))
            .assertIsDisplayed()

        // Login button
        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.feature_login_employee_login_button_text))
            .assertIsDisplayed()
    }

    @Test
    fun validationErrors_whenCompanyCodeEmpty_showsError() {
        composeTestRule.setContent {
            POSTheme {
                LoginInEmployeeDialog(
                    uiState = LoginEmployeeUiState(),
                    formState = LoginEmployeeFormState(
                        companyCodeError = R.string.feature_login_employee_error_company_code_empty,
                    ),
                    onFormEvent = {},
                    showMessage = {},
                    onShowMessage = {},
                    onDismiss = {},
                )
            }
        }

        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.feature_login_employee_error_company_code_empty))
            .assertIsDisplayed()
    }

    @Test
    fun validationErrors_whenEmployeeIdEmpty_showsError() {
        composeTestRule.setContent {
            POSTheme {
                LoginInEmployeeDialog(
                    uiState = LoginEmployeeUiState(),
                    formState = LoginEmployeeFormState(
                        employeeIdError = R.string.feature_login_employee_error_name_empty,
                    ),
                    onFormEvent = {},
                    showMessage = {},
                    onShowMessage = {},
                    onDismiss = {},
                )
            }
        }

        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.feature_login_employee_error_name_empty))
            .assertIsDisplayed()
    }

    @Test
    fun validationErrors_whenPasswordEmpty_showsError() {
        composeTestRule.setContent {
            POSTheme {
                LoginInEmployeeDialog(
                    uiState = LoginEmployeeUiState(),
                    formState = LoginEmployeeFormState(
                        passwordError = R.string.feature_login_employee_error_password_empty,
                    ),
                    onFormEvent = {},
                    showMessage = {},
                    onShowMessage = {},
                    onDismiss = {},
                )
            }
        }

        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.feature_login_employee_error_password_empty))
            .assertIsDisplayed()
    }

    @Test
    fun inputField_whenTextEntered_updatesValue() {
        var capturedEvent: LoginEmployeeFormEvent? = null

        composeTestRule.setContent {
            POSTheme {
                LoginInEmployeeDialog(
                    uiState = LoginEmployeeUiState(),
                    formState = LoginEmployeeFormState(),
                    onFormEvent = { capturedEvent = it },
                    showMessage = {},
                    onShowMessage = {},
                    onDismiss = {},
                )
            }
        }

        // Enter company code
        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.feature_login_employee_label_company_code))
            .performTextInput("TEST123")

        assert(capturedEvent is LoginEmployeeFormEvent.CompanyCodeChanged)
    }

    @Test
    fun loginButton_whenClicked_triggersSubmitEvent() {
        var submitClicked = false

        composeTestRule.setContent {
            POSTheme {
                LoginInEmployeeDialog(
                    uiState = LoginEmployeeUiState(),
                    formState = LoginEmployeeFormState(
                        companyCode = "TEST",
                        employeeId = "emp1",
                        password = "pass",
                    ),
                    onFormEvent = { event ->
                        if (event == LoginEmployeeFormEvent.SubmitClicked) {
                            submitClicked = true
                        }
                    },
                    showMessage = {},
                    onShowMessage = {},
                    onDismiss = {},
                )
            }
        }

        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.feature_login_employee_login_button_text))
            .performClick()

        assert(submitClicked)
    }
}
