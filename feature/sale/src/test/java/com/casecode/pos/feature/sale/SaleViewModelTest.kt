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
import com.casecode.pos.core.model.data.users.Item
import com.casecode.pos.core.testing.base.BaseTest
import com.casecode.pos.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import java.util.Locale
import kotlin.test.assertTrue

class SaleViewModelTest : BaseTest() {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Subject under test
    private lateinit var viewModel: SaleViewModel

    override fun init() {
        viewModel =
            SaleViewModel(
                networkMonitor,
                getItems,
                addInvoice,
                updateStockInItem,
            )
    }

    @Test
    fun fetchItems_whenHasItems_returnListOfItems() =
        runTest {
            // When
            itemRepository.sendItems()
            val items = viewModel.uiState.value.items
            // Then
            assertThat(items, `is`(itemRepository.itemsTest))
        }

    @kotlin.test.Test
    fun addItemInvoice_ShouldAddItemInInvoiceAndUpdateStock() =
        runTest {
            // Given
            itemRepository.sendItems()

            // When - added item
            val actual = viewModel.uiState.value.items[0]
            val newStock = actual.quantity.minus(1.0)
            viewModel.addItemInvoice(actual)
            val expected =
                viewModel.uiState.value.itemsInvoice
                    .elementAt(0)
            // Then -
            assertThat(actual.sku, `is`(expected.sku))
            assertThat(actual.name, `is`(expected.name))
            assertThat(actual.unitPrice, `is`(expected.unitPrice))
            assertThat(actual.imageUrl, `is`(expected.imageUrl))
            assertThat(actual.quantity, `is`(newStock))
            assertThat(1.0, `is`(expected.quantity))
        }

    @Test
    fun addItemInvoice_whenAddItemInInvoiceTerribleTimes_ShouldAddItemAndUpdateStock() =
        runTest {
            // Given
            itemRepository.sendItems()

            // When - added item
            val selectedItem = viewModel.uiState.value.items[0]
            val newStock = selectedItem.quantity.minus(3.0)
            viewModel.addItemInvoice(selectedItem)
            viewModel.addItemInvoice(selectedItem)
            viewModel.addItemInvoice(selectedItem)
            val expected =
                viewModel.uiState.value.itemsInvoice
                    .elementAt(0)
            // Then -
            assertThat(selectedItem.sku, `is`(expected.sku))
            assertThat(selectedItem.name, `is`(expected.name))
            assertThat(selectedItem.unitPrice, `is`(expected.unitPrice))
            assertThat(selectedItem.imageUrl, `is`(expected.imageUrl))
            assertThat(selectedItem.quantity, `is`(newStock))
            assertThat(3.0, `is`(expected.quantity))
        }

    @Test
    fun addItemInvoice_whenItemOutOfStock_returnMessageItemOfStock() {
        // Given
        itemRepository.sendItems()

        // When - added item and out of stock
        val actual = itemRepository.itemsTest[2]
        viewModel.addItemInvoice(actual)
        // Then
        assertThat(
            viewModel.uiState.value.userMessage,
            `is`(R.string.feature_sale_item_out_of_stock_message),
        )
    }

    @Test
    fun scanItem_whenSKUFound_returnItem() =
        runTest {
            // Given
            itemRepository.sendItems()
            // When
            viewModel.scanItem(itemRepository.itemsTest[0].sku)
            val expectedItem =
                viewModel.uiState.value.itemsInvoice
                    .elementAt(0)
            val actualItem = itemRepository.itemsTest[0]
            // Then - check item added to invoice and check quantity is start by one
            assertThat(expectedItem.sku, `is`(actualItem.sku))
            assertThat(expectedItem.quantity, `is`(1.0))
            assertThat(expectedItem.name, `is`(actualItem.name))
            assertThat(expectedItem.unitPrice, `is`(actualItem.unitPrice))
            assertThat(expectedItem.unitOfMeasurement, `is`(actualItem.unitOfMeasurement))
            assertThat(expectedItem.imageUrl, `is`(actualItem.imageUrl))
        }

    @Test
    fun scanItem_whenSKUNotFound_returnMessageItemNotFound() {
        // When
        viewModel.scanItem("not found")
        // Then
        assertThat(
            viewModel.uiState.value.userMessage,
            `is`(R.string.feature_sale_error_sale_item_not_found),
        )
    }

    @Test
    fun deleteItemInvoice_shouldResetQuantityItemInStock() =
        runTest {
            // Given - fetch items and  add item in invoice
            itemRepository.sendItems()
            viewModel.addItemInvoice(viewModel.uiState.value.items[0])

            // When delete item
            viewModel.deleteItemInvoice(
                viewModel.uiState.value.itemsInvoice
                    .elementAt(0),
            )
            // Then - check quantity item in stock
            assertThat(
                viewModel.uiState.value.items[0]
                    .quantity,
                `is`(itemRepository.itemsTest[0].quantity),
            )
        }

    @Test
    fun updateQuantityItem_shouldUpdateQuantityItemInStockAndInItemInvoice() =
        runTest {
            // Given - Add Items to Invoice
            itemRepository.sendItems()
            viewModel.addItemInvoice(viewModel.uiState.value.items[0])

            // When - Update the quantity
            viewModel.itemInvoiceSelected(
                viewModel.uiState.value.itemsInvoice
                    .elementAt(0),
            )
            viewModel.updateQuantityItemInvoice(23)
            println("TEST: ${viewModel.uiState.value.itemsInvoice}")
            // Then update item in stock and in items invoice
            assertThat(
                viewModel.uiState.value.items[0]
                    .quantity,
                `is`(0.0),
            )
            assertThat(
                viewModel.uiState.value.itemsInvoice
                    .elementAt(0)
                    .quantity,
                `is`(23.0),
            )
        }

    @Test
    fun updateQuantityItem_whenItemsSelectedIsNull_ShouldShowMessageUpdateFail() {
        // Given - Add Items to Invoice
        itemRepository.sendItems()
        viewModel.addItemInvoice(itemRepository.itemsTest[0])
        // When - Update the quantity
        viewModel.updateQuantityItemInvoice(23)
        // Then - show message fail to update quantity item
        assertThat(
            viewModel.uiState.value.userMessage,
            `is`(R.string.feature_sale_error_update_invoice_item_quantity),
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
        assertThat(
            viewModel.uiState.value.userMessage,
            `is`(R.string.feature_sale_error_update_invoice_item_quantity),
        )
    }

    @Test
    fun updateQuantityItem_whenItemsInvoiceIsEmpty_shouldShowMessageUpdateFail() {
        // Given - Add Items to Invoice
        itemRepository.sendItems()
        viewModel.itemInvoiceSelected(itemRepository.itemsTest[0])
        // When - Update the quantity
        viewModel.updateQuantityItemInvoice(23)
        // Then
        assertThat(
            viewModel.uiState.value.userMessage,
            `is`(R.string.feature_sale_error_update_invoice_item_quantity),
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

                else -> sb.append(char) // Append non-numeric characters as is
            }
        }
        return sb.toString()
    }
}