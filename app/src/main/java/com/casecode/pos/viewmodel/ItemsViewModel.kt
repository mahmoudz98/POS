package com.casecode.pos.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.casecode.data.utils.NetworkMonitor
import com.casecode.domain.model.users.Item
import com.casecode.domain.usecase.AddItemUseCase
import com.casecode.domain.usecase.DeleteItemUseCase
import com.casecode.domain.usecase.GetItemsUseCase
import com.casecode.domain.usecase.ItemImageUseCase
import com.casecode.domain.usecase.UpdateItemUseCase
import com.casecode.domain.utils.Resource
import com.casecode.pos.R
import com.casecode.pos.base.BaseViewModel
import com.casecode.pos.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemsViewModel
@Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val getItemsUseCase: GetItemsUseCase,
    private val addItemsUseCase: AddItemUseCase,
    private val updateItemUseCase: UpdateItemUseCase,
    private val deleteItemUseCase: DeleteItemUseCase,
    private val imageUseCase: ItemImageUseCase,
) : BaseViewModel() {
    private val isOnline: MutableLiveData<Boolean> = MutableLiveData(false)

    private val _items = MutableLiveData<List<Item>>()
    val items get() = _items

    private val _itemSelected = MutableLiveData<Item>()
    val itemSelected: LiveData<Item> = _itemSelected
    private val _isAddOrUpdateItem: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isAddItem get() = _isAddOrUpdateItem
    private val bitmapImageItem = MutableLiveData<Bitmap?>()
    private val itemImageChanged = MutableLiveData<Boolean>(false)
    private val itemUpdated = MutableLiveData<Item>()

    private val _isEmptyItems: MutableLiveData<Boolean> = MutableLiveData(false)
    val isEmptyItems: LiveData<Boolean> get() = _isEmptyItems

    init {
        setNetworkMonitor()
    }

    private fun setNetworkMonitor() = viewModelScope.launch {
        networkMonitor.isOnline.collect {
            setConnected(it)
        }
    }

    private fun setConnected(isConnect: Boolean) {
        isOnline.value = isConnect
    }

    internal fun fetchItems() {
        viewModelScope.launch {
            getItemsUseCase().collect {
                when (val result = it) {
                    is Resource.Empty -> {
                        _isEmptyItems.value = true
                        hideProgress()
                    }
                    is Resource.Error -> {
                        _isEmptyItems.value =
                            _items.value.isNullOrEmpty() // true if empty else false
                        showSnackbarMessage(result.message as? Int ?: R.string.all_error_unknown)
                        hideProgress()
                    }

                    is Resource.Loading -> {
                        showProgress()
                    }

                    is Resource.Success -> {
                        _isEmptyItems.value = false
                        _items.value = result.data
                        hideProgress()
                    }
                }
            }
        }
    }

    fun checkNetworkAndAddItem() {
        if (isOnline.value == true) {
            uploadImageAndAddItem()
        } else {
            isAddItemOrUpdate()
            hideProgress()
            showSnackbarMessage(R.string.network_error)
        }
    }

    private fun uploadImageAndAddItem() {
        viewModelScope.launch {
            val sku = itemSelected.value?.sku!!
            imageUseCase.uploadImage(bitmap = bitmapImageItem.value, imageName = sku).collect {
                when (val uploadImage = it) {
                    is Resource.Loading -> showProgress()
                    is Resource.Error -> {
                        hideProgress()
                        showSnackbarMessage(uploadImage.message as Int)
                    }

                    is Resource.Empty -> {
                        addItem()
                    }

                    is Resource.Success -> {
                        val imageUrl = uploadImage.data
                        // Now you can use the imageUrl as needed, e.g., updating the item object
                        _itemSelected.value?.imageUrl = imageUrl
                        addItem()
                    }
                }
            }
        }
    }

    private fun addItem() {
        viewModelScope.launch {
            handleResponseAddAndUpdateItem(addItemsUseCase(_itemSelected.value!!))
        }
    }

    fun updateItemImage() {
        itemImageChanged.value = true
    }

    fun checkNetworkAndUpdateItem() {
        if (isOnline.value == true) {
            checkItemImageChangeAndItemUpdate()
        } else {
            isAddItemOrUpdate()
            hideProgress()
            showSnackbarMessage(R.string.network_error)
        }
    }

    private fun checkItemImageChangeAndItemUpdate() {
        if (itemImageChanged.value == true) {
            itemImageChanged.value = false
            replaceImageAndUpdateItem()
        } else {
            itemUpdated.value?.imageUrl = _itemSelected.value?.imageUrl
            updateItem()

        }
    }
    private fun replaceImageAndUpdateItem(
    ) {
        viewModelScope.launch {
            imageUseCase.replaceOrUploadImage(
                bitmapImageItem.value,
                _itemSelected.value?.imageUrl,
                _itemSelected.value?.sku,
            ).collect {
                when (val urlImage = it) {
                    is Resource.Loading -> showProgress()
                    is Resource.Error -> {
                        hideProgress()
                        showSnackbarMessage(urlImage.message as Int)
                    }

                    is Resource.Success -> {
                        itemUpdated.value?.imageUrl = urlImage.data
                        // Now you can add the item
                        updateItem()
                    }

                    is Resource.Empty -> {
                        updateItem()
                    }
                }
            }
        }
    }

    private fun updateItem() {
        viewModelScope.launch {
            if (itemUpdated.value != _itemSelected.value && itemUpdated.value != null) {
                handleResponseAddAndUpdateItem(updateItemUseCase(itemUpdated.value!!))
            } else {
                showSnackbarMessage(R.string.update_item_fail)
                isAddItemOrUpdate(true)
            }
        }
    }

    private fun handleResponseAddAndUpdateItem(result: Resource<Int>) {
        when (result) {
            is Resource.Loading -> showProgress()
            is Resource.Error, is Resource.Empty -> {
                 isAddItemOrUpdate()
                hideProgress()
                showSnackbarMessage((result as Resource.Error).message as Int)
            }

            is Resource.Success -> {

                hideProgress()
                isAddItemOrUpdate(true)
                showSnackbarMessage(result.data)
            }
        }
    }

    private fun isAddItemOrUpdate(isAddOrUpdate:Boolean = false) {
        _isAddOrUpdateItem.value = Event(isAddOrUpdate)
    }
    fun checkNetworkAndDeleteItem(item:Item) {
        if (isOnline.value == true) {
            deleteImageAndDeleteItem(item)
        } else {
            showSnackbarMessage(R.string.network_error)
        }
    }

  private  fun deleteImageAndDeleteItem(item: Item) {
        viewModelScope.launch {
            imageUseCase.deleteImage(imageUrl = item.imageUrl).collect { deleteImage ->
                when (deleteImage) {
                    is Resource.Loading -> showProgress()
                    is Resource.Error -> {
                        hideProgress()
                        showSnackbarMessage(deleteImage.message as Int)
                        deleteItem(item)
                    }

                    is Resource.Success, is Resource.Empty -> {
                        // Image deleted successfully, now delete the item
                        deleteItem(item)
                    }
                }
            }
        }
    }

    private fun deleteItem(item: Item) {
        viewModelScope.launch { handleResponseDeleteItem(deleteItemUseCase(item)) }
    }

    private fun handleResponseDeleteItem(result: Resource<Int>) {
        when (result) {
            is Resource.Loading -> showProgress()
            is Resource.Error -> {
                hideProgress()
                showSnackbarMessage(result.message as Int)
            }

            is Resource.Success, is Resource.Empty -> {
                hideProgress()
                showSnackbarMessage((result as Resource.Success).data)
            }

        }
    }

    fun setItemSelected(item: Item) {
        _itemSelected.value = item
    }

    fun setBitmap(bitmap: Bitmap?) {
        this.bitmapImageItem.value = bitmap
    }

    fun setItemUpdated(item: Item) {
        itemUpdated.value = item
    }

}