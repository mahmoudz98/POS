package com.casecode.data.mapper

import com.casecode.domain.model.users.Item
import com.casecode.domain.utils.ITEM_IMAGE_URL_FIELD
import com.casecode.domain.utils.ITEM_NAME_FIELD
import com.casecode.domain.utils.ITEM_PRICE_FIELD
import com.casecode.domain.utils.ITEM_QUANTITY_FIELD
import com.casecode.domain.utils.ITEM_SKU_FIELD
import com.casecode.domain.utils.ITEM_UNITOFMEASUREMENT_FIELD

fun Item.asExternalModel() = mapOf(
    ITEM_NAME_FIELD to this.name,
    ITEM_PRICE_FIELD to this.price,
    ITEM_QUANTITY_FIELD to this.quantity,
    ITEM_SKU_FIELD to this.sku,
    ITEM_UNITOFMEASUREMENT_FIELD to this.unitOfMeasurement,
    ITEM_IMAGE_URL_FIELD to this.imageUrl,
)

fun Item.hasItemOutOfStock(): Boolean = quantity <= 0.0

fun MutableSet<Item>.addItemToInvoices(item: Item): Pair<Boolean, MutableSet<Item>> {
    val isAdded: Boolean
    val newInvoiceItems = this.apply {
        val existingItem = this.find { it.sku == item.sku }
        isAdded = if (existingItem != null) {
            this.remove(existingItem)
            this.add(existingItem.copy(quantity = existingItem.quantity.inc()))
        } else {
            // Add the item to the set with quantity 1.0
            this.add(item.copy(quantity = 1.0))
        }
    }
    return Pair(isAdded, newInvoiceItems)

}

fun MutableSet<Item>.updateQuantityItem(
    item: Item,
    newQuantity: Double,
): Pair<Boolean, MutableSet<Item>> {
    remove(item)
    val isUpdate = add(item.copy(quantity = newQuantity))
    return Pair(isUpdate, this)
}