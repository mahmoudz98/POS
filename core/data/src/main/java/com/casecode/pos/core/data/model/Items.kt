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
package com.casecode.pos.core.data.model

import com.casecode.pos.core.firebase.services.ITEM_CATEGORY_FIELD
import com.casecode.pos.core.firebase.services.ITEM_COST_PRICE_FIELD
import com.casecode.pos.core.firebase.services.ITEM_DELETED_FIELD
import com.casecode.pos.core.firebase.services.ITEM_IMAGE_URL_FIELD
import com.casecode.pos.core.firebase.services.ITEM_NAME_FIELD
import com.casecode.pos.core.firebase.services.ITEM_PRICE_FIELD
import com.casecode.pos.core.firebase.services.ITEM_QTY_PER_PACK_FIELD
import com.casecode.pos.core.firebase.services.ITEM_QUANTITY_FIELD
import com.casecode.pos.core.firebase.services.ITEM_REORDER_LEVEL_FIELD
import com.casecode.pos.core.firebase.services.ITEM_SKU_FIELD
import com.casecode.pos.core.firebase.services.ITEM_SUPPLIER_NAME_FIELD
import com.casecode.pos.core.firebase.services.ITEM_UNIT_OF_MEASUREMENT_FIELD
import com.casecode.pos.core.firebase.services.model.ItemDataModel
import com.casecode.pos.core.model.data.users.Item
import com.casecode.pos.core.model.data.users.UnitOfMeasurement.Companion.toUnitOfMeasurement

fun ItemDataModel.asDomainModel() = Item(
    name = this.name,
    category = this.category,
    supplierName = this.supplierName,
    costPrice = this.costPrice,
    unitPrice = this.price,
    reorderLevel = this.reorderLevel,
    quantity = this.quantity,
    qtyPerPack = this.qtyPerPack,
    sku = this.sku,
    unitOfMeasurement = toUnitOfMeasurement(this.unitOfMeasurement),
    imageUrl = this.imageUrl,
    deleted = this.deleted,
)

fun Item.asExternalMapper(): Map<String, Any?> {
    val itemNetwork =
        ItemDataModel(
            name = this.name,
            category = this.category,
            supplierName = this.supplierName,
            costPrice = this.costPrice,
            price = this.unitPrice,
            reorderLevel = this.reorderLevel,
            quantity = this.quantity,
            qtyPerPack = this.qtyPerPack,
            sku = this.sku,
            unitOfMeasurement = this.unitOfMeasurement.toString(),
            imageUrl = this.imageUrl,
            deleted = this.deleted,
        )
    return mapOf(
        ITEM_NAME_FIELD to itemNetwork.name,
        ITEM_CATEGORY_FIELD to itemNetwork.category,
        ITEM_SUPPLIER_NAME_FIELD to itemNetwork.supplierName,
        ITEM_COST_PRICE_FIELD to itemNetwork.costPrice,
        ITEM_PRICE_FIELD to itemNetwork.price,
        ITEM_REORDER_LEVEL_FIELD to itemNetwork.reorderLevel,
        ITEM_QUANTITY_FIELD to itemNetwork.quantity,
        ITEM_QTY_PER_PACK_FIELD to itemNetwork.qtyPerPack,
        ITEM_SKU_FIELD to itemNetwork.sku,
        ITEM_UNIT_OF_MEASUREMENT_FIELD to itemNetwork.unitOfMeasurement,
        ITEM_IMAGE_URL_FIELD to itemNetwork.imageUrl,
        ITEM_DELETED_FIELD to itemNetwork.deleted,
    )
}