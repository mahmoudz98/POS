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

import java.util.Date

data class Invoice(
    val invoiceId: String = "",
    val date: Date = Date(),
    val createdBy: String = "",
    val customer: Customer? = null,
    val items: List<Item> = emptyList(),
) {
    val total: Double
        get() = items.sumOf { it.price * it.quantity }
}

fun Set<Item>.addItemToInvoices(item: Item): Set<Item> {
    val existingItem = find { it.sku == item.sku }
    return if (existingItem != null) {
        toMutableSet().apply {
            remove(existingItem)
            add(existingItem.copy(quantity = existingItem.quantity + 1))
        }
    } else {
        plus(item.copy(quantity = 1.0))
    }
}