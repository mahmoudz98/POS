package com.casecode.domain.usecase

import com.casecode.domain.model.users.Item
import com.casecode.domain.repository.ResourceItems
import com.casecode.domain.utils.Resource
import com.casecode.testing.repository.TestItemRepository
import com.casecode.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test


class ItemUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Subjects under test
    private val testItemRepository = TestItemRepository()
    private val itemUseCase = GetItemsUseCase(testItemRepository)
    private val addItemUseCase = AddItemUseCase(testItemRepository)
    private val updateItemUseCase = UpdateItemUseCase(testItemRepository)
    private val deleteItemUseCase = DeleteItemUseCase(testItemRepository)
    private val updateStockInItemsUseCase = UpdateStockInItemsUseCase(testItemRepository)


    @Test
    fun getItems_whenItemsExist_returnsItems() = runTest {
        // When
        val items = itemUseCase()

        // Then
        assertThat(items.first(), `is`(ResourceItems.success(testItemRepository.fakeItems)))
    }

    @Test
    fun getItems_whenHasError_returnsError() = runTest {
        // Given
        testItemRepository.setReturnError(true)
        // When
        val items = itemUseCase()
        // Then
        assertThat(
            items.first(),
            `is`(
                Resource.error(
                    com.casecode.pos.data.R.string.error_fetching_items,
                ),
            ),
        )
    }

    @Test
    fun getItems_whenHasNoItems_returnsEmpty() = runTest {
        // Given
        testItemRepository.setReturnEmpty(true)

        // When
        val items = itemUseCase()

        // Then
        assertThat(items.first(), `is`(ResourceItems.empty()))
    }

    @Test
    fun addItem_whenItemAdded_returnsSuccess() = runTest {
        // Given
        val newItem = Item("New Item", 10.0, 22.2, "1212312", "newItemImage")

        // When
        val result = addItemUseCase(newItem)

        // Then
        assertThat(
            result,
            `is`(Resource.success(com.casecode.pos.data.R.string.item_added_successfully)),
        )
    }

    @Test
    fun addItem_whenHasError_returnsError() = runTest {
        // Given
        testItemRepository.setReturnError(true)
        val newItem = Item("New Item", 10.0, 22.2, "1212312", "newItemImage")

        // When
        val result = addItemUseCase(newItem)

        // Then
        assertThat(
            result,
            `is`(
                Resource.error(
                    com.casecode.pos.data.R.string.add_item_failure_generic,
                ),
            ),
        )

    }

    @Test
    fun updateItem_whenItemUpdated_returnsSuccess() = runTest {
        // Given
        val newItem = Item("New Item", 10.0, 22.2, "1212312", "newItemImage")

        // When
        val result = updateItemUseCase(newItem)

        // Then
        assertThat(
            result,
            `is`(Resource.success(com.casecode.pos.data.R.string.item_updated_successfully)),
        )
    }

    @Test
    fun updateStockInItemsUseCase_whenItemUpdated_returnsSuccess() = runTest {
        // Given
        val itemsUpdate = listOf(Item("New Item", 10.0, 22.2, "1212312", "newItemImage"))

        // When
        val result = updateStockInItemsUseCase(itemsUpdate)

        // Then
        assertThat(
            result,
            `is`(Resource.success(itemsUpdate)),
        )
    }

    @Test
    fun updateStockInItemsUseCase_whenEmptyItems_returnsMessageEmpty() = runTest {
        // Given
        val emptyItems = listOf<Item>()

        // When
        val result = updateStockInItemsUseCase(emptyItems)

        // Then
        assertThat(
            result,
            `is`(
                Resource.empty(
                    message = com.casecode.pos.domain.R.string.invoice_items_empty,
                ),
            ),
        )
    }

    @Test
    fun updateStockInItemsUseCase_whenHasError_returnMessageError() = runTest{
        val itemsUpdate = listOf(Item("New Item", 10.0, 22.2, "1212312", "newItemImage"))

        // When
        testItemRepository setReturnError true
        val resultUpdate = updateStockInItemsUseCase(itemsUpdate)
        assertThat(resultUpdate, `is`(Resource.error(com.casecode.pos.data.R.string
            .update_item_failure_generic)))
    }

    @Test
    fun updateItem_whenHasError_returnsError() = runTest {
        // Given
        testItemRepository.setReturnError(true)
        val newItem = Item("New Item", 10.0, 22.2, "1212312", "newItemImage")

        // When
        val result = updateItemUseCase(newItem)

        // Then
        assertThat(result, `is`(Resource.error(com.casecode.pos.data.R.string.update_item_failure_generic)))
    }

    @Test
    fun deleteItem_whenItemDeleted_returnsSuccess() = runTest {
        // Given
        val newItem = Item("New Item", 10.0, 22.2, "1212312", "newItemImage")

        // When
        val result = deleteItemUseCase(newItem)

        // Then
        assertThat(
            result,
            `is`(Resource.success(com.casecode.pos.data.R.string.item_deleted_successfully)),
        )
    }

    @Test
    fun deleteItem_whenHasError_returnsError() = runTest {
        // Given
        testItemRepository.setReturnError(true)
        val newItem = Item("New Item", 10.0, 22.2, "1212312", "newItemImage")

        // When
        val result = deleteItemUseCase(newItem)

        // Then
        assertThat(
            result,
            `is`(
                Resource.error(
                    com.casecode.pos.data.R.string.delete_item_failure_generic,
                ),
            ),
        )
    }

}