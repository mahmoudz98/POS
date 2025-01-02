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
package com.casecode.pos.feature.bill.detials

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import com.casecode.pos.core.ui.R.string as uiString

class AddBillScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    private val saveButtonLabel by lazy {
        composeTestRule.activity.getString(uiString.core_ui_save_action_text)
    }

/*    @Test
    fun whenBillInputEmpty_assertErrorsIsDisplayed() {
        composeTestRule.setContent {
            AddBillScreen(
                billInputState = BillInputState(),
                searchSupplier = "",
                userMessage = null,
                filterSupplierState = SearchSupplierUiState.EmptyQuery,
                onSearchSupplierChange = {},
                onNavigateBack = {},
                onAddBillItem = {},
                onUpdateBillItem = {},
                onRemoveBillItem = {},
                showSnackbarMessage = {},
                onShownMessage = {},
                onAddBill = {},

            )
        }
        composeTestRule.onNodeWithText(saveButtonLabel).performClick()
        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(
                R.string.feature_bill_supplier_not_selected_message,
            ),
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(
                R.string.feature_bill_number_empty,
            ),
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(
                R.string.feature_bill_items_empty_message,
            ),
        )
            .assertIsDisplayed()
    }

    @Test
    fun whenHasBillInput_assertErrorsIsDisplayed() {
        composeTestRule.setContent {
            AddBillScreen(
                billInputState = BillInputState(),
                searchSupplier = "",
                userMessage = null,
                filterSupplierState = SearchSupplierUiState.EmptyQuery,
                onSearchSupplierChange = {},
                onNavigateBack = {},
                onAddBillItem = {},
                onUpdateBillItem = {},
                onRemoveBillItem = {},
                showSnackbarMessage = {},
                onShownMessage = {},
                onAddBill = {},

            )
        }
        composeTestRule.onNodeWithText(saveButtonLabel).performClick()
        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(
                R.string.feature_bill_supplier_empty,
            ),
        )
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(
                R.string.feature_bill_number_empty,
            ),
        )
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(
                R.string.feature_bill_items_empty_message,
            ),
        )
            .assertIsDisplayed()
    }*/
}