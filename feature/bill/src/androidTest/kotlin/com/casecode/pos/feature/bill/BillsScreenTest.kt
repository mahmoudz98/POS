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
package com.casecode.pos.feature.bill

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.casecode.pos.core.designsystem.component.SearchWidgetState
import com.casecode.pos.core.testing.data.supplierInvoicesTestData
import org.junit.Rule
import org.junit.Test

class BillsScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun circularProgressIndicator_whenScreenIsLoading_exists() {
        composeTestRule.setContent {
            BillsScreen(
                uiState = BillsUiState.Loading,
                searchWidgetState = SearchWidgetState.CLOSED,
                filterUiState = BillsFilterUiState(),
                searchQuery = "",
                userMessage = null,
                onBillsUiEvent = {},
                onBillsEffect = {},
            )
        }

        composeTestRule.onNodeWithContentDescription("LoadingBills").assertExists()
    }

    @Test
    fun emptyItemsScreen_whenEmptyState_exists() {
        composeTestRule.setContent {
            BillsScreen(
                uiState = BillsUiState.Empty,
                searchWidgetState = SearchWidgetState.CLOSED,
                searchQuery = "",
                filterUiState = BillsFilterUiState(),
                userMessage = null,
                onBillsUiEvent = {},
                onBillsEffect = {},
            )
        }
        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(R.string.feature_bill_empty_title),
            )
            .assertExists()
    }

    @Test
    fun bills_whenSuccessState_isShown() {
        val testSupplierInvoices = supplierInvoicesTestData.associateBy { it.invoiceId }
        composeTestRule.setContent {
            BillsScreen(
                uiState = BillsUiState.Success(testSupplierInvoices),
                searchWidgetState = SearchWidgetState.CLOSED,
                searchQuery = "",
                filterUiState = BillsFilterUiState(),
                userMessage = null,
                onBillsUiEvent = {},
                onBillsEffect = {},
            )
        }
/*        composeTestRule.onNodeWithText(testSupplierInvoices.values.first().supplierName)
            .assertExists()
        composeTestRule.onNodeWithText(testSupplierInvoices.values.first().paymentStatus.name)
            .assertExists()*/
    }

    @Test
    fun searchBar_whenSearchWidgetStateIsOpened_isFocused() {
        val testSupplierInvoices = supplierInvoicesTestData.associateBy { it.invoiceId }
        composeTestRule.setContent {
            BillsScreen(
                uiState = BillsUiState.Success(testSupplierInvoices),
                searchWidgetState = SearchWidgetState.OPENED,
                searchQuery = "",
                filterUiState = BillsFilterUiState(),
                userMessage = null,
                onBillsUiEvent = {},
                onBillsEffect = {},
            )
        }
        composeTestRule.onNodeWithTag("searchTextField").assertIsFocused()
    }

    @Test
    fun emptySearchResult_emptySearchIsDisplayed() {
        composeTestRule.setContent {
            BillsScreen(
                uiState = BillsUiState.Success(emptyMap()),
                searchWidgetState = SearchWidgetState.CLOSED,
                searchQuery = "",
                filterUiState = BillsFilterUiState(),
                userMessage = null,
                onBillsUiEvent = {},
                onBillsEffect = {},
            )
        }
        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(R.string.feature_bill_empty_filter_message),
            )
            .assertExists()
    }
}