package com.casecode.testing.repository

import com.casecode.domain.model.users.Item
import com.casecode.domain.repository.AddItem
import com.casecode.domain.repository.DeleteItem
import com.casecode.domain.repository.ItemRepository
import com.casecode.domain.repository.ResourceItems
import com.casecode.domain.repository.UpdateItem
import com.casecode.domain.repository.UpdateQuantityItems
import com.casecode.domain.utils.Resource
import com.casecode.testing.base.BaseTestRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class TestItemRepository @Inject constructor(): ItemRepository, BaseTestRepository() {
    var fakeItems =
        arrayListOf(
            Item("item #1", 1.0, 23.0, "1234567899090", "EA", "www.image1.png"),
            Item("item #2", 3.0, 421312.0, "1555567899090", "EA", "www.image2.png"),
            Item("item #2", 3.0, 0.0, "1200", "EA", "www.image2.png"),
        )

    override fun init() = Unit

    fun setItemFakeList(itemsArrayList: ArrayList<Item>) {
        fakeItems.addAll(itemsArrayList)
    }

    override fun getItems(): Flow<ResourceItems> {
        if (shouldReturnError) {
            return flowOf(Resource.error(com.casecode.pos.data.R.string.error_fetching_items))
        }
        if (shouldReturnEmpty) {
            return flowOf(Resource.empty())
        }
        return flowOf(ResourceItems.success(fakeItems))
    }

  override suspend  fun addItem(item: Item): AddItem {
        fakeItems.add(item)
        return if(shouldReturnError){
            AddItem.error(com.casecode.pos.data.R.string.add_item_failure_generic)
        } else{
            fakeItems.add(item)
            Resource.Success( com.casecode.pos.data.R.string.item_added_successfully)

        }
    }

    override suspend fun updateItem(item: Item): UpdateItem {
        if(shouldReturnError){
            return UpdateItem.error(com.casecode.pos.data.R.string.update_item_failure_generic)
        }
       return Resource.Success(com.casecode.pos.data.R.string.item_updated_successfully)
    }

    override suspend fun updateQuantityInItems(items: List<Item>): UpdateQuantityItems {
        println(shouldReturnError)
        if(shouldReturnError) {
            return UpdateQuantityItems.error(com.casecode.pos.data.R.string.update_item_failure_generic)
        }
        return Resource.Success(items)
    }

    override suspend fun deleteItem(item: Item): DeleteItem {
        if(shouldReturnError){
            return DeleteItem.error(com.casecode.pos.data.R.string.delete_item_failure_generic)
        }
        fakeItems.remove(item)
        return Resource.success(com.casecode.pos.data.R.string.item_deleted_successfully)
    }
}