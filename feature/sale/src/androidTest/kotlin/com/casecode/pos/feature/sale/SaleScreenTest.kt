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
package com.casecode.pos.feature.sale

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.casecode.pos.core.testing.data.itemsTestData
import org.junit.Rule
import org.junit.Test

class SaleScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun loadingState_showsLoadingIndicator() {
        composeTestRule.setContent {
            SaleScreen(
                itemsUiState = ItemsUiState.Loading,
                saleItemsState = emptySet(),
                searchItemsUiState = SearchItemsUiState.EmptySearch,
                searchQuery = "",
                amountInput = "",
                restOfAmount = 0.0,
                onSearchQueryChanged = {},
                userMessage = null,
                onSnackbarMessageShown = {},
                onScan = {},
                onSearchItemClick = {},
                onGoToItems = {},
                onRemoveItem = {},
                onUpdateQuantity = {},
                onAmountChanged = {},
                onSaveInvoice = {},
                totalSaleItems = 0.0,
            )
        }
        composeTestRule.onNodeWithContentDescription("SaleLoading").assertExists()
    }

    @Test
    fun emptyState_showsEmptyView() {
        composeTestRule.setContent {
            SaleScreen(
                itemsUiState = ItemsUiState.Empty,
                saleItemsState = emptySet(),
                searchItemsUiState = SearchItemsUiState.EmptySearch,
                searchQuery = "",
                amountInput = "",
                totalSaleItems = 0.0,
                restOfAmount = 0.0,
                onSearchQueryChanged = {},
                userMessage = null,
                onSnackbarMessageShown = {},
                onScan = {},
                onSearchItemClick = {},
                onGoToItems = {},
                onRemoveItem = {},
                onUpdateQuantity = {},
                onAmountChanged = {},
                onSaveInvoice = {},
            )
        }
        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(R.string.feature_sale_items_empty_message),
        ).assertExists()
    }

    @Test
    fun successState_withItems_showsItems() {
        val testItems = itemsTestData.toSet()
        composeTestRule.setContent {
            SaleScreen(
                itemsUiState = ItemsUiState.Success,
                saleItemsState = testItems,
                searchItemsUiState = SearchItemsUiState.EmptySearch,
                searchQuery = "",
                amountInput = "",
                totalSaleItems = 0.0,
                restOfAmount = 0.0,
                onSearchQueryChanged = {},
                userMessage = null,
                onSnackbarMessageShown = {},
                onScan = {},
                onSearchItemClick = {},
                onGoToItems = {},
                onRemoveItem = {},
                onUpdateQuantity = {},
                onAmountChanged = {},
                onSaveInvoice = {},
            )
        }
        composeTestRule.onNodeWithText(testItems.first().name).assertExists()
    }

    @Test
    fun searchBar_performsSearch() {
        val searchQuery = "test"
        composeTestRule.setContent {
            SaleScreen(
                itemsUiState = ItemsUiState.Success,
                saleItemsState = emptySet(),
                totalSaleItems = 0.0,
                searchItemsUiState = SearchItemsUiState.EmptySearch,
                searchQuery = searchQuery,
                amountInput = "",
                restOfAmount = 0.0,
                onSearchQueryChanged = {},
                userMessage = null,
                onSnackbarMessageShown = {},
                onScan = {},
                onSearchItemClick = {},
                onGoToItems = {},
                onRemoveItem = {},
                onUpdateQuantity = {},
                onAmountChanged = {},
                onSaveInvoice = {},
            )
        }
        composeTestRule.onNodeWithText(searchQuery).assertExists()
    }

    @Test
    fun amountInput_showsCorrectAmount() {
        val amount = "100.00"
        composeTestRule.setContent {
            SaleScreen(
                itemsUiState = ItemsUiState.Success,
                saleItemsState = setOf(itemsTestData[0]),
                searchItemsUiState = SearchItemsUiState.EmptySearch,
                searchQuery = "",
                totalSaleItems = 0.0,
                amountInput = amount,
                restOfAmount = 0.0,
                onSearchQueryChanged = {},
                userMessage = null,
                onSnackbarMessageShown = {},
                onScan = {},
                onSearchItemClick = {},
                onGoToItems = {},
                onRemoveItem = {},
                onUpdateQuantity = {},
                onAmountChanged = {},
                onSaveInvoice = {},
            )
        }
        composeTestRule.onNodeWithText(amount).assertExists()
    }

    @Test
    fun removeItem_triggersCallback() {
        var itemRemoved = false
        val testItem = itemsTestData[0]

        composeTestRule.setContent {
            SaleScreen(
                itemsUiState = ItemsUiState.Success,
                saleItemsState = setOf(testItem),
                searchItemsUiState = SearchItemsUiState.EmptySearch,
                totalSaleItems = 0.0,
                searchQuery = "",
                amountInput = "",
                restOfAmount = 0.0,
                onSearchQueryChanged = {},
                userMessage = null,
                onSnackbarMessageShown = {},
                onScan = {},
                onSearchItemClick = {},
                onGoToItems = {},
                onRemoveItem = { itemRemoved = true },
                onUpdateQuantity = {},
                onAmountChanged = {},
                onSaveInvoice = {},
            )
        }

        composeTestRule
            .onNodeWithContentDescription(
                composeTestRule.activity.getString(R.string.feature_sale_dialog_delete_invoice_item_title),
            )
            .performClick()

        assert(itemRemoved)
    }

    @Test
    fun searchResults_showsMatchingItems() {
        val searchResults = itemsTestData.take(2)
        composeTestRule.setContent {
            SaleScreen(
                itemsUiState = ItemsUiState.Success,
                saleItemsState = emptySet(),
                searchItemsUiState = SearchItemsUiState.Success(searchResults),
                searchQuery = "test",
                totalSaleItems = 0.0,
                amountInput = "",
                restOfAmount = 0.0,
                onSearchQueryChanged = {},
                userMessage = null,
                onSnackbarMessageShown = {},
                onScan = {},
                onSearchItemClick = {},
                onGoToItems = {},
                onRemoveItem = {},
                onUpdateQuantity = {},
                onAmountChanged = {},
                onSaveInvoice = {},
            )
        }

        searchResults.forEach { item ->
            composeTestRule.onNodeWithText(item.name).assertExists()
        }
    }
}