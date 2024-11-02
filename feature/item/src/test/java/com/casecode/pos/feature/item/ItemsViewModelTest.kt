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

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import com.casecode.pos.core.domain.usecase.AddItemUseCase
import com.casecode.pos.core.domain.usecase.DeleteItemUseCase
import com.casecode.pos.core.domain.usecase.GetItemsUseCase
import com.casecode.pos.core.domain.usecase.ItemImageUseCase
import com.casecode.pos.core.domain.usecase.UpdateItemUseCase
import com.casecode.pos.core.model.data.users.Item
import com.casecode.pos.core.testing.repository.TestItemImageRepository
import com.casecode.pos.core.testing.repository.TestItemRepository
import com.casecode.pos.core.testing.util.MainDispatcherRule
import com.casecode.pos.core.testing.util.TestNetworkMonitor
import io.mockk.mockk
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import com.casecode.pos.core.data.R.string as ResourcesData

class ItemsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Subject under test
    private lateinit var viewModel: ItemsViewModel
    private val itemRepository = TestItemRepository()
    private val itemImageRepository = TestItemImageRepository()
    private val networkMonitor = TestNetworkMonitor()
    private val getItems = GetItemsUseCase(itemRepository)
    private val addItem = AddItemUseCase(itemRepository)
    private val updateItem = UpdateItemUseCase(itemRepository)
    private val deleteItem = DeleteItemUseCase(itemRepository)
    private val getImage = ItemImageUseCase(itemImageRepository)

    private val bitmapMock: Bitmap = mockk<Bitmap>()

    @Before
    fun setup() {
        viewModel =
            ItemsViewModel(
                SavedStateHandle(),
                networkMonitor,
                getItems,
                addItem,
                updateItem,
                deleteItem,
                getImage,
            )
    }

    @Test
    fun itemsStateIsSuccessAfterLoadingItems() =
        runTest {
            backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.itemsUiState.collect() }
            backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.categoriesUiState.collect() }
            assertEquals(viewModel.itemsUiState.value, ItemsUIState.Loading)
            val itemsFromRepo = itemRepository.itemsTest
            val itemsMap = itemsFromRepo.associateBy { it.sku }
            val categoriesExpected = itemRepository.categoriesTest
            itemRepository.sendItems()

            assertEquals(
                viewModel.itemsUiState.value,
                ItemsUIState.Success(itemsMap, itemsFromRepo),
            )
            assertEquals(
                viewModel.categoriesUiState.value,
                categoriesExpected,
            )
        }

    @Test
    fun itemsUiState_isSuccessWithSearchFilter() = runTest {
        // Given: Search query is "Iphone"
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.itemsUiState.collect() }
        val searchQuery = "Iphone"
        viewModel.onSearchQueryChanged(searchQuery)
        itemRepository.sendItems() // Provide items to the repository

        // When: Items are fetched with the search query applied
        val itemsUiState = viewModel.itemsUiState.value as ItemsUIState.Success

        // Then: Items with "Iphone" in their name or SKU should be in filteredItems
        val expectedItems = itemRepository.itemsTest.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.sku.contains(searchQuery, ignoreCase = true)
        }

        assertEquals(itemsUiState.filteredItems, expectedItems)
    }

    @Test
    fun itemsUiState_isSuccessWithEmptySearchQuery() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.itemsUiState.collect() }

        // Given: Empty search query
        viewModel.onSearchQueryChanged("")
        itemRepository.sendItems()

        // When: Items are fetched
        val itemsUiState = viewModel.itemsUiState.value as ItemsUIState.Success

        // Then: All items from the repository are returned
        assertEquals(itemsUiState.filteredItems, itemRepository.itemsTest)
    }

    @Test
    fun `itemsUiState is Success and filters by selected category`() = runTest {
        // Given: A category filter is applied
        val selectedCategory = "Phones"
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.itemsUiState.collect() }
        viewModel.onCategorySelected(selectedCategory)
        itemRepository.sendItems()

        // When: Items are fetched with the category filter applied
        val itemsUiState = viewModel.itemsUiState.value as ItemsUIState.Success

        // Then: Verify that each filtered item belongs to the selected category
        assertTrue(
            itemsUiState.filteredItems.all { it.category == selectedCategory },
        )
    }

    @Test
    fun itemsUiState_isSuccessWithMultipleCategoryFilters() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.itemsUiState.collect() }

        // Given: Multiple categories are selected
        val categories = listOf("Phones", "Laptops")
        viewModel.onCategorySelected(categories[0])
        viewModel.onCategorySelected(categories[1])
        itemRepository.sendItems()

        // When: Items are fetched with multiple category filters applied
        val itemsUiState = viewModel.itemsUiState.value as ItemsUIState.Success

        // Then: All items belong to the selected categories
        assertTrue(
            itemsUiState.filteredItems.all { it.category in categories },
        )
    }

    @Test
    fun itemsUiState_isSuccessWithCategoryFilter() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.itemsUiState.collect() }

        // Given: Category is "Electronics"
        val category = "Laptops"
        val nameSearch = "macbook"
        viewModel.onCategorySelected(category)
        viewModel.onSearchQueryChanged(nameSearch)
        itemRepository.sendItems()

        // When: Items are fetched with the category filter applied
        val itemsUiState = viewModel.itemsUiState.value as ItemsUIState.Success

        // Then: check itemsFilter has category with name macbook

        assertTrue(
            itemsUiState.filteredItems.all { it.category == category },
        )
    }

    @Test
    fun itemsUiState_isSuccessWithStockFilter() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.itemsUiState.collect() }

        // Given: Stock filter is "InStock"
        viewModel.onSortFilterStockChanged(FilterStockState.InStock)
        itemRepository.sendItems()

        // When: Items are fetched with the stock filter applied
        val itemsUiState = viewModel.itemsUiState.value as ItemsUIState.Success

        assertTrue(
            itemsUiState.filteredItems.all { it.isInStockAndTracked() },
        )
    }

    @Test
    fun itemsUiState_isSuccessWithPriceSorting() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.itemsUiState.collect() }

        // Given: Price sorting is set to low to high
        viewModel.onSortPriceChanged(SortPriceState.LowToHigh)
        itemRepository.sendItems()

        // When: Items are fetched with price sorting applied
        val itemsUiState = viewModel.itemsUiState.value as ItemsUIState.Success

        // Then: Items should be sorted by price in ascending order
        val expectedItems = itemRepository.itemsTest.sortedBy { it.unitPrice }

        assertEquals(itemsUiState.filteredItems, expectedItems)
    }

    @Test
    fun itemsStateIsEmptyAfterLoadingItemsStates() =
        runTest {
            backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.itemsUiState.collect() }
            assertEquals(viewModel.itemsUiState.value, (ItemsUIState.Loading))
            itemRepository.setReturnEmpty(true)

            assertEquals(viewModel.itemsUiState.value, (ItemsUIState.Empty))
        }

    @Test
    fun itemsStateIsErrorAfterLoadingItemsStates() =
        runTest {
            backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.itemsUiState.collect() }

            assertEquals(viewModel.itemsUiState.value, (ItemsUIState.Loading))
            itemRepository.setReturnError(true)

            assertEquals(viewModel.itemsUiState.value, ItemsUIState.Error)
            assertEquals(viewModel.userMessage.value, ResourcesData.core_data_error_fetching_items)
        }

    @Test
    fun itemsStatesIsSuccessAfterAddNewItem() =
        runTest {
            // Given
            backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.itemsUiState.collect() }
            assertEquals(viewModel.itemsUiState.value, (ItemsUIState.Loading))

            // When - new item added in run time
            itemRepository.addItem(itemRepository.itemsTest[0])
            // Then
            assertEquals(
                (viewModel.itemsUiState.value as ItemsUIState.Success).items,
                itemRepository.itemsTest.associateBy { it.sku },
            )
        }

    @Test
    fun checkNetworkAndAddItem_whenHasItemAndNetworkAvailable_returnsSameItemAndMessageItemAdded() =
        runTest {
            // Given
            backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.itemsUiState.collect() }

            val newItem = Item(
                name = "Iphone 1",
                unitPrice = 10.0,
                quantity = 22,
                sku = "1212312",
                imageUrl = "newItemImage",
            )
            val itemImageBitmap = null

            // When - check to add item
            networkMonitor.setConnected(true)
            viewModel.checkNetworkAndAddItem(newItem, itemImageBitmap)

            // Then
            assertEquals(itemRepository.itemsTest.last(), newItem)
            assertEquals(
                viewModel.userMessage.value,
                (ResourcesData.core_data_item_added_successfully),
            )
        }

    @Test
    fun checkNetworkAndAddItem_whenHasItemAndNetworkUnavailable_returnsFalseAndMessageNetworkUnavailable() =
        runTest {
            // Given new Item and no network available
            backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.itemsUiState.collect() }

            networkMonitor.setConnected(false)
            val newItem = Item(
                name = "Iphone 1",
                unitPrice = 10.0,
                quantity = 22,
                sku = "1212312",
                imageUrl = "newItemImage",
            )
            val bitmap = null
            // When - check to add item
            viewModel.checkNetworkAndAddItem(newItem, bitmap)
            // Then - assert item not add and has message network error .
            assertEquals(
                viewModel.userMessage.value,
                (com.casecode.pos.core.ui.R.string.core_ui_error_network),
            )
        }

    @Test
    fun `checkNetworkAndAddItem when duplicate item and network available then show error duplicate`() =
        runTest {
            backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.itemsUiState.collect() }
            itemRepository.sendItems()

            // Given: A duplicate item with the same SKU
            val duplicateItem = itemRepository.itemsTest[0]
            networkMonitor.setConnected(true)

            // When: Attempting to add the duplicate item and has Items in itemsUiState
            assert(viewModel.itemsUiState.value is ItemsUIState.Success)
            viewModel.checkNetworkAndAddItem(duplicateItem, bitmapMock)

            // Then: Error message should indicate the duplicate issue
            assertEquals(
                viewModel.userMessage.value,
                (R.string.feature_item_error_duplicate),
            )
        }

    @Test
    fun `checkNetworkAndAddItem when duplicate item and items empty and network available then show error duplicate`() =
        runTest {
            backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.itemsUiState.collect() }
            itemRepository.setReturnEmpty(true)

            // Given: A duplicate item with the same SKU
            val duplicateItem = itemRepository.itemsTest[0]
            networkMonitor.setConnected(true)

            // When: Attempting to add the duplicate item and has Items in itemsUiState
            assert(viewModel.itemsUiState.value is ItemsUIState.Empty)
            viewModel.checkNetworkAndAddItem(duplicateItem, bitmapMock)

            // Then: Error message should indicate the duplicate issue
            assertEquals(
                viewModel.userMessage.value,
                ResourcesData.core_data_item_added_successfully,
            )
        }

    @Test
    fun checkNetworkAndAddItem_whenHasItemAndItemImageBitmap_returnMessageItemAdded() =
        runTest {
            backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.itemsUiState.collect() }
            // Given new Item, and bitmap for image item
            val newItem = Item(
                name = "Iphone 1",
                unitPrice = 10.0,
                quantity = 22,
                sku = "1212312",
                imageUrl = "newItemImage",
            )
            networkMonitor.setConnected(true)
            // When - check to add item
            viewModel.checkNetworkAndAddItem(newItem, bitmapMock)
            // then - assert  item added and has message added item and item has url image .
            val success = viewModel.itemsUiState.value as ItemsUIState.Success
            assertEquals(success.items.values.last().imageUrl, ("imageTest.com"))
            assertEquals(
                viewModel.userMessage.value,
                (ResourcesData.core_data_item_added_successfully),
            )
        }

    @Test
    fun checkNetworkAndUpdateItem_whenChangedItemAndNetworkAvailable_returnMessageItemUpdated() =
        runTest {
            // Given change Item
            backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.itemsUiState.collect() }
            val newItem = Item(
                name = "Iphone 1",
                unitPrice = 10.0,
                quantity = 22,
                sku = "1212312",
                imageUrl = "newItemImage",
            )
            viewModel.setItemSelected(itemRepository.itemsTest[0])

            // When
            networkMonitor.setConnected(true)
            viewModel.checkNetworkAndUpdateItem(newItem, null)

            // Then - assert message updated item.
            assertEquals(
                viewModel.userMessage.value,
                (ResourcesData.core_data_item_updated_successfully),
            )
        }

    @Test
    fun checkNetworkAndUpdateItem_whenNotChangeItem_ReturnMessageItemFail() =
        runTest {
            val updateItem = itemRepository.itemsTest[0]
            viewModel.setItemSelected(updateItem)

            // When
            networkMonitor.setConnected(true)
            viewModel.checkNetworkAndUpdateItem(updateItem, null)

            // Then - assert message updated item.
            assertEquals(
                viewModel.userMessage.value,
                (R.string.feature_item_error_update_item_message),
            )
        }

    @Test
    fun checkNetworkAndDeleteItem_whenHasItemAndNetworkAvailable_ReturnsMessageItemDeleted() =
        runTest {
            // Given
            viewModel.setItemSelected(itemRepository.itemsTest[0])

            // When
            networkMonitor.setConnected(true)
            viewModel.checkNetworkAndDeleteItem()

            // Then
            assertEquals(
                viewModel.userMessage.value,
                (ResourcesData.core_data_item_deleted_successfully),
            )
        }

    @Test
    fun checkNetworkAndDeleteItem_whenHasItemDeletedAndNetworkUnavailable_ReturnsMessageNetworkError() =
        runTest {
            // Given
            networkMonitor.setConnected(false)
            viewModel.setItemSelected(itemRepository.itemsTest[0])
            // When -
            viewModel.checkNetworkAndDeleteItem()

            // Then
            assertEquals(
                viewModel.userMessage.value,
                (com.casecode.pos.core.ui.R.string.core_ui_error_network),
            )
        }
}