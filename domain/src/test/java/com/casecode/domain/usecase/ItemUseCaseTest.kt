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

    // Subject under test
    private val testItemRepository = TestItemRepository()
    private val itemUseCase = ItemUseCase(testItemRepository)


    @Test
    fun getItems_whenItemsExist_returnsItems() = runTest {
        // When
        val items = itemUseCase.getItems()

        // Then
        assertThat(items.first(), `is`(ResourceItems.success(testItemRepository.fakeItems)))
    }

    @Test
    fun getItems_whenHasError_returnsError() = runTest {
        // Given
        testItemRepository.setReturnError(true)
        // When
        val items = itemUseCase.getItems()
        // Then
        assertThat(items.first(), `is`(Resource.error(com.casecode.pos.data.R.string
            .error_fetching_items)))
    }

    @Test
    fun getItems_whenHasNoItems_returnsEmpty() = runTest {
        // Given
        testItemRepository.setReturnEmpty(true)

        // When
        val items = itemUseCase.getItems()

        // Then
        assertThat(items.first(), `is`(ResourceItems.empty()))
    }

    @Test
    fun addItem_whenItemAdded_returnsSuccess() = runTest {
        // Given
        val newItem = Item( "New Item", 10.0, 22.2,"1212312","newItemImage")

        // When
        val result = itemUseCase.addItem(newItem)

        // Then
        assertThat(result, `is`(Resource.success(true)))
    }

    @Test
    fun addItem_whenHasError_returnsError() = runTest {
        // Given
        testItemRepository.setReturnError(true)
        val newItem = Item( "New Item", 10.0, 22.2,"1212312","newItemImage")

        // When
        val result = itemUseCase.addItem(newItem)

        // Then
        assertThat(result, `is`(Resource.error(com.casecode.pos.data.R.string
            .add_item_failure_generic)))

    }

    @Test
    fun updateItem_whenItemUpdated_returnsSuccess() = runTest {
        // Given
        val newItem = Item( "New Item", 10.0, 22.2,"1212312","newItemImage")

        // When
        val result = itemUseCase.updateItem(newItem)

        // Then
        assertThat(result, `is`(Resource.success(true)))
    }

    @Test
    fun updateItem_whenHasError_returnsError() = runTest {
        // Given
        testItemRepository.setReturnError(true)
        val newItem = Item( "New Item", 10.0, 22.2,"1212312","newItemImage")

        // When
        val result = itemUseCase.updateItem(newItem)

        // Then
        assertThat(result, `is`(Resource.error(com.casecode.pos.data.R.string
            .update_item_failure_generic)))
    }

    @Test
    fun deleteItem_whenItemDeleted_returnsSuccess() = runTest {
        // Given
        val newItem = Item( "New Item", 10.0, 22.2,"1212312","newItemImage")

        // When
        val result = itemUseCase.deleteItem(newItem)

        // Then
        assertThat(result, `is`(Resource.success(true)))
    }

    @Test
    fun deleteItem_whenHasError_returnsError() = runTest {
        // Given
        testItemRepository.setReturnError(true)
        val newItem = Item( "New Item", 10.0, 22.2,"1212312","newItemImage")

        // When
        val result = itemUseCase.deleteItem(newItem)

        // Then
        assertThat(result, `is`(Resource.error(com.casecode.pos.data.R.string
            .delete_item_failure_generic)))
    }

}