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
package com.casecode.pos.core.domain.repository

import com.casecode.pos.core.domain.utils.OperationResult
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Item
import kotlinx.coroutines.flow.Flow

typealias ResourceItems = Resource<List<Item>>
typealias AddItem = Resource<Int>
typealias UpdateItem = Resource<Int>
typealias DeleteItem = Resource<Int>

/**
 * An interface defining methods for CRUD operations on invoiceItems.
 */
interface ItemRepository {
    /**
     * Retrieves invoiceItems associated with the specified user ID.
     *
     * @return A [] resource containing the retrieved invoiceItems.
     */
    fun getItems(): Flow<Resource<List<Item>>>

    /**
     * Adds a new item to the repository.
     *
     * @param item The item to be added.
     * @return An [AddItem] resource indicating the success or failure of the addition operation.
     */
    suspend fun addItem(item: Item): AddItem

    /**
     * Updates an existing item in the repository.
     *
     * @param item The updated item.
     * @return An [UpdateItem] resource indicating the success or failure of the update operation.
     */
    suspend fun updateItem(item: Item): UpdateItem

    suspend fun updateQuantityInItems(items: List<Item>, isIncrement: Boolean): OperationResult

    /**
     * Deletes an existing item from the repository.
     *
     * @param item The item to be deleted.
     * @return A [DeleteItem] resource indicating the success or failure of the deletion operation.
     */
    suspend fun deleteItem(item: Item): DeleteItem
}