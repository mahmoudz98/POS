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

import com.casecode.pos.core.model.data.users.Item

/**
 * Represents the current state of the Search Widget.
 */
enum class SearchWidgetState {
    OPENED,
    CLOSED,
}

/**
 * Represents the UI state for the items screen.
 */
sealed interface ItemsUIState {
    data object Loading : ItemsUIState

    data class Success(
        val items: Map<String, Item>,
        val filteredItems: List<Item> = emptyList(),
    ) : ItemsUIState

    data object Error : ItemsUIState
    data object Empty : ItemsUIState
}

/**
 * Represents the UI state for the filter screen.
 *
 * This class holds the currently selected filter options, including selected categories,
 * stock filter status, and price sorting order.
 *
 * @property selectedCategories A set of selected category names. Defaults to an empty set.
 * @property stockFilter The current stock filter state (e.g., All, In Stock, Out of Stock). Defaults to FilterStockState.All.
 * @property sortPrice The current price sorting state (e.g., None, Ascending, Descending). Defaults to SortPriceState.None.
 */
data class FilterUiState(
    val selectedCategories: Set<String> = emptySet(),
    val stockFilter: FilterStockState = FilterStockState.All,
    val sortPrice: SortPriceState = SortPriceState.None,
)