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
package com.casecode.pos.feature.sale

import android.icu.text.Collator
import androidx.lifecycle.SavedStateHandle
import com.casecode.pos.core.domain.usecase.AddInvoiceUseCase
import com.casecode.pos.core.domain.usecase.GetItemsUseCase
import com.casecode.pos.core.domain.usecase.UpdateStockInItemsUseCase
import com.casecode.pos.core.model.data.users.Item
import com.casecode.pos.core.testing.repository.TestInvoiceRepository
import com.casecode.pos.core.testing.repository.TestItemRepository
import com.casecode.pos.core.testing.util.MainDispatcherRule
import com.casecode.pos.core.testing.util.TestNetworkMonitor
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SaleViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var viewModel: SaleViewModel
    private val networkMonitor = TestNetworkMonitor()
    private val itemRepository = TestItemRepository()
    private val invoicesRepository = TestInvoiceRepository()
    private val getItems = GetItemsUseCase(itemRepository)
    private val updateStockInItem = UpdateStockInItemsUseCase(itemRepository)
    private val addInvoice = AddInvoiceUseCase(invoicesRepository)

    @Before
    fun init() {
        viewModel =
            SaleViewModel(
                networkMonitor,
                getItems,
                addInvoice,
                updateStockInItem,
                SavedStateHandle(),
            )
    }

    @Test
    fun fetchItems_whenHasItems_returnListOfItems() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.itemsUiState.collect() }
        itemRepository.sendItems()
        val items = viewModel.items.value as MutableMap<*, *>
        // Then
        val itemsMaps = itemRepository.itemsTest.associateBy { it.sku }.toMutableMap()
        assertEquals(items, itemsMaps)
    }

    @Test
    fun `addItemInvoice should add item to invoice and update stock correctly`() = runTest {
        // Given - Initialize state and send initial items to repository
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.itemsUiState.collect() }
        itemRepository.sendItems()
        // Fetch the first item and calculate expected values
        val itemToAdd = viewModel.items.value.values.first()
        val expectedNewStock = itemToAdd.quantity - 1
        val expectedSku = itemToAdd.sku
        // When - Add item to the invoice
        viewModel.addItemInvoice(itemToAdd)
        // Then - Verify item is added to the invoice with correct quantity
        val addedInvoiceItem = viewModel.saleItemsState.firstOrNull {
            it.sku == expectedSku
        }
        assertNotNull(addedInvoiceItem, "Item was not added to the invoice")
        assertEquals(1, addedInvoiceItem.quantity, "Invoice item quantity mismatch")
        // Verify stock is updated for the added item
        val updatedItemStock =
            viewModel.items.value.values.firstOrNull { it.sku == expectedSku }?.quantity
        assertEquals(expectedNewStock, updatedItemStock, "Stock quantity was not updated correctly")
    }

    @Test
    fun addItemInvoice_whenItemOutOfStock_returnMessageItemOfStock() = runTest {
        // Given
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.items.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.userMessage.collect() }
        val itemsTest = arrayListOf(Item(quantity = 0, reorderLevel = 0))
        itemRepository.sendItems(itemsTest)
        // When - added item and out of stock
        val actual = itemsTest[0]
        viewModel.addItemInvoice(actual)
        // Then
        assertEquals(
            viewModel.userMessage.value,
            R.string.feature_sale_item_out_of_stock_message,
        )
    }

    @Test
    fun scanItem_whenSKUFound_returnItem() = runTest {
        // Given
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.itemsUiState.collect() }
        itemRepository.sendItems()
        // When
        viewModel.scanItem(itemRepository.itemsTest[0].sku)
        val expectedItem =
            viewModel.saleItemsState.elementAt(0)
        val actualItem = itemRepository.itemsTest[0]
        // Then - check item added to invoice and check quantity is start by one
        assertEquals(expectedItem.sku, actualItem.sku)
        assertEquals(expectedItem.quantity, 1)
        assertEquals(expectedItem.name, actualItem.name)
        assertEquals(expectedItem.unitPrice, actualItem.unitPrice)
        assertEquals(expectedItem.unitOfMeasurement, actualItem.unitOfMeasurement)
        assertEquals(expectedItem.imageUrl, actualItem.imageUrl)
    }

    @Test
    fun scanItem_whenSKUNotFound_returnMessageItemNotFound() {
        // When
        viewModel.scanItem("not found")
        // Then
        assertEquals(
            viewModel.userMessage.value,
            R.string.feature_sale_error_sale_item_not_found,
        )
    }

    @Test
    fun deleteItemInvoice_shouldResetQuantityItemInStock() = runTest {
        // Given - fetch items and  add item in invoice
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.itemsUiState.collect() }
        itemRepository.sendItems()
        val item = viewModel.items.value.values.elementAt(0)
        viewModel.addItemInvoice(item)
        // When delete item
        viewModel.deleteItemInvoice(viewModel.saleItemsState.elementAt(0))
        // Then - check quantity item in stock
        val actual = viewModel.items.value.values.find { it.sku == item.sku }
        assertEquals(
            actual?.quantity,
            itemRepository.itemsTest[0].quantity,
        )
    }

    @Test
    fun updateQuantityItem_whenInStock_thenUpdateStockAndUpdateSaleInItem() = runTest {
        // Given - Add Items to Invoice
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.itemsUiState.collect() }
        itemRepository.sendItems()
        viewModel.addItemInvoice(viewModel.items.value.values.elementAt(0))

        // When - Update the quantity
        viewModel.itemInvoiceSelected(viewModel.saleItemsState.elementAt(0))
        viewModel.updateQuantityItemInvoice(22)

        // Then update item in stock and in items invoice
        assertEquals(viewModel.items.value.values.elementAt(0).quantity, 1)
        assertEquals(viewModel.saleItemsState.elementAt(0).quantity, 22)
    }

    @Test
    fun updateQuantityItem_whenTakeWholeStock_thenUpdateStockAndUpdateSaleInItem() = runTest {
        // Given - Add Items to Invoice
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.itemsUiState.collect() }
        itemRepository.sendItems()
        viewModel.addItemInvoice(viewModel.items.value.values.elementAt(0))

        // When - Update the quantity
        viewModel.itemInvoiceSelected(viewModel.saleItemsState.elementAt(0))
        viewModel.updateQuantityItemInvoice(23)

        // Then update item in stock and in items invoice
        assertEquals(viewModel.items.value.values.elementAt(0).quantity, 0)
        assertEquals(viewModel.saleItemsState.elementAt(0).quantity, 23)
    }

    @Test
    fun updateQuantityItem_whenItemsSelectedIsNull_ShouldShowMessageUpdateFail() {
        // Given - Add Items to Invoice
        itemRepository.sendItems()
        viewModel.addItemInvoice(itemRepository.itemsTest[0])
        // When - Update the quantity
        viewModel.updateQuantityItemInvoice(23)
        // Then - show message fail to update quantity item
        assertEquals(
            viewModel.userMessage.value,
            R.string.feature_sale_error_update_invoice_item_quantity,
        )
    }

    @Test
    fun updateQuantityItem_whenQuantityNotChange_ShouldShowMessageUpdateFail() {
        // Given - Add Items to Invoice
        itemRepository.sendItems()
        viewModel.addItemInvoice(itemRepository.itemsTest[0])
        // When - Update the quantity and not change
        viewModel.itemInvoiceSelected(itemRepository.itemsTest[0].copy(quantity = 1))
        viewModel.updateQuantityItemInvoice(1)
        // Then
        assertEquals(
            viewModel.userMessage.value,
            R.string.feature_sale_error_update_invoice_item_quantity,
        )
    }

    @Test
    fun updateQuantityItem_whenItemsInvoiceIsEmpty_shouldShowMessageUpdateFail() = runTest {
        // Given - Add Items to Invoice
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.items.collect() }
        itemRepository.sendItems()
        viewModel.itemInvoiceSelected(itemRepository.itemsTest[0])
        // When - Update the quantity
        viewModel.updateQuantityItemInvoice(23)
        // Then
        assertEquals(
            viewModel.userMessage.value,
            R.string.feature_sale_error_update_invoice_item_quantity,
        )
    }

    @Test
    fun checkFilterItemsWithArabicNumber() {
        val searchQuery = "١٢٣"
        val collator = java.text.Collator.getInstance(Locale("ar")) // Use the default locale
        collator.strength = Collator.PRIMARY // Case-insensitive and ignores accents
        val normalizedQuery = normalizeNumber(searchQuery)
        val items =
            listOf(
                Item(
                    name = "Iphone1",
                    unitPrice = 10.0,
                    quantity = 5,
                    sku = "123",
                    unitOfMeasurement = null,
                    imageUrl = null,
                ),
                Item(
                    name = "Iphone6",
                    unitPrice = 10.0,
                    quantity = 5,
                    sku = "1234",
                    unitOfMeasurement = null,
                    imageUrl = null,
                ),
            )
        val filter =
            items.filter {
                val normalizedSku = normalizeNumber(it.sku)
                println("normalizedSku: $normalizedSku")
                println("normilzeQue: $normalizedQuery")
                collator.compare(normalizedQuery, normalizedSku) == 0 ||
                    normalizedSku.contains(normalizedQuery)
            }
        print("filter: $filter")
        assertTrue(filter.isNotEmpty())
    }

    private fun normalizeNumber(input: String): String {
        val arabicNumerals = listOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        val englishNumerals = listOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
        val sb = StringBuilder()
        for (char in input) {
            when (char) {
                in englishNumerals -> sb.append(char)
                in arabicNumerals -> {
                    val index = arabicNumerals.indexOf(char)
                    sb.append(englishNumerals[index])
                }

                else -> sb.append(char)
            }
        }
        return sb.toString()
    }
}