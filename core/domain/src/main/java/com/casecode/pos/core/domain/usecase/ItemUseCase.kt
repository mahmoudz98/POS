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
package com.casecode.pos.core.domain.usecase

import com.casecode.pos.core.domain.R
import com.casecode.pos.core.domain.repository.ItemRepository
import com.casecode.pos.core.domain.repository.UpdateQuantityItems
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Item
import javax.inject.Inject

class GetItemsUseCase
@Inject
constructor(
    private val itemRepository: ItemRepository,
) {
    operator fun invoke() = itemRepository.getItems()
}

class AddItemUseCase
@Inject
constructor(
    private val itemRepository: ItemRepository,
) {
    suspend operator fun invoke(item: Item) = itemRepository.addItem(item)
}

class UpdateItemUseCase
@Inject
constructor(
    private val itemRepository: ItemRepository,
) {
    suspend operator fun invoke(item: Item) = itemRepository.updateItem(item)
}

class UpdateStockInItemsUseCase
@Inject
constructor(
    private val itemRepository: ItemRepository,
) {
    suspend operator fun invoke(items: List<Item>?): UpdateQuantityItems {
        if (items.isNullOrEmpty()) return Resource.Companion.empty(
            message = R.string.core_domain_invoice_items_empty,
        )
        return itemRepository.updateQuantityInItems(items)
    }
}

class DeleteItemUseCase
@Inject
constructor(
    private val itemRepository: ItemRepository,
) {
    suspend operator fun invoke(item: Item) = itemRepository.deleteItem(item)
}