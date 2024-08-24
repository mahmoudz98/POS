package com.casecode.pos.feature.item

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Item
import com.casecode.pos.core.testing.base.BaseTest
import com.casecode.pos.core.testing.util.MainDispatcherRule
import io.mockk.mockk
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import com.casecode.pos.core.data.R.string as ResourcesData

class ItemsViewModelTest : BaseTest() {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Subject under test
    private lateinit var viewModel: ItemsViewModel

    override fun init() {
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
            val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiItemsState.collect() }
            assertThat(viewModel.uiItemsState.value.resourceItems, `is`(Resource.loading()))
            val itemsFromRepo = itemRepository.fakeListItems
            itemRepository.sendItems()
            assertThat(
                viewModel.uiItemsState.value.resourceItems,
                `is`(Resource.success(itemsFromRepo)),
            )
            collectJob.cancel()
        }

    @Test
    fun itemsStateIsEmptyAfterLoadingItemsStates() =
        runTest {
            val collectJob =
                launch(UnconfinedTestDispatcher()) {
                    viewModel.uiItemsState.collect { println(it) }
                }
            assertThat(viewModel.uiItemsState.value.resourceItems, equalTo(Resource.loading()))
            itemRepository.setReturnEmpty(true)

            assertThat(viewModel.uiItemsState.value.resourceItems, equalTo(Resource.empty()))

            collectJob.cancel()
        }

    @Test
    fun itemsStateIsErrorAfterLoadingItemsStates() =
        runTest {
            // Given
            val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiItemsState.collect() }

            assertThat(viewModel.uiItemsState.value.resourceItems, equalTo(Resource.loading()))
            itemRepository.setReturnError(true)

            assertThat(
                viewModel.uiItemsState.value.resourceItems,
                equalTo(Resource.error(ResourcesData.core_data_error_fetching_items)),
            )

            collectJob.cancel()
        }

    @Test
    fun itemsStatesIsSuccessAfterAddNewItem() =
        runTest {
            // Given
            val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiItemsState.collect() }
            assertThat(viewModel.uiItemsState.value.resourceItems, equalTo(Resource.loading()))

            // When - new item added in run time
            itemRepository.addItem(itemRepository.fakeListItems[0])
            // Then
            assertThat(
                viewModel.uiItemsState.value.resourceItems,
                equalTo(Resource.success(itemRepository.fakeListItems)),
            )
            collectJob.cancel()
        }

    @Test
    fun checkNetworkAndAddItem_whenHasItemAndNetworkAvailable_returnsSameItemAndMessageItemAdded() =
        runTest {
            // Given new Item
            val name = "item4"
            val price = 1000.0
            val quantity = 2.0
            val sku = "123456789123"
            val bitmap = null
            networkMonitor.setConnected(true)
            viewModel.checkNetworkAndAddItem(name, price, quantity, sku, bitmap)
            // When - check to add item

            // Then
            assertThat(
                itemRepository.fakeListItems.last(),
                equalTo(
                    Item(
                        name,
                        price,
                        quantity,
                        sku,
                        "",
                        null,
                    ),
                ),
            )
            assertThat(
                viewModel.userMessage.value,
                `is`(ResourcesData.core_data_item_added_successfully),
            )
        }

    @Test
    fun checkNetworkAndAddItem_whenHasItemAndNetworkUnavailable_returnsFalseAndMessageNetworkUnavailable() =
        runTest {
            // Given new Item and no network available
            networkMonitor.setConnected(false)
            val name = "item4"
            val price = 1000.0
            val quantity = 2.0
            val sku = "123456789123"
            val bitmap = null
            // When - check to add item
            viewModel.checkNetworkAndAddItem(name, price, quantity, sku, bitmap)
            // Then - assert item not add and has message network error .
            assertThat(viewModel.userMessage.value, `is`(com.casecode.pos.core.ui.R.string.core_ui_error_network))
        }

    @Test
    fun checkNetworkAndAddItem_whenHasItemAndItemImageBitmap_returnMessageItemAdded() =
        runTest {
            val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiItemsState.collect() }

            // Given new Item, and bitmap for image item
            val name = "item4"
            val price = 1000.0
            val quantity = 2.0
            val sku = "123456789123"
            val bitmap = bitmapImage
            networkMonitor.setConnected(true)
            // When - check to add item
            viewModel.checkNetworkAndAddItem(name, price, quantity, sku, bitmap)
            // then - assert  item added and has message added item and item has url image .
            val items = viewModel.uiItemsState.value.resourceItems as Resource.Success
            assertThat(items.data.last().imageUrl, `is`("imageTest.com"))
            assertThat(
                viewModel.userMessage.value,
                `is`(ResourcesData.core_data_item_added_successfully),
            )
            collectJob.cancel()
        }

    @Test
    fun checkNetworkAndUpdateItem_whenChangedItemAndNetworkAvailable_returnMessageItemUpdated() =
        runTest {
            // Given change Item
            networkMonitor.setConnected(true)
            val name = "itemUpdate"
            val price = 12.0
            val quantity = 22.0
            val sku = "123456789123"
            val bitmap = bitmapImage
            viewModel.setItemSelected(itemRepository.fakeListItems[0])
            viewModel.checkNetworkAndUpdateItem(name, price, quantity, sku, bitmap)

            // Then - assert message updated item.
            assertThat(
                viewModel.userMessage.value,
                `is`(ResourcesData.core_data_item_updated_successfully),
            )
        }

    @Test
    fun checkNetworkAndUpdateItem_whenNotChangeItem_ReturnMessageItemFail() =
        runTest {
            networkMonitor.setConnected(true)
            val name = itemRepository.fakeListItems[0].name
            val price = itemRepository.fakeListItems[0].price
            val quantity = itemRepository.fakeListItems[0].quantity
            val sku = itemRepository.fakeListItems[0].sku
            val bitmap = null
            viewModel.setItemSelected(itemRepository.fakeListItems[0])
            viewModel.checkNetworkAndUpdateItem(name, price, quantity, sku, bitmap)

            // Then - assert message updated item.
            assertThat(
                viewModel.userMessage.value,
                `is`(R.string.feature_item_error_update_item_message),
            )
        }

    @Test
    fun checkNetworkAndDeleteItem_whenHasItemAndNetworkAvailable_ReturnsMessageItemDeleted() =
        runTest {
            // Given
            networkMonitor.setConnected(true)
            viewModel.setItemSelected(itemRepository.fakeListItems[0])

            // When -
            viewModel.checkNetworkAndDeleteItem()

            // Then
            assertThat(
                viewModel.userMessage.value,
                `is`(ResourcesData.core_data_item_deleted_successfully),
            )
        }

    @Test
    fun checkNetworkAndDeleteItem_whenHasItemDeletedAndNetworkUnavailable_ReturnsMessageNetworkError() =
        runTest {
            // Given
            networkMonitor.setConnected(false)
            viewModel.setItemSelected(itemRepository.fakeListItems[0])
            // When -
            viewModel.checkNetworkAndDeleteItem()

            // Then
            assertThat(
                viewModel.userMessage.value,
                `is`(com.casecode.pos.core.ui.R.string.core_ui_error_network),
            )
        }

    private val bitmapImage = mockk<Bitmap>()
}