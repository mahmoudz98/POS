package com.casecode.domain.repository

import com.casecode.domain.model.users.Item
import com.casecode.domain.utils.Resource

typealias GetItem = Resource<List<Item>>
typealias AddItem = Resource<String>
typealias UpdateItem = Resource<String>
typealias DeleteItem = Resource<String>

/**
 * An interface defining methods for CRUD operations on items.
 */
interface ItemRepository {
    /**
     * Retrieves items associated with the specified user ID [uid].
     *
     * @param uid The user ID for which items are to be retrieved.
     * @return A [GetItem] resource containing the retrieved items.
     */
    suspend fun getItems(uid: String): GetItem

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

    /**
     * Deletes an existing item from the repository.
     *
     * @param item The item to be deleted.
     * @return A [DeleteItem] resource indicating the success or failure of the deletion operation.
     */
    suspend fun deleteItem(item: Item): DeleteItem
}
