package com.casecode.pos.core.domain.usecase

import com.casecode.pos.core.domain.R
import com.casecode.pos.core.domain.repository.ItemRepository
import com.casecode.pos.core.domain.repository.UpdateQuantityItems
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
        if (items.isNullOrEmpty()) return UpdateQuantityItems.empty(message = R.string.invoice_items_empty)
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