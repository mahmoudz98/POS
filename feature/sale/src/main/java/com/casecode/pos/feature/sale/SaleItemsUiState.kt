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

import androidx.compose.runtime.Stable
import androidx.compose.runtime.snapshots.SnapshotStateSet
import com.casecode.pos.core.model.data.users.Item

/**
 * Represents the UI state for a list of sale items.
 *
 * This class manages the list of items added to a sale, their quantities, and provides
 * functionalities for adding, updating, removing, and clearing items. It also calculates
 * the total value of the sale items.
 */
@Stable
class SaleItemsUiState {
    private var _items = SnapshotStateSet<Item>()
    val items: Set<Item> get() = _items
    val totalSaleItems: Double
        get() {
            return _items.sumOf {
                it.unitPrice.times(it.quantity.toDouble())
            }
        }
    fun addItem(item: Item) {
        val existingItem = _items.find { it.sku == item.sku }
        _items = _items.apply {
            if (existingItem != null) {
                remove(existingItem)
                add(
                    existingItem.copy(quantity = existingItem.quantity + 1),
                )
            } else {
                add(item.copy(quantity = 1))
            }
        }
    }

    fun updateItemQuantity(sku: String, newQuantity: Int) {
        val existingItem = _items.find { it.sku == sku } ?: return
        _items = _items.apply {
            remove(existingItem)
            add(
                existingItem.copy(quantity = newQuantity),
            )
        }
    }

    fun removeItem(item: Item) {
        _items = _items.apply {
            remove(item)
        }
    }

    fun clear() {
        _items = _items.apply {
            clear()
        }
    }
}