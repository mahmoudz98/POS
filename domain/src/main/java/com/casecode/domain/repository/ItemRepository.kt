package com.casecode.domain.repository

import com.casecode.domain.model.users.Item
import com.casecode.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

typealias ResourceItems = Resource<List<Item>>
typealias AddItem = Resource<Int>
typealias UpdateItem = Resource<Int>
typealias UpdateQuantityItems = Resource<List<Item>>
typealias DeleteItem = Resource<Int>

/**
 * An interface defining methods for CRUD operations on items.
 */
interface ItemRepository {
    /**
     * Retrieves items associated with the specified user ID.
     *
     * @return A [ResourceItems] resource containing the retrieved items.
     */
    fun getItems(): Flow<ResourceItems>

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

    suspend fun updateQuantityInItems(items: List<Item>): UpdateQuantityItems

    /**
     * Deletes an existing item from the repository.
     *
     * @param item The item to be deleted.
     * @return A [DeleteItem] resource indicating the success or failure of the deletion operation.
     */
    suspend fun deleteItem(item: Item): DeleteItem
}