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
package com.casecode.pos.core.firebase.services.model

import com.casecode.pos.core.firebase.services.ITEM_COST_PRICE_FIELD
import com.casecode.pos.core.firebase.services.ITEM_IMAGE_URL_FIELD
import com.casecode.pos.core.firebase.services.ITEM_QTY_PER_PACK_FIELD
import com.casecode.pos.core.firebase.services.ITEM_REORDER_LEVEL_FIELD
import com.casecode.pos.core.firebase.services.ITEM_SUPPLIER_NAME_FIELD
import com.casecode.pos.core.firebase.services.ITEM_UNIT_OF_MEASUREMENT_FIELD
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

@IgnoreExtraProperties
data class ItemDataModel(
    val name: String = "",
    val category: String = "",
    @get:PropertyName(ITEM_SUPPLIER_NAME_FIELD)
    @set:PropertyName(ITEM_SUPPLIER_NAME_FIELD)
    var supplierName: String = "",
    @set:PropertyName(ITEM_COST_PRICE_FIELD)
    @get:PropertyName(ITEM_COST_PRICE_FIELD)
    var costPrice: Double = 0.0,
    val price: Double = 0.0,
    @set:PropertyName(ITEM_REORDER_LEVEL_FIELD)
    @get:PropertyName(ITEM_REORDER_LEVEL_FIELD)
    var reorderLevel: Int = 0,
    val quantity: Int = 0,
    @get:PropertyName(ITEM_QTY_PER_PACK_FIELD)
    @set:PropertyName(ITEM_QTY_PER_PACK_FIELD)
    var qtyPerPack: Int = 0,
    val sku: String = "",
    @get:PropertyName(ITEM_UNIT_OF_MEASUREMENT_FIELD)
    @set:PropertyName(ITEM_UNIT_OF_MEASUREMENT_FIELD)
    var unitOfMeasurement: String? = "",
    @get:PropertyName(ITEM_IMAGE_URL_FIELD)
    @set:PropertyName(ITEM_IMAGE_URL_FIELD)
    var imageUrl: String? = "",
    val deleted: Boolean = false,
)