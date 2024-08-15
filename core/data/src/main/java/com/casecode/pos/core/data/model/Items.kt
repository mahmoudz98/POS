package com.casecode.pos.core.data.model

import com.casecode.pos.core.data.utils.ITEM_IMAGE_URL_FIELD
import com.casecode.pos.core.data.utils.ITEM_NAME_FIELD
import com.casecode.pos.core.data.utils.ITEM_PRICE_FIELD
import com.casecode.pos.core.data.utils.ITEM_QUANTITY_FIELD
import com.casecode.pos.core.data.utils.ITEM_SKU_FIELD
import com.casecode.pos.core.data.utils.ITEM_UNITOFMEASUREMENT_FIELD
import com.casecode.pos.core.model.data.users.Item
import com.google.firebase.firestore.PropertyName

data class ItemDataModel(
    val name: String = "",
    val price: Double = 0.0,
    var quantity: Double = 0.0,
    val sku: String = "",
    @set:PropertyName("unit_of_measurement") @get:PropertyName("unit_of_measurement") var unitOfMeasurement: String? = "",
    @set:PropertyName("image_url") @get:PropertyName("image_url") var imageUrl: String? = "",
)

fun ItemDataModel.asDomainModel() = Item(
    name = this.name,
    price = this.price,
    quantity = this.quantity,
    sku = this.sku,
    unitOfMeasurement = this.unitOfMeasurement,
    imageUrl = this.imageUrl,
)

fun Item.asExternalMapper(): Map<String, Any?> {
    val itemNetwork = ItemDataModel(
        this.name,
        this.price,
        this.quantity,
        this.sku,
        this.unitOfMeasurement,
        this.imageUrl,
    )
    return mapOf(
        ITEM_NAME_FIELD to itemNetwork.name,
        ITEM_PRICE_FIELD to itemNetwork.price,
        ITEM_QUANTITY_FIELD to itemNetwork.quantity,
        ITEM_SKU_FIELD to itemNetwork.sku,
        ITEM_UNITOFMEASUREMENT_FIELD to itemNetwork.unitOfMeasurement,
        ITEM_IMAGE_URL_FIELD to itemNetwork.imageUrl,
    )
}

fun Item.isInStock(): Boolean = quantity > 0.0