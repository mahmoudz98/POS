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

import com.casecode.pos.core.model.data.users.Item

data class SaleUiState(
    val searchQuery: String = "",
    val items: List<Item> = emptyList(),
    val itemsInvoice: Set<Item> = emptySet(),
    val itemInvoiceSelected: Item? = null,
    val invoiceState: InvoiceState = InvoiceState.Loading,
    val itemSelected: Item? = null,
    val amountInput: String = "",
    val userMessage: Int? = null,
) {
    val totalItemsInvoice: Double
        get() = itemsInvoice.sumOf { it.unitPrice.times(it.quantity.toDouble()) }

    val restOfAmount: Double
        get() =
            amountInput.toDoubleOrNull().run {
                if (this == null || this == totalItemsInvoice) {
                    return 0.0
                }
                return this.minus(totalItemsInvoice)
            }
}

// Sealed class to represent the invoice states
sealed interface InvoiceState {
    data object Loading : InvoiceState

    data object EmptyItems : InvoiceState

    data object EmptyItemInvoice : InvoiceState

    data object HasItems : InvoiceState
}

sealed interface SaleItemsInvoiceUiState {
    data object Empty : SaleItemsInvoiceUiState

    data class Success(
        val itemsInvoice: Set<Item>,
    ) : SaleItemsInvoiceUiState
}