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

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.casecode.pos.core.designsystem.component.FilterTitleText
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosElevatedFilterChip
import com.casecode.pos.core.designsystem.component.PosFilterChip
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.ui.DevicePreviews
import com.casecode.pos.core.ui.PosFilterScreen

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun FilterScreenOverlay(
    visible: Boolean,
    sharedTransitionScope: SharedTransitionScope,
    categories: Set<String>,
    filterUiState: FilterUiState,
    onSortFilterStockChanged: (FilterStockState) -> Unit,
    onSortPriceChanged: (SortPriceState) -> Unit,
    onCategorySelected: (String) -> Unit,
    onCategoryUnselected: (String) -> Unit,
    onRestDefaultFilter: () -> Unit,
    onDismiss: () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        FilterItemsScreen(
            animatedVisibilityScope = this,
            sharedTransitionScope = sharedTransitionScope,
            categories = categories,
            filterUiState = filterUiState,
            onSortFilterStockChanged = onSortFilterStockChanged,
            onSortPriceChanged = onSortPriceChanged,
            onCategorySelected = onCategorySelected,
            onCategoryUnselected = onCategoryUnselected,
            onRestDefaultFilter = onRestDefaultFilter,
            onDismiss = onDismiss,
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun FilterItemsScreen(
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    categories: Set<String>,
    filterUiState: FilterUiState,
    onSortFilterStockChanged: (FilterStockState) -> Unit,
    onSortPriceChanged: (SortPriceState) -> Unit,
    onCategorySelected: (String) -> Unit,
    onCategoryUnselected: (String) -> Unit,
    onRestDefaultFilter: () -> Unit,
    onDismiss: () -> Unit,
) {
    val resetEnabled =
        filterUiState.stockFilter != FilterStockState.All ||
            filterUiState.selectedCategories.isNotEmpty() ||
            filterUiState.sortPrice != SortPriceState.None

    PosFilterScreen(
        modifier = modifier,
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        enableFilter = resetEnabled,
        onRestFilterClick = { onRestDefaultFilter() },
        onDismiss = onDismiss,
    ) {
        FilterStockSection(
            sortState = filterUiState.stockFilter,
            onSortFilterStockChanged = onSortFilterStockChanged,
        )
        SortPriceFilterSection(
            sortPriceState = filterUiState.sortPrice,
            onSortPriceChanged = onSortPriceChanged,
        )
        FilterCategorySection(
            categories = categories,
            selectedCategories = filterUiState.selectedCategories,
            onCategorySelected = onCategorySelected,
            onCategoryUnselected = onCategoryUnselected,
        )
        BackHandler(onBack = onDismiss)
    }
}

@Composable
fun FilterStockSection(
    sortState: FilterStockState,
    onSortFilterStockChanged: (FilterStockState) -> Unit,
) {
    FilterTitleText(text = stringResource(id = R.string.feature_item_filter_stock_label))
    Column(Modifier.padding(bottom = 24.dp)) {
        FilterStock(
            sortState = sortState,
            onChanged = onSortFilterStockChanged,
        )
    }
}

@Composable
fun FilterStock(
    stockFilters: List<FilterStockState> = FilterStockState.entries,
    sortState: FilterStockState,
    onChanged: (FilterStockState) -> Unit,
) {
    stockFilters.forEach { filter ->
        PosElevatedFilterChip(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(filter.label)) },
            selected = sortState == filter,
            onSelectedChange = {
                onChanged(filter)
            },
        )
    }
}

@Composable
fun SortPriceFilterSection(
    sortPriceState: SortPriceState,
    onSortPriceChanged: (SortPriceState) -> Unit,
) {
    FilterTitleText(text = stringResource(id = R.string.feature_item_sort_price_label))
    Column(Modifier.padding(bottom = 24.dp)) {
        SortPriceFilter(
            sortState = sortPriceState,
            onChanged = onSortPriceChanged,
        )
    }
}

@Composable
private fun SortPriceFilter(
    stockFilters: List<SortPriceState> = SortPriceState.entries,
    sortState: SortPriceState,
    onChanged: (SortPriceState) -> Unit,
) {
    stockFilters.forEach { filter ->
        PosElevatedFilterChip(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(filter.label)) },
            selected = sortState == filter,
            onSelectedChange = {
                onChanged(filter)
            },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun FilterCategorySection(
    categories: Set<String>,
    selectedCategories: Set<String>,
    onCategorySelected: (String) -> Unit,
    onCategoryUnselected: (String) -> Unit,
) {
    FilterTitleText(text = stringResource(R.string.feature_item_filter_category_label))
    FlowRow(
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 8.dp)
            .padding(horizontal = 4.dp),
    ) {
        categories.forEach { category ->
            val isSelected = selectedCategories.contains(category)
            PosFilterChip(
                selected = isSelected,
                onSelectedChange = {
                    if (isSelected) {
                        onCategoryUnselected(category)
                    } else {
                        onCategorySelected(category)
                    }
                },
                label = { Text(text = category) },
                modifier = Modifier.padding(end = 4.dp, bottom = 8.dp),
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@DevicePreviews
@Composable
fun FilterItemScreenPreview() {
    POSTheme {
        PosBackground {
            SharedTransitionLayout {
                FilterScreenOverlay(
                    visible = true,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    categories = setOf("Phones", "laptops", "Tablets"),
                    filterUiState = FilterUiState(),
                    onSortFilterStockChanged = {},
                    onSortPriceChanged = {},
                    onCategorySelected = {},
                    onCategoryUnselected = {},
                    onRestDefaultFilter = {},
                    onDismiss = {},
                )
            }
        }
    }
}