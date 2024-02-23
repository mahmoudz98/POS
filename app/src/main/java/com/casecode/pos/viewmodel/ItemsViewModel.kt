package com.casecode.pos.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.domain.model.users.Item
import com.casecode.domain.usecase.ImageUseCase
import com.casecode.domain.usecase.ItemUseCase
import com.casecode.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel class responsible for managing item-related data and operations.
 *
 * @property itemUseCase The use case for item-related operations.
 * @property imageUseCase The use case for image-related operations.
 * @constructor Creates an [ItemsViewModel] with the provided [itemUseCase] and [imageUseCase].
 */
@HiltViewModel
class ItemsViewModel @Inject constructor(
    private val itemUseCase: ItemUseCase,
    private val imageUseCase: ImageUseCase
) : ViewModel() {

    // LiveData for items
    private val _items = MutableLiveData<Resource<List<Item>>>()
    val items: LiveData<Resource<List<Item>>> = _items

    // LiveData for item action state
    private val _itemActionState = MutableLiveData<Resource<String>>()
    val itemActionState: LiveData<Resource<String>> = _itemActionState

    // LiveData for individual item
    private val _item = MutableLiveData<Item?>()
    val item: LiveData<Item?> = _item

    // LiveData for bitmap image
    private val _bitmap = MutableLiveData<Bitmap?>()
    val bitmap: LiveData<Bitmap?> = _bitmap

    /**
     * Uploads an image and adds an item.
     *
     * @param bitmap The bitmap image to upload.
     * @param item The item to add.
     */
    fun uploadImageAndAddItem(bitmap: Bitmap, item: Item) {
        viewModelScope.launch {
            _itemActionState.value = Resource.Loading("Uploading image...")

            when (val result = imageUseCase.uploadImage(bitmap = bitmap, imageName = item.sku)) {
                is Resource.Empty -> TODO()
                is Resource.Error -> {
                    // Handle error case
                    Timber.e("Error uploading image: ${result.data}")
                    _itemActionState.value =
                        Resource.Error(result.message ?: "Unknown error occurred")
                }

                is Resource.Loading -> {
                    // Handle loading state if necessary
                }

                is Resource.Success -> {
                    // Handle success case
                    val imageUrl = result.data
                    // Now you can use the imageUrl as needed, e.g., updating the item object
                    item.imageUrl = imageUrl

                    // Now you can add the item
                    addItem(item)
                }
            }
        }
    }

    /**
     * Uploads an image and updates an item.
     *
     * @param bitmap The bitmap image to upload.
     * @param item The item to update.
     */
    fun uploadImageAndUpdateItem(bitmap: Bitmap, item: Item) {
        viewModelScope.launch {
            _itemActionState.value = Resource.Loading("Uploading image...")

            when (val result = imageUseCase.uploadImage(bitmap = bitmap, imageName = item.sku)) {
                is Resource.Empty -> TODO()
                is Resource.Error -> {
                    // Handle error case
                    Timber.e("Error uploading image: ${result.data}")
                    _itemActionState.value =
                        Resource.Error(result.message ?: "Unknown error occurred")
                }

                is Resource.Loading -> {
                    // Handle loading state if necessary
                }

                is Resource.Success -> {
                    // Handle success case
                    val imageUrl = result.data
                    // Now you can use the imageUrl as needed, e.g., updating the item object
                    item.imageUrl = imageUrl

                    // Now you can add the item
                    updateItem(item)
                }
            }
        }
    }

    /**
     * Deletes an image and deletes an item.
     *
     * @param item The item to delete.
     */
    fun deleteImageAndDeleteItem(item: Item) {
        viewModelScope.launch {
            _itemActionState.value = Resource.Loading("Deleting image...")

            when (val result = imageUseCase.deleteImage(imageUrl = item.imageUrl.toString())) {
                is Resource.Empty -> TODO()
                is Resource.Error -> {
                    // Handle error case
                    _itemActionState.value =
                        Resource.Error(result.message ?: "Unknown error occurred")
                }

                is Resource.Loading -> {
                    // Handle loading state if necessary
                    TODO()
                }

                is Resource.Success -> {
                    // Handle success case
                    Timber.e(result.data)
                    // Image deleted successfully, now delete the item
                    deleteItem(item)
                }
            }
        }
    }

    /**
     * Adds an item.
     *
     * @param item The item to add.
     */
    fun addItem(item: Item) {
        viewModelScope.launch {
            when (val result = itemUseCase.addItem(item)) {
                is Resource.Empty -> TODO()
                is Resource.Error -> {
                    // Handle error case
                    Timber.e(result.message.toString())
                    _itemActionState.value =
                        Resource.Error(result.message ?: "Unknown error occurred")
                }

                is Resource.Loading -> {
                    // Handle loading state if necessary
                }

                is Resource.Success -> {
                    // Handle success case
                    // Item added successfully
                    Timber.i(message = result.data)
                    _itemActionState.value = Resource.Success(result.data)

                    // Fetch items again to refresh the list
                    fetchItems("nRoH5pcgCsbMIHVa6tmN3wkEPFL2")
                }
            }
        }
    }

    /**
     * Updates an item.
     *
     * @param item The item to update.
     */
    fun updateItem(item: Item) {
        viewModelScope.launch {
            when (val result = itemUseCase.updateItem(item)) {
                is Resource.Empty -> TODO()
                is Resource.Error -> {
                    // Handle error case
                    Timber.e(result.message.toString())
                    _itemActionState.value =
                        Resource.Error(result.message ?: "Unknown error occurred")
                }

                is Resource.Loading -> {
                    // Handle loading state if necessary
                    TODO()
                }

                is Resource.Success -> {
                    // Handle success case
                    // Item updated successfully
                    Timber.i(message = result.data)
                    _itemActionState.value = Resource.Success(result.data)

                    // Fetch items again to refresh the list
                    fetchItems("nRoH5pcgCsbMIHVa6tmN3wkEPFL2")
                }
            }
        }
    }

    /**
     * Deletes an item.
     *
     * @param item The item to delete.
     */
    fun deleteItem(item: Item) {
        viewModelScope.launch {
            when (val result = itemUseCase.deleteItem(item)) {
                is Resource.Empty -> TODO()
                is Resource.Error -> {
                    // Handle error case
                    Timber.e(result.message.toString())
                    _itemActionState.value =
                        Resource.Error(result.message ?: "Unknown error occurred")
                }

                is Resource.Loading -> {
                    // Handle loading state if necessary
                }

                is Resource.Success -> {
                    // Handle success case
                    // Item deleted successfully
                    Timber.i(result.data)
                    _itemActionState.value = Resource.Success(result.data)

                    fetchItems("nRoH5pcgCsbMIHVa6tmN3wkEPFL2")
                }
            }
        }
    }

    init {
        fetchItems("nRoH5pcgCsbMIHVa6tmN3wkEPFL2")
    }

    /**
     * Fetches items for the specified user ID.
     *
     * @param uid The user ID.
     */
    private fun fetchItems(uid: String) {
        viewModelScope.launch {
            when (val result = itemUseCase.getItems(uid)) {
                is Resource.Empty -> {
                    Timber.e("fetch items is empty data!.")
                    _items.value = Resource.Empty()
                }

                is Resource.Error -> {
                    Timber.e(result.message.toString())
                    _items.value = Resource.Error(result.message)
                }

                is Resource.Loading -> {
                    // Handle loading state if necessary
                }

                is Resource.Success -> {
                    Timber.i(result.data.toString())
                    _items.value = Resource.Success(result.data)
                }
            }
        }
    }

    /**
     * Sets the currently selected item.
     *
     * @param item The item to set.
     */
    fun setItem(item: Item) {
        _item.value = item
    }

    /**
     * Sets the bitmap image.
     *
     * @param bitmap The bitmap image to set.
     */
    fun setBitmap(bitmap: Bitmap) {
        _bitmap.value = bitmap
    }

    /**
     * Retrieves the currently selected item.
     *
     * @return The currently selected item.
     */
    fun getItem() = item.value

    /**
     * Retrieves the bitmap image.
     *
     * @return The bitmap image.
     */
    fun getBitmap() = bitmap.value

    /**
     * Define clearData method to reset ViewModel data
     */
    fun clearData() {
        _item.value = null
        _bitmap.value = null
    }

}