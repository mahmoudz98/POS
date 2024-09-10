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
 * Represents an item in inventory or a product.
 *
 * @property name The name of the item.
 * @property price The price of the item. Default is 0.0.
 * @property quantity The quantity of the item. Default is 0.0.
 * @property sku The stock keeping unit (SKU) of the item.
 * @property unitOfMeasurement The unit of measurement for the item. Can be null.
 * @property imageUrl The URL of the image associated with the item. Default is null.
 * @constructor Creates an item with default values for name, price, quantity, and imageUrl.
 */
data class Item(
    val name: String = "",
    val price: Double = 0.0,
    var quantity: Double = 0.0,
    val sku: String = "",
    var unitOfMeasurement: String? = "",
    var imageUrl: String? = "",
)