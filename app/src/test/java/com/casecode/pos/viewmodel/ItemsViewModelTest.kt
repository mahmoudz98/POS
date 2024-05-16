package com.casecode.pos.viewmodel

import android.graphics.Bitmap
import com.casecode.domain.model.users.Item
import com.casecode.pos.R
import com.casecode.testing.base.BaseTest
import com.casecode.testing.util.MainDispatcherRule
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test

class ItemsViewModelTest : BaseTest() {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Subject under test
    private lateinit var viewModel: ItemsViewModel

    override fun init() {
        viewModel = ItemsViewModel(testNetworkMonitor, getItemsUseCase,addItemsUseCase,
            updateItemsUseCase,deleteItemUseCase,imageUseCase)
    }

    @Test
    fun `fetchItems when has items should return items and update state empty and loading`() =
        runTest {
            // Given -
            viewModel.fetchItems()
            val expected = testItemRepository.fakeItems
            // When items are fetched
            assertThat(viewModel.items.value, `is`(expected))
            assertThat(viewModel.isEmptyItems.value, `is`(false))
            assertThat(viewModel.isLoading.value, `is`(false))
        }

    @Test
    fun `fetchItems when has not items should return empty items and update state empty and loading`() =
        runTest {
            // Given -
            testItemRepository.setReturnEmpty(true)
            // When - repo are empty
            viewModel.fetchItems()

            // Then -  items are fetched and empty
            assertThat(viewModel.items.value, equalTo(null))
            assertThat(viewModel.isEmptyItems.value, `is`(true))
            assertThat(viewModel.isLoading.value, `is`(false))
        }

    @Test
    fun `fetchItems when has error should return empty items and update state empty and loading`() =
        runTest {
            // Given
            testItemRepository.setReturnError(true)

            // When
            viewModel.fetchItems()
            // Then -  items are fetched and empty
            assertThat(viewModel.items.value, equalTo(null))
            assertThat(viewModel.isLoading.value, `is`(false))
            assertThat(viewModel.isEmptyItems.value, `is`(true))
        }

    @Test
    fun `fetchItems when has new items should return items and update state empty and loading`() =
        runTest {
            // Given
            viewModel.fetchItems()

            // When - new item added in run time
            testItemRepository.addItem(testItemRepository.fakeItems[0])
            // then
            assertThat(viewModel.items.value, `is`(testItemRepository.fakeItems))
        }

    @Test
    fun checkNetworkAndAddItem_whenHasItemAndNetworkAvailable_returnsTrueAndMessageItemAdded() =
        runTest {
            // Given new Item
            testNetworkMonitor.setConnected(true)

            viewModel.setItemSelected(fakeNewItem)
            // When - check to add item
            viewModel.checkNetworkAndAddItem()
            // then - assert  item added and has message added item.
            assertThat(viewModel.isAddItem.value?.peekContent(), `is`(true))
            assertThat(
                viewModel.userMessage.value?.peekContent(),
                `is`(com.casecode.pos.data.R.string.item_added_successfully),
            )
        }

    @Test
    fun checkNetworkAndAddItem_whenHasItemAndNetworkUnavailable_returnsFalseAndMessageNetworkUnavailable() =
        runTest {
            // Given new Item and no network available
            testNetworkMonitor.setConnected(false)

            viewModel.setItemSelected(fakeNewItem)
            // When - check to add item
            viewModel.checkNetworkAndAddItem()
            // then - assert item not add and has message network error .
            assertThat(viewModel.isAddItem.value?.peekContent(), `is`(false))
            assertThat(
                viewModel.userMessage.value?.peekContent(),
                `is`(R.string.network_error),
            )
        }

    @Test
    fun checkNetworkAndAddItem_whenHasItemAndItemImageBitmap_returnsTrueAndMessageItemAdded() =
        runTest {
            // Given new Item, and bitmap for image item
            testNetworkMonitor.setConnected(true)
            viewModel.setItemSelected(fakeNewItem)
            viewModel.setBitmap(bitmapImage)
            // When - check to add item
            viewModel.checkNetworkAndAddItem()
            // then - assert  item added and has message added item and item has url image .
            assertThat(viewModel.itemSelected.value?.imageUrl, `is`("imageTest.com"))
            assertThat(viewModel.isAddItem.value?.peekContent(), `is`(true))
            assertThat(
                viewModel.userMessage.value?.peekContent(),
                `is`(com.casecode.pos.data.R.string.item_added_successfully),
            )
        }

    @Test
    fun checkNetworkAndUpdateItem_whenChangedItemAndNetworkAvailable_returnsTrueAndMessageItemUpdated() =
        runTest {
            // Given change Item
            viewModel.fetchItems()
            testNetworkMonitor.setConnected(true)

            viewModel.setItemSelected(fakeNewItem)
            val updateItem = fakeNewItem.copy(name = "updateItem")
            viewModel.setItemUpdated(updateItem)
            // When - check to update item
            viewModel.checkNetworkAndUpdateItem()

            // Then - assert  item update and has message updated item.
            assertThat(viewModel.isAddItem.value?.peekContent(), `is`(true))
            assertThat(
                viewModel.userMessage.value?.peekContent(),
                `is`(com.casecode.pos.data.R.string.item_updated_successfully),
            )
        }

    @Test
    fun checkNetworkAndUpdateItem_whenNotChangItemAndNetworkAvailable_ReturnsFalseAndMessageItemUpdated() =
        runTest {
            // Given change Item
            viewModel.fetchItems()
            testNetworkMonitor.setConnected(true)

            viewModel.setItemSelected(fakeNewItem)
            val updateItem = fakeNewItem.copy()
            viewModel.setItemUpdated(updateItem)
            // When - check to update item
            viewModel.checkNetworkAndUpdateItem()

            // Then - assert  item update and has message failed update item.
            assertThat(
                viewModel.userMessage.value?.peekContent(),
                `is`(R.string.update_item_fail),
            )
        }

    @Test
    fun checkNetworkAndDeleteItem_whenHasItemAndNetworkAvailable_ReturnsMessageItemDeleted() =
        runTest {
            // Given
            viewModel.fetchItems()
            testNetworkMonitor.setConnected(true)

            // When -
            viewModel.checkNetworkAndDeleteItem(testItemRepository.fakeItems[0])

            // Then
            assertThat(
                viewModel.userMessage.value?.peekContent(),
                `is`(com.casecode.pos.data.R.string.item_deleted_successfully),
            )
        }

    @Test
    fun checkNetworkAndDeleteItem_whenHasItemDeletedAndNetworkUnavailable_ReturnsMessageNetworkError() =
        runTest {
            // Given
            viewModel.fetchItems()
            testNetworkMonitor.setConnected(false)

            // When -
            viewModel.checkNetworkAndDeleteItem(testItemRepository.fakeItems[0])

            // Then
            assertThat(
                viewModel.userMessage.value?.peekContent(),
                `is`(R.string.network_error),
            )
        }

    private val bitmapImage = mockk<Bitmap>()

    private val fakeNewItem = Item(
        name = "IPhone", price = 1000.0, imageUrl = null,
        quantity = 2.0,
        unitOfMeasurement = null, sku = "123456789123",
    )
}