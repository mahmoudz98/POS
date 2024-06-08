package com.casecode.pos.viewmodel

import android.graphics.Bitmap
import com.casecode.domain.model.users.Item
import com.casecode.domain.utils.Resource
import com.casecode.pos.R
import com.casecode.pos.ui.item.ItemsViewModel
import com.casecode.testing.base.BaseTest
import com.casecode.testing.util.MainDispatcherRule
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
import com.casecode.pos.data.R.string as ResourcesData

class ItemsViewModelTest : BaseTest() {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Subject under test
    private lateinit var viewModel: ItemsViewModel

    override fun init() {
        viewModel = ItemsViewModel(
            testNetworkMonitor,
            getItemsUseCase,
            addItemsUseCase,
            updateItemsUseCase,
            deleteItemUseCase,
            imageUseCase,
        )
    }

    @Test
    fun itemsStateIsSuccessAfterLoadingItems() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiItemsState.collect() }
        assertThat(viewModel.uiItemsState.value.resourceItems, `is`(Resource.loading()))
        val itemsFromRepo = testItemRepository.fakeListItems
        testItemRepository.sendItems()
        assertThat(viewModel.uiItemsState.value.resourceItems, `is`(Resource.success(itemsFromRepo)))
        collectJob.cancel()
    }

    @Test
    fun itemsStateIsEmptyAfterLoadingItemsStates() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.uiItemsState.collect { println(it) }
        }
        assertThat(viewModel.uiItemsState.value.resourceItems, equalTo(Resource.loading()))
        testItemRepository.setReturnEmpty(true)

        assertThat(viewModel.uiItemsState.value.resourceItems, equalTo(Resource.empty()))

        collectJob.cancel()
    }

    @Test
    fun itemsStateIsErrorAfterLoadingItemsStates() = runTest {
        // Given
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiItemsState.collect() }

        assertThat(viewModel.uiItemsState.value.resourceItems, equalTo(Resource.loading()))
        testItemRepository.setReturnError(true)

        assertThat(
            viewModel.uiItemsState.value.resourceItems,
            equalTo(Resource.error(ResourcesData.error_fetching_items)),
        )

        collectJob.cancel()
    }

    @Test
    fun itemsStatesIsSuccessAfterAddNewItem() = runTest {
        // Given
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiItemsState.collect() }
        assertThat(viewModel.uiItemsState.value.resourceItems, equalTo(Resource.loading()))

        // When - new item added in run time
        testItemRepository.addItem(testItemRepository.fakeListItems[0])
        // Then
        assertThat(
            viewModel.uiItemsState.value.resourceItems,
            equalTo(Resource.success(testItemRepository.fakeListItems)),
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
            testNetworkMonitor.setConnected(true)
            viewModel.checkNetworkAndAddItem(name, price, quantity, sku, bitmap)
            // When - check to add item

            // Then
            assertThat(
                testItemRepository.fakeListItems.last(),
                equalTo(
                    Item(
                        name, price, quantity,
                        sku, "", null,
                    ),
                ),
            )
            assertThat(
                viewModel.userMessage.value,
                `is`(ResourcesData.item_added_successfully),
            )
        }

    @Test
    fun checkNetworkAndAddItem_whenHasItemAndNetworkUnavailable_returnsFalseAndMessageNetworkUnavailable() =
        runTest {
            // Given new Item and no network available
            testNetworkMonitor.setConnected(false)
            val name = "item4"
            val price = 1000.0
            val quantity = 2.0
            val sku = "123456789123"
            val bitmap = null
            // When - check to add item
            viewModel.checkNetworkAndAddItem(name, price, quantity, sku, bitmap)
            // Then - assert item not add and has message network error .
            assertThat(viewModel.userMessage.value, `is`(R.string.network_error))
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
            testNetworkMonitor.setConnected(true)
            // When - check to add item
            viewModel.checkNetworkAndAddItem(name, price, quantity, sku, bitmap)
            // then - assert  item added and has message added item and item has url image .
            val items= viewModel.uiItemsState.value.resourceItems as Resource.Success
            assertThat( items.data.last().imageUrl , `is`("imageTest.com"))
            assertThat(
                viewModel.userMessage.value,
                `is`(ResourcesData.item_added_successfully),
            )
            collectJob.cancel()
        }

    @Test
    fun checkNetworkAndUpdateItem_whenChangedItemAndNetworkAvailable_returnMessageItemUpdated() =
        runTest {
            // Given change Item
            testNetworkMonitor.setConnected(true)
            val name = "itemUpdate"
            val price = 12.0
            val quantity = 22.0
            val sku = "123456789123"
            val bitmap = bitmapImage
            viewModel.setItemSelected(testItemRepository.fakeListItems[0])
            viewModel.checkNetworkAndUpdateItem(name, price, quantity, sku, bitmap)


            // Then - assert message updated item.
            assertThat(
                viewModel.userMessage.value,
                `is`(ResourcesData.item_updated_successfully),
            )

        }

    @Test
    fun checkNetworkAndUpdateItem_whenNotChangeItem_ReturnMessageItemFail() =
        runTest {
            testNetworkMonitor.setConnected(true)
            val name = testItemRepository.fakeListItems[0].name
            val price = testItemRepository.fakeListItems[0].price
            val quantity = testItemRepository.fakeListItems[0].quantity
            val sku = testItemRepository.fakeListItems[0].sku
            val bitmap = null
            viewModel.setItemSelected(testItemRepository.fakeListItems[0])
            viewModel.checkNetworkAndUpdateItem(name, price, quantity, sku, bitmap)


            // Then - assert message updated item.
            assertThat(
                viewModel.userMessage.value,
                `is`(R.string.update_item_fail),
            )

        }

    @Test
    fun checkNetworkAndDeleteItem_whenHasItemAndNetworkAvailable_ReturnsMessageItemDeleted() =
        runTest {
            // Given
            testNetworkMonitor.setConnected(true)

            // When -
            viewModel.checkNetworkAndDeleteItem(testItemRepository.fakeListItems[0])

            // Then
             assertThat(viewModel.userMessage.value, `is`(ResourcesData.item_deleted_successfully))
        }

    @Test
    fun checkNetworkAndDeleteItem_whenHasItemDeletedAndNetworkUnavailable_ReturnsMessageNetworkError() =
        runTest {
            // Given
            testNetworkMonitor.setConnected(false)
            // When -
            viewModel.checkNetworkAndDeleteItem(testItemRepository.fakeListItems[0])

            // Then
            assertThat(
                 viewModel.userMessage.value,
                 `is`(R.string.network_error),)
        }

    private val bitmapImage = mockk<Bitmap>()

}