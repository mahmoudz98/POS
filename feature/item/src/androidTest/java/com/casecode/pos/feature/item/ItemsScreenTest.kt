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
package com.casecode.pos.feature.item

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.casecode.pos.core.designsystem.component.SearchWidgetState
import com.casecode.pos.core.testing.data.itemsTestData
import org.junit.Rule
import org.junit.Test

class ItemsScreenTest {
    @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun circularProgressIndicator_whenScreenIsLoading_exists() {
        composeTestRule.setContent {
            ItemsScreen(
                uiState = ItemsUIState.Loading,
                searchWidgetState = SearchWidgetState.CLOSED,
                onSearchClicked = {},
                searchQuery = "",
                categories = setOf<String>(),
                filterUiState = FilterUiState(),
                onBackClick = {},
                onSearchQueryChanged = {},
                onClearRecentSearches = {},
                onFilterStockChange = {},
                onCategorySelected = {},
                onCategoryUnselected = {},
                onSortPriceChanged = {},
                onClearFilter = {},
                userMessage = null,
                onAddItemClick = {},
                onItemClick = {},
                onItemLongClick = {},
                onPrintItemClick = {},
                onShownMessage = {},
            )
        }
        composeTestRule.onNodeWithContentDescription("LoadingItems").assertExists()
    }

    @Test
    fun emptyItemsScreen_whenEmptyState_exists() {
        composeTestRule.setContent {
            ItemsScreen(
                uiState = ItemsUIState.Empty,
                searchWidgetState = SearchWidgetState.CLOSED,
                onSearchClicked = {},
                onBackClick = {},
                searchQuery = "",
                categories = setOf<String>(),
                filterUiState = FilterUiState(),
                onSearchQueryChanged = {},
                onClearRecentSearches = {},
                onFilterStockChange = {},
                onCategorySelected = {},
                onCategoryUnselected = {},
                onSortPriceChanged = {},
                onClearFilter = {},
                userMessage = null,
                onAddItemClick = {},
                onItemClick = {},
                onItemLongClick = {},
                onPrintItemClick = {},
                onShownMessage = {},
            )
        }
        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(R.string.feature_item_empty_items_title),
            )
            .assertExists()
    }

    @Test
    fun items_whenSuccessState_isShown() {
        val testItemsData = itemsTestData
        composeTestRule.setContent {
            ItemsScreen(
                uiState = ItemsUIState.Success(mapOf(), testItemsData),
                searchWidgetState = SearchWidgetState.CLOSED,
                onBackClick = {},
                onSearchClicked = {},
                searchQuery = "",
                categories = setOf<String>(),
                filterUiState = FilterUiState(),
                onSearchQueryChanged = {},
                onClearRecentSearches = {},
                onFilterStockChange = {},
                onCategorySelected = {},
                onCategoryUnselected = {},
                onSortPriceChanged = {},
                onClearFilter = {},
                userMessage = null,
                onAddItemClick = {},
                onItemClick = {},
                onItemLongClick = {},
                onPrintItemClick = {},
                onShownMessage = {},
            )
        }
        composeTestRule.onNodeWithText(testItemsData[0].name).assertExists()
        composeTestRule.onNodeWithText(testItemsData[1].name).assertExists()
        composeTestRule.onNodeWithText(testItemsData[2].name).assertExists()
    }

    @Test
    fun itemsEmptyScreen_WhenItemsUiStateIsError_exists() {
        composeTestRule.setContent {
            ItemsScreen(
                uiState = ItemsUIState.Error,
                searchWidgetState = SearchWidgetState.CLOSED,
                onBackClick = {},
                onSearchClicked = {},
                searchQuery = "",
                categories = setOf<String>(),
                filterUiState = FilterUiState(),
                onSearchQueryChanged = {},
                onClearRecentSearches = {},
                onFilterStockChange = {},
                onCategorySelected = {},
                onCategoryUnselected = {},
                onSortPriceChanged = {},
                onClearFilter = {},
                userMessage = null,
                onAddItemClick = {},
                onItemClick = {},
                onItemLongClick = {},
                onPrintItemClick = {},
                onShownMessage = {},
            )
        }
        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(R.string.feature_item_empty_items_title),
            )
            .assertExists()
    }

    @Test
    fun searchBar_whenSearchWidgetStateIsOpened_isFocused() {
        composeTestRule.setContent {
            ItemsScreen(
                uiState = ItemsUIState.Empty,
                searchWidgetState = SearchWidgetState.OPENED,
                onBackClick = {},
                onSearchClicked = {},
                searchQuery = "",
                categories = setOf<String>(),
                filterUiState = FilterUiState(),
                onSearchQueryChanged = {},
                onClearRecentSearches = {},
                onFilterStockChange = {},
                onCategorySelected = {},
                onCategoryUnselected = {},
                onSortPriceChanged = {},
                onClearFilter = {},
                userMessage = null,
                onAddItemClick = {},
                onItemClick = {},
                onItemLongClick = {},
                onPrintItemClick = {},
                onShownMessage = {},
            )
        }
        composeTestRule.onNodeWithTag("searchTextField").assertIsFocused()
    }

    @Test
    fun emptySearchResult_emptySearchIsDisplayed() {
        val searchItemNotExist = "Item Not Exist"
        val testItemsData = itemsTestData.filter { it.name == searchItemNotExist }
        composeTestRule.setContent {
            ItemsScreen(
                uiState = ItemsUIState.Success(mapOf(), testItemsData),
                searchWidgetState = SearchWidgetState.OPENED,
                onBackClick = {},
                onSearchClicked = {},
                searchQuery = searchItemNotExist,
                categories = setOf<String>(),
                filterUiState = FilterUiState(),
                onSearchQueryChanged = {},
                onClearRecentSearches = {},
                onFilterStockChange = {},
                onCategorySelected = {},
                onCategoryUnselected = {},
                onSortPriceChanged = {},
                onClearFilter = {},
                userMessage = null,
                onAddItemClick = {},
                onItemClick = {},
                onItemLongClick = {},
                onPrintItemClick = {},
                onShownMessage = {},
            )
        }
        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(R.string.feature_item_empty_items_filter_title),
            )
            .assertExists()
    }
}