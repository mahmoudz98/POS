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
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Employee
import org.junit.Rule
import kotlin.test.Test
import com.casecode.pos.core.ui.R.string as uiString

class EmployeeScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun circularProgressIndicator_whenScreenIsLoading_exists() {
        composeTestRule.setContent {
            EmployeesScreen(
                uiState = UiEmployeesState(resourceEmployees = Resource.loading()),
                onAddClick = {},
                onEmployeeClick = {},
                onItemLongClick = {},
            )
        }
        composeTestRule.onNodeWithContentDescription("LoadingEmployees")
            .assertExists()
    }

    @Test
    fun employeesResourceEmpty_whenScreenIsEmpty_exists() {
        composeTestRule.setContent {
            EmployeesScreen(
                uiState = UiEmployeesState(resourceEmployees = Resource.empty()),
                onAddClick = {},
                onEmployeeClick = {},
                onItemLongClick = {},
            )
        }
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(uiString.core_ui_employees_empty_title))
    }

    @Test
    fun employeesResourceError_whenScreenIsError_exists() {
        composeTestRule.setContent {
            EmployeesScreen(
                uiState = UiEmployeesState(resourceEmployees = Resource.error(uiString.core_ui_error_unknown)),
                onAddClick = {},
                onEmployeeClick = {},
                onItemLongClick = {},
            )
        }
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(uiString.core_ui_error_unknown))
    }

    @Test
    fun employeesResourceSuccess_whenScreenIsSuccess_exists() {
        composeTestRule.setContent {
            EmployeesScreen(
                uiState = UiEmployeesState(resourceEmployees = Resource.success(employees)),
                onAddClick = {},
                onEmployeeClick = {},
                onItemLongClick = {},
            )
        }
        composeTestRule.onNodeWithText(employees[0].name).assertIsDisplayed()
        composeTestRule.onNodeWithText(employees[1].name).assertIsDisplayed()
    }

    val employees = listOf(
        Employee(
            name = "John Doe",
            phoneNumber = "123-456-7890",
            permission = "Admin",
            branchName = "Branch 1",
            password = "password",
        ),
        Employee(
            name = "Jane Smith",
            phoneNumber = "987-654-3210",
            permission = "User",
            branchName = "Branch 2",
            password = "password2",
        ),
    )
}