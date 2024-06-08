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
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class TestItemRepository @Inject constructor(): ItemRepository, BaseTestRepository() {
    private val resourcesItemsFlow: MutableSharedFlow<ResourceItems> =
        MutableSharedFlow(replay = 2, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    var fakeListItems =
        arrayListOf(
            Item("item #1", 1.0, 23.0, "1234567899090", "EA", "www.image1.png"),
            Item("item #2", 3.0, 421312.0, "1555567899090", "EA", "www.image2.png"),
            Item("item #2", 3.0, 0.0, "1200", "EA", "www.image2.png"),
        )

    override fun init() = Unit
    fun sendItems(){
        resourcesItemsFlow.tryEmit(Resource.success(fakeListItems))

    }
    fun sendItems(itemsArrayList: ArrayList<Item>) {
        fakeListItems.addAll(itemsArrayList)
        sendItems()
    }

    override fun setReturnEmpty(value: Boolean) {
        super.setReturnEmpty(value)
        resourcesItemsFlow.tryEmit(Resource.empty())
    }

    override fun setReturnError(value: Boolean) {
        super.setReturnError(value)
        resourcesItemsFlow.tryEmit(Resource.error(com.casecode.pos.data.R.string.error_fetching_items))
    }

    override fun getItems(): Flow<ResourceItems> {

        return resourcesItemsFlow
    }

  override suspend  fun addItem(item: Item): AddItem {
        return if(shouldReturnError){
            AddItem.error(com.casecode.pos.data.R.string.add_item_failure_generic)
        } else{
            fakeListItems.add(item)
            resourcesItemsFlow.tryEmit(Resource.success(fakeListItems))
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
        fakeListItems.remove(item)
        return Resource.success(com.casecode.pos.data.R.string.item_deleted_successfully)
    }
}