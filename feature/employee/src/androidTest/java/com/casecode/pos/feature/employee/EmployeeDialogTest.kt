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
import androidx.compose.ui.test.performTextReplacement
import com.casecode.pos.core.model.data.users.Branch
import org.junit.Rule
import kotlin.test.Test
import com.casecode.pos.core.ui.R.string as uiString

class EmployeeDialogTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    val adminPermission by lazy {
        composeTestRule.activity.getString(uiString.core_ui_permission_admin_text)
    }

    @Test
    fun whenEmployeeInputEmpty_thenShowError() {
        composeTestRule.setContent {
            EmployeeDialog(
                isUpdate = false,
                employeeUpdate = null,
                countryIsoCode = "en",
                branches = listOf(Branch(1, "Branch 1")),
                onAddEmployee = { _ -> },
                onUpdateEmployee = { _ -> },
                onDismiss = {},
            )
        }
        // Click the Add button without entering any data
        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(uiString.core_ui_add_employee_button_text),
            )
            .performClick()
        // Verify error messages for empty name, phone, password, branch, and permission
        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(uiString.core_ui_error_employee_name_empty),
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(uiString.core_ui_error_phone_empty))
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(
                    uiString.core_ui_error_add_employee_password_empty,
                ),
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(
                    uiString.core_ui_error_add_employee_branch_empty,
                ),
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(
                    uiString.core_ui_error_add_employee_permission_empty,
                ),
            )
            .assertIsDisplayed()
    }

    @Test
    fun whenInputEmployeeValidate_thenAssertErrorValidationNotExist() {
        val branches = listOf(Branch(1, "Branch 1"))
        composeTestRule.setContent {
            EmployeeDialog(
                isUpdate = false,
                employeeUpdate = null,
                countryIsoCode = "EG",
                branches = branches,
                onAddEmployee = {},
                onUpdateEmployee = {},
                onDismiss = {},
            )
        }
        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(uiString.core_ui_employee_name_hint))
            .performTextReplacement("John Doe")
        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(uiString.core_ui_work_phone_number_hint),
            )
            .performTextReplacement("1234567890")
        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(uiString.core_ui_employee_password_hint),
            )
            .performTextReplacement("password123")
        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(uiString.core_ui_branch_name_hint))
            .performClick()
        composeTestRule.onNodeWithText(branches[0].branchName).assertIsDisplayed().performClick()
        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(uiString.core_ui_permissions_text))
            .performClick()
        composeTestRule.onNodeWithText(adminPermission).assertIsDisplayed().performClick()

        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(uiString.core_ui_add_employee_button_text),
            )
            .performClick()

        // Check for absence of validation errors
        listOf(
            uiString.core_ui_error_employee_name_empty,
            uiString.core_ui_error_phone_invalid,
            uiString.core_ui_error_add_employee_password,
            uiString.core_ui_error_add_employee_branch_empty,
            uiString.core_ui_error_add_employee_permission_empty,
        )
            .forEach { errorStringRes ->
                composeTestRule
                    .onAllNodesWithText(composeTestRule.activity.getString(errorStringRes))
                    .assertCountEquals(0)
            }
    }
}