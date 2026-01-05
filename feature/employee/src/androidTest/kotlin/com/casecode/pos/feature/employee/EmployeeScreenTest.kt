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
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import com.casecode.pos.core.model.business.Employee
import com.casecode.pos.core.model.business.EmployeeRole
import com.casecode.pos.core.ui.R
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

class EmployeeScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun whenLoading_thenShowLoadingWheel() {
        // Given a loading state
        val loadingState = EmployeeUiState(isLoading = true)

        // When content is set
        composeTestRule.setContent {
            EmployeesScreen(
                uiState = loadingState,
                onEventClick = {},
            )
        }

        // Then verify loading indicator exists
        composeTestRule
            .onNodeWithContentDescription("LoadingEmployees")
            .assertExists()
    }

    @Test
    fun whenEmpty_thenShowEmptyScreen() {
        // Given an empty state
        val emptyState = EmployeeUiState(employees = emptyList(), isLoading = false)

        // When content is set
        composeTestRule.setContent {
            EmployeesScreen(
                uiState = emptyState,
                onEventClick = {},
            )
        }

        // Then verify empty screen title exists
        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.core_ui_employees_empty_title))
            .assertExists()
    }

    @Test
    fun whenSuccess_thenShowEmployeesList() {
        // Given a list of employees
        val employees = listOf(
            Employee(
                id = "1",
                name = "Alice",
                phone = "111",
                role = EmployeeRole.CASHIER,
                assignedBranchId = "b1",
            ),
            Employee(
                id = "2",
                name = "Bob",
                phone = "222",
                role = EmployeeRole.MANAGER,
                assignedBranchId = "b1",
            ),
        )
        val successState = EmployeeUiState(employees = employees)

        // When content is set
        composeTestRule.setContent {
            EmployeesScreen(
                uiState = successState,
                onEventClick = {},
            )
        }

        // Then verify employee names are displayed
        composeTestRule.onNodeWithText("Alice").assertExists()
        composeTestRule.onNodeWithText("Bob").assertExists()
    }

    @Test
    fun whenAddFabClicked_thenTriggerCreationEvent() {
        // Given a captured event
        var capturedEvent: EmployeeEvent? = null

        // When content is set and FAB is clicked
        composeTestRule.setContent {
            EmployeesScreen(
                uiState = EmployeeUiState(employees = emptyList()),
                onEventClick = { capturedEvent = it },
            )
        }

        composeTestRule.onNodeWithContentDescription(
            composeTestRule.activity.getString(R.string.core_ui_add_employee_button_text),
        ).performClick()

        // Then verify correct event was triggered
        assertEquals(EmployeeEvent.CreationEmployeeOpened, capturedEvent)
    }

    @Test
    fun whenEmployeeItemClicked_thenTriggerUpdatingEvent() {
        // Given an employee and a captured event
        val employee = Employee(
            id = "1",
            name = "John Doe",
            phone = "1234567890",
            role = EmployeeRole.CASHIER,
            assignedBranchId = "branch1",
        )
        var capturedEvent: EmployeeEvent? = null

        // When content is set and item is clicked
        composeTestRule.setContent {
            EmployeesScreen(
                uiState = EmployeeUiState(employees = listOf(employee)),
                onEventClick = { capturedEvent = it },
            )
        }

        composeTestRule.onNodeWithText("John Doe").performClick()

        // Then verify correct event was triggered with employee
        assertEquals(EmployeeEvent.UpdatingEmployeeOpened(employee), capturedEvent)
    }

    @Test
    fun whenEmployeeItemLongClicked_thenTriggerDeletingEvent() {
        // Given an employee and a captured event
        val employee = Employee(
            id = "1",
            name = "Jane Doe",
            phone = "0987654321",
            role = EmployeeRole.MANAGER,
            assignedBranchId = "branch1",
        )
        var capturedEvent: EmployeeEvent? = null

        // When content is set and item is long clicked
        composeTestRule.setContent {
            EmployeesScreen(
                uiState = EmployeeUiState(employees = listOf(employee)),
                onEventClick = { capturedEvent = it },
            )
        }

        composeTestRule.onNodeWithText("Jane Doe").performTouchInput {
            longClick()
        }

        // Then verify correct event was triggered
        assertEquals(EmployeeEvent.DeletingEmployeeOpened(employee), capturedEvent)
    }

    @Test
    fun whenActionIconClicked_thenTriggerCompanyCodeEvent() {
        // Given a captured event
        var capturedEvent: EmployeeEvent? = null

        // When content is set and action icon is clicked
        composeTestRule.setContent {
            EmployeesScreen(
                uiState = EmployeeUiState(employees = emptyList()),
                onEventClick = { capturedEvent = it },
            )
        }

        composeTestRule.onNodeWithContentDescription(
            composeTestRule.activity.getString(com.casecode.pos.feature.employee.R.string.feature_employee_dialog_title_company_code),
        ).performClick()

        // Then verify correct event was triggered
        assertEquals(EmployeeEvent.CompanyCodeOpened, capturedEvent)
    }
}
