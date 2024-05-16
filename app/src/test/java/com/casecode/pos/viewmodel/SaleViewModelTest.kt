package com.casecode.pos.viewmodel

import com.casecode.pos.R
import com.casecode.testing.base.BaseTest
import com.casecode.testing.util.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test


class SaleViewModelTest : BaseTest() {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Subject under test
    private lateinit var viewModel: SaleViewModel


    override fun init() {
        viewModel = SaleViewModel(
            testNetworkMonitor,
            getItemsUseCase,
            addInvoiceUseCase,
            updateStockInItemsUseCase)
    }

    @Test
    fun fetchItems_whenHasItems_returnListOfItems() {
        // When
        val items = viewModel.items.value
        // Then
        assertThat(items, `is`(testItemRepository.fakeItems))
    }

    @Test
    fun fetchItems_whenHasItemsAndThenError_thenReturnMessageError() = runTest {
        // When
        testItemRepository setReturnError true
        viewModel.fetchItems()
        // Then
        assertThat(
            viewModel.userMessage.value?.peekContent(),
            `is`(com.casecode.pos.data.R.string.error_fetching_items),
        )
    }

    @Test
    fun scanItem_whenSKUFound_returnItem() {

        // When
        viewModel.scanItem(testItemRepository.fakeItems[0].sku)
        val expectedItem = viewModel.itemsInvoice.value?.elementAt(0)!!
        val actualItem = testItemRepository.fakeItems[0]
        // Then - check item added to invoice and check quantity is start by one
        assertThat(expectedItem.sku, `is`(actualItem.sku))
        assertThat(expectedItem.quantity, `is`(1.0))
        assertThat(expectedItem.name, `is`(actualItem.name))
        assertThat(expectedItem.price, `is`(actualItem.price))
        assertThat(expectedItem.unitOfMeasurement, `is`(actualItem.unitOfMeasurement))
        assertThat(expectedItem.imageUrl, `is`(actualItem.imageUrl))
    }

    @Test
    fun scanItem_whenSKUNotFound_returnMessageItemNotFound() {
        // When
        viewModel.scanItem("not found")
        // Then
        assertThat(
            viewModel.userMessage.value?.peekContent(),
            `is`(R.string.search_qr_cant_find_item),
        )
    }

    @Test
    fun addItemInvoice_ShouldAddItemInInvoiceAndUpdateStock() {
        // When - added item
        val actual = viewModel.items.value?.get(0)!!
        val newStock = actual.quantity.minus(1.0)
        viewModel.addItemInvoice(actual)
        val expected = viewModel.itemsInvoice.value?.elementAt(0)!!
        // Then -
        assertThat(actual.sku, `is`(expected.sku))
        assertThat(actual.name, `is`(expected.name))
        assertThat(actual.price, `is`(expected.price))
        assertThat(actual.imageUrl, `is`(expected.imageUrl))
        assertThat(actual.quantity, `is`(newStock))
    }

    @Test
    fun addItemInvoice_whenItemOutOfStock_returnMessageItemOfStock() {
        // When - added item and out of stock
        val actual = testItemRepository.fakeItems[2]
        viewModel.addItemInvoice(actual)
        // Then
        assertThat(
            viewModel.userMessage.value?.peekContent(),
            `is`(R.string.sale_item_out_of_stock),
        )
    }

    @Test
    fun deleteItemInvoice_shouldResetQuantityItemInStock() = runTest {
        // Given - add item and then selected item
        viewModel.addItemInvoice(viewModel.items.value?.get(0)!!)
        viewModel.itemInvoiceSelected(testItemRepository.fakeItems[0].copy(quantity = 1.0))
        // When delete item
        viewModel.deleteItemInvoice(viewModel.items.value?.get(0)!!)
        // Then - check quantity item in stock
        assertThat(
            viewModel.items.value?.get(0)!!.quantity,
            `is`(testItemRepository.fakeItems[0].quantity),
        )
    }


    @Test
    fun updateQuantityItem_shouldUpdateQuantityItemInStockAndInItemInvoice() = runTest {
        // Given - Add Items to Invoice
        viewModel.addItemInvoice(testItemRepository.fakeItems[0])
        // When - Update the quantity
        viewModel.itemInvoiceSelected(testItemRepository.fakeItems[0].copy(quantity = 1.0))
        viewModel.updateQuantityItemInvoice(23.0)
        // Then update item in stock and in items invoice
        assertThat(viewModel.items.value?.get(0)?.quantity, `is`(0.0))
        assertThat(viewModel.itemsInvoice.value?.elementAt(0)?.quantity, `is`(23.0))
    }

    @Test
    fun updateQuantityItem_whenItemsSelectedIsNull_ShouldShowMessageUpdateFail() {
        // Given - Add Items to Invoice
        viewModel.addItemInvoice(testItemRepository.fakeItems[0])
        // When - Update the quantity
        viewModel.updateQuantityItemInvoice(23.0)
        // Then - show message fail to update quantity item
        assertThat(
            viewModel.userMessage.value?.peekContent(),
            `is`(R.string.quantity_item_invoice_update_fail),
        )
    }

    @Test
    fun updateQuantityItem_whenQuantityNotChange_ShouldShowMessageUpdateFail() {
        // Given - Add Items to Invoice
        viewModel.addItemInvoice(testItemRepository.fakeItems[0])
        // When - Update the quantity and not change
        viewModel.itemInvoiceSelected(testItemRepository.fakeItems[0].copy(quantity = 1.0))
        viewModel.updateQuantityItemInvoice(1.0)
        // Then
        assertThat(
            viewModel.userMessage.value?.peekContent(),
            `is`(R.string.quantity_item_invoice_update_fail),
        )
    }

    @Test
    fun updateQuantityItem_whenItemsInvoiceIsEmpty_shouldShowMessageUpdateFail() {
        // Given - Add Items to Invoice
        viewModel.itemInvoiceSelected(testItemRepository.fakeItems[0])
        // When - Update the quantity
        viewModel.updateQuantityItemInvoice(23.0)
        // Then
        assertThat(
            viewModel.userMessage.value?.peekContent(),
            `is`(R.string.quantity_item_invoice_update_fail),
        )
    }

}