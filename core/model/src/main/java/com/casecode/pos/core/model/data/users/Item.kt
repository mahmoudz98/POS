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
package com.casecode.pos.core.model.data.users

/**
 * Data class representing an item in the inventory.
 *
 * @property name The name of the item.
 * @property category The category the item belongs to.
 * @property supplierName The name of the supplier for this item.
 * @property costPrice The cost price of the item.
 * @property unitPrice The selling price of the item.
 * @property reorderLevel The minimum stock level at which the item should be reordered.
 * @property quantity The current quantity of the item in stock.
 * @property qtyPerPack The number of items in a single pack.
 * @property sku The Stock Keeping Unit (SKU) for the item.
 * @property unitOfMeasurement The unit of measurement for the item (e.g., Per Piece, Per Kilo).
 * @property deleted Indicates whether the item has been marked as deleted.
 * @property imageUrl The URL of the item's image.
 */
data class Item(
    val name: String = "",
    val category: String = "",
    val supplierName: String = "",
    val costPrice: Float = 0f,
    val unitPrice: Float = 0f,
    val reorderLevel: Int = -1,
    var quantity: Int = 0,
    val qtyPerPack: Int = 0,
    val sku: String = "",
    var unitOfMeasurement: UnitOfMeasurement? = UnitOfMeasurement.PerPiece,
    var imageUrl: String? = "",
    val deleted: Boolean = false,
) {

    fun isInStockAndTracked(): Boolean = !((quantity > 0) xor (isTrackStock()))
    fun isTrackStock(): Boolean = (reorderLevel != -1)
    fun isLowLevelStock(): Boolean = reorderLevel >= quantity
}