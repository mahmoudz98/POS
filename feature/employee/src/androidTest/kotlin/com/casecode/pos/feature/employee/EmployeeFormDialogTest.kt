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
package com.casecode.pos.feature.employee

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.casecode.pos.core.model.business.Branch
import com.casecode.pos.core.model.business.EmployeeRole
import org.junit.Rule
import kotlin.test.Test
import com.casecode.pos.core.ui.R.string as uiString

class EmployeeFormDialogTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val branches = listOf(Branch(name = "Branch 1", phone = "1231232"))

    @Test
    fun whenStateHasErrors_thenErrorMessagesAreDisplayed() {
        // Given a state with errors
        val errorState = EmployeeFormUiState(
            formErrors = FormErrors(
                nameError = uiString.core_ui_error_employee_name_empty,
                phoneError = uiString.core_ui_error_phone_empty,
                passwordError = uiString.core_ui_error_add_employee_password_empty,
                assignedBranchesError = uiString.core_ui_error_branch_name_empty,
            ),
        )

        // When content is set
        composeTestRule.setContent {
            EmployeeFormDialog(
                isUpdate = false,
                uiState = errorState,
                onEvent = { _ -> },
                branches = branches,
                onDismiss = {},
            )
        }

        // Then verify error messages are displayed
        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(uiString.core_ui_error_employee_name_empty))
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(uiString.core_ui_error_phone_empty))
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(uiString.core_ui_error_add_employee_password_empty))
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(uiString.core_ui_error_branch_name_empty))
            .assertIsDisplayed()
    }

    @Test
    fun whenStateIsValid_thenNoErrorMessagesAreDisplayed() {
        // Given a valid state
        val validState = EmployeeFormUiState(
            name = "John Doe",
            phone = "1234567890",
            password = "password123",
            role = EmployeeRole.CASHIER,
            assignedBranchId = branches[0].id,
        )

        // When content is set
        composeTestRule.setContent {
            EmployeeFormDialog(
                isUpdate = false,
                uiState = validState,
                onEvent = { _ -> },
                branches = branches,
                onDismiss = {},
            )
        }

        // Then verify no error messages are displayed
        listOf(
            uiString.core_ui_error_employee_name_empty,
            uiString.core_ui_error_phone_empty,
            uiString.core_ui_error_add_employee_password_empty,
            uiString.core_ui_error_branch_name_empty,
        ).forEach { errorRes ->
            composeTestRule
                .onAllNodesWithText(composeTestRule.activity.getString(errorRes))
                .assertCountEquals(0)
        }
    }

    @Test
    fun whenUpdateEmployee_thenFieldsArePopulated() {
        // Given
        val uiState = EmployeeFormUiState(
            name = "Existing Employee",
            phone = "1112223333",
            password = "password",
            role = EmployeeRole.CASHIER,
            assignedBranchId = branches[0].id,
        )

        // When
        composeTestRule.setContent {
            EmployeeFormDialog(
                isUpdate = true,
                uiState = uiState,
                onEvent = { _ -> },
                branches = branches,
                onDismiss = {},
            )
        }

        // Then
        composeTestRule.onNodeWithText("Existing Employee").assertIsDisplayed()
        composeTestRule.onNodeWithText("1112223333").assertIsDisplayed()

        // Check Button Text
        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(uiString.core_ui_update_employee_button_text),
            )
            .assertIsDisplayed()

        // Check Title
        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(uiString.core_ui_update_employee_title),
            )
            .assertIsDisplayed()
    }

    @Test
    fun whenDeleteDialogShown_thenContentIsCorrect() {
        // Given
        var confirmClicked = false
        var dismissClicked = false

        // When
        composeTestRule.setContent {
            com.casecode.pos.core.ui.PosDeleteDialog(
                titleRes = com.casecode.pos.feature.employee.R.string.feature_employee_dialog_delete_title,
                messageRes = com.casecode.pos.feature.employee.R.string.feature_employee_dialog_delete_message,
                onConfirm = { confirmClicked = true },
                onDismiss = { dismissClicked = true },
            )
        }

        // Then
        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(
                    com.casecode.pos.feature.employee.R.string.feature_employee_dialog_delete_title,
                ),
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(
                    com.casecode.pos.feature.employee.R.string.feature_employee_dialog_delete_message,
                ),
            )
            .assertIsDisplayed()

        // Test Buttons
        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(uiString.core_ui_dialog_cancel_button_text))
            .performClick()
        assert(dismissClicked)

        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(uiString.core_ui_dialog_delete_button_text))
            .performClick()
        assert(confirmClicked)
    }
}
