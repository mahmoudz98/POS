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
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.casecode.pos.core.ui.R
import org.junit.Rule
import org.junit.Test

class EmployeeScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `addEmployeeFab_showsAddEmployeeDialog`() {
        // Arrange
        composeTestRule.setContent {
            // EmployeesScreen()
        }

        // Act
        composeTestRule.onNodeWithContentDescription(composeTestRule.activity.getString(R.string.core_ui_add_employee_button_text)).performClick()

        // Assert
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.core_ui_add_employee_title)).assertExists()
    }
}
