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
package com.casecode.pos.core.testing.repository

import com.casecode.pos.core.domain.repository.AddItem
import com.casecode.pos.core.domain.repository.DeleteItem
import com.casecode.pos.core.domain.repository.ItemRepository
import com.casecode.pos.core.domain.repository.ResourceItems
import com.casecode.pos.core.domain.repository.UpdateItem
import com.casecode.pos.core.domain.repository.UpdateQuantityItems
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Item
import com.casecode.pos.core.testing.base.BaseTestRepository
import com.casecode.pos.core.testing.data.itemsTestData
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import kotlin.text.isNotBlank
import com.casecode.pos.core.data.R.string as stringData

class TestItemRepository
@Inject
constructor() :
    BaseTestRepository(),
    ItemRepository {
    private val resourcesItemsFlow: MutableSharedFlow<ResourceItems> =
        MutableSharedFlow(replay = 2, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val itemsTest = ArrayList(itemsTestData)
    val categoriesTest
        get() =
            itemsTest
                .mapNotNull {
                    it.category.takeIf { it.isNotBlank() }
                }.toSet()

    override fun init() = Unit

    fun sendItems() {
        resourcesItemsFlow.tryEmit(Resource.success(itemsTest))
    }

    fun sendItems(itemsArrayList: ArrayList<Item>) {
        itemsTest.addAll(itemsArrayList)
        sendItems()
    }

    override fun setReturnEmpty(value: Boolean) {
        super.setReturnEmpty(value)
        resourcesItemsFlow.tryEmit(Resource.empty())
    }

    override fun setReturnError(value: Boolean) {
        super.setReturnError(value)
        resourcesItemsFlow.tryEmit(Resource.error(stringData.core_data_error_fetching_items))
    }

    override fun getItems(): Flow<ResourceItems> = resourcesItemsFlow

    override suspend fun addItem(item: Item): AddItem = if (shouldReturnError) {
        Resource.error(stringData.core_data_add_item_failure_generic)
    } else {
        itemsTest.add(item)
        resourcesItemsFlow.tryEmit(Resource.success(itemsTest))
        Resource.Success(stringData.core_data_item_added_successfully)
    }

    override suspend fun updateItem(item: Item): UpdateItem {
        if (shouldReturnError) {
            return Resource.Companion.error(stringData.core_data_update_item_failure_generic)
        }
        return Resource.Success(stringData.core_data_item_updated_successfully)
    }

    override suspend fun updateQuantityInItems(items: List<Item>): UpdateQuantityItems {
        println(shouldReturnError)
        if (shouldReturnError) {
            return Resource.Companion.error(stringData.core_data_update_item_failure_generic)
        }
        return Resource.Success(items)
    }

    override suspend fun deleteItem(item: Item): DeleteItem {
        if (shouldReturnError) {
            return Resource.Companion.error(stringData.core_data_delete_item_failure_generic)
        }
        itemsTest.remove(item)
        return Resource.success(stringData.core_data_item_deleted_successfully)
    }
}