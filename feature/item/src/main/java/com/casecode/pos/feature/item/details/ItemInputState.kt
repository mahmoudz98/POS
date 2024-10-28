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
package com.casecode.pos.feature.item.details

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import com.casecode.pos.core.model.data.users.Item
import com.casecode.pos.feature.item.R
import com.casecode.pos.feature.item.utils.NOT_TRACKED_REORDER_LEVEL

@Composable
fun rememberItemInputState(itemUpdated: Item?): ItemInputState {
    return rememberSaveable(itemUpdated, saver = ItemInputState.Saver) {
        ItemInputState(itemUpdated)
    }
}

@Stable
class ItemInputState(itemUpdated: Item?) {

    var name by mutableStateOf("")
    var category by mutableStateOf("")
    var price by mutableStateOf("")
    var costPrice by mutableStateOf("")
    var quantity by mutableStateOf("")
    var reorderLevel by mutableIntStateOf(-1)
    var qtyPerPack by mutableIntStateOf(0)
    var sku by mutableStateOf("")
    var selectedImageUri by mutableStateOf(Uri.EMPTY)
    val isTrackSelected by derivedStateOf { reorderLevel > NOT_TRACKED_REORDER_LEVEL }

    var nameError by mutableStateOf(false)
    var priceError by mutableStateOf<Int?>(null)
    var costPriceError by mutableStateOf<Int?>(null)
    var quantityError by mutableStateOf(false)
    var skuError by mutableStateOf<Int?>(null)

    init {
        itemUpdated?.let { item ->
            name = item.name
            category = item.category
            price = item.unitPrice.toFormattedString()
            costPrice = item.costPrice.toFormattedString()
            quantity = item.quantity.toString()
            reorderLevel = item.reorderLevel
            qtyPerPack = item.qtyPerPack
            sku = item.sku
            selectedImageUri = item.imageUrl?.toUri() ?: Uri.EMPTY
        }
    }

    private fun Double?.toFormattedString(): String =
        this?.toBigDecimal()?.stripTrailingZeros()?.toPlainString() ?: ""

    fun hasValidateInput(): Boolean {
        nameError = name.isEmpty()
        priceError = if (price.isEmpty()) {
            R.string.feature_item_error_price_empty
        } else null
        quantityError = quantity.isEmpty() && isTrackSelected
        skuError = if (sku.isEmpty()) R.string.feature_item_error_sku_empty else null

        return !(nameError || priceError != null || quantityError || skuError != null)
    }

    companion object {
        val Saver: Saver<ItemInputState, Any> = listSaver(
            save = {
                listOf(
                    it.name,
                    it.category,
                    it.price,
                    it.costPrice,
                    it.quantity,
                    it.reorderLevel,
                    it.qtyPerPack,
                    it.sku,
                )
            },
            restore = { restoredList ->
                ItemInputState(
                    Item(
                        name = restoredList[0] as String,
                        category = restoredList[1] as String,
                        unitPrice = (restoredList[2] as String).toDoubleOrNull() ?: 0.0,
                        costPrice = (restoredList[3] as String).toDoubleOrNull() ?: 0.0,
                        quantity = (restoredList[4] as String).toIntOrNull() ?: 0,
                        reorderLevel = restoredList[5] as Int,
                        qtyPerPack = restoredList[6] as Int,
                        sku = restoredList[7] as String,
                    ),
                )
            },
        )
    }
}