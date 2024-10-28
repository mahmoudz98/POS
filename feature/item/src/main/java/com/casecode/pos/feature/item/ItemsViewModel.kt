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
package com.casecode.pos.feature.item

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.pos.core.data.utils.NetworkMonitor
import com.casecode.pos.core.domain.usecase.AddItemUseCase
import com.casecode.pos.core.domain.usecase.DeleteItemUseCase
import com.casecode.pos.core.domain.usecase.GetItemsUseCase
import com.casecode.pos.core.domain.usecase.ItemImageUseCase
import com.casecode.pos.core.domain.usecase.UpdateItemUseCase
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.text.isNotBlank
import com.casecode.pos.core.ui.R.string as uiString

/**
 * ViewModel for the Items screen.
 * This ViewModel is responsible for managing the UI state of the Items screen,
 * including fetching items, filtering, searching, and handling user interactions.
 * It also handles network connectivity and displays appropriate messages to the user.
 *
 * @param savedStateHandle A SavedStateHandle to persist and restore UI state.
 * @param networkMonitor A NetworkMonitor to track network connectivity.
 * @param getItemsUseCase A UseCase to fetch items from the data source.
 * @param addItemsUseCase A UseCase to add a new item to the data source.
 * @param updateItemUseCase A UseCase to update an existing item in the data source.
 * @param deleteItemUseCase A UseCase to delete an item from the data source.
 * @param imageUseCase A UseCase to manage item images, including upload, replace, and delete.
 */
@HiltViewModel
class ItemsViewModel
@Inject
constructor(
    private val savedStateHandle: SavedStateHandle,
    networkMonitor: NetworkMonitor,
    getItemsUseCase: GetItemsUseCase,
    private val addItemsUseCase: AddItemUseCase,
    private val updateItemUseCase: UpdateItemUseCase,
    private val deleteItemUseCase: DeleteItemUseCase,
    private val imageUseCase: ItemImageUseCase,
) : ViewModel() {

    private val isOnline: StateFlow<Boolean> = networkMonitor.isOnline
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    var userMessage: MutableStateFlow<Int?> = MutableStateFlow(null)
        private set

    val searchQuery = savedStateHandle.getStateFlow(key = SEARCH_QUERY, initialValue = "")
    val searchWidgetState = savedStateHandle.getStateFlow(
        key = SEARCH_WIDGET_STATE,
        initialValue = SearchWidgetState.CLOSED,
    )
    val categoriesUiState: MutableStateFlow<Set<String>> = MutableStateFlow(emptySet())

    private val _filterUiState = MutableStateFlow(FilterUiState())
    val filterUiState = _filterUiState.asStateFlow()

    val itemsUiState: StateFlow<ItemsUIState> = combine(
        getItemsUseCase(),
        searchQuery,
        filterUiState,
    ) { itemsResource, searchText, filterState ->
        when (itemsResource) {
            is Resource.Loading -> ItemsUIState.Loading
            is Resource.Error -> {
                showSnackbarMessage(itemsResource.message as Int)
                ItemsUIState.Error
            }

            is Resource.Empty -> ItemsUIState.Empty
            is Resource.Success -> {
                val categories = itemsResource.data.mapNotNullTo(mutableSetOf()) {
                    it.category.takeIf { category -> category.isNotBlank() }
                }
                categoriesUiState.update { categories }
                processSuccessState(
                    items = itemsResource.data,
                    searchText = searchText,
                    filterState = filterState,
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = ItemsUIState.Loading,
    )

    private fun processSuccessState(
        items: List<Item>,
        searchText: String,
        filterState: FilterUiState,
    ): ItemsUIState {
        val itemsMap = items.associateBy { it.sku }

        val filteredItems = itemsMap.values
            .asSequence()
            .filter { item -> matchesSearchCriteria(item, searchText) }
            .filter { item -> matchesCategory(item, filterState.selectedCategories) }
            .filter { item -> matchesStockFilter(item, filterState.stockFilter) }
            .toList()
            .let { sortItemsByPrice(it, filterState.sortPrice) }

        return ItemsUIState.Success(
            items = itemsMap,
            filteredItems = filteredItems,
        )
    }

    private fun matchesSearchCriteria(item: Item, searchText: String): Boolean =
        searchText.isBlank() ||
                with(searchText.lowercase()) {
                    item.name.lowercase().contains(this) ||
                            item.sku.contains(this)
                }

    private fun matchesCategory(item: Item, selectedCategories: Set<String>): Boolean =
        selectedCategories.isEmpty() || item.category in selectedCategories

    private fun matchesStockFilter(item: Item, stockFilter: FilterStockState): Boolean =
        when (stockFilter) {
            FilterStockState.All -> true
            FilterStockState.InStock -> item.isInStockAndTracked()
            FilterStockState.OutOfStock -> !item.isInStockAndTracked()
            FilterStockState.LowLevelStock -> item.hasLowLevelStock()
        }

    private fun sortItemsByPrice(items: List<Item>, sortPrice: SortPriceState): List<Item> =
        when (sortPrice) {
            SortPriceState.LowToHigh -> items.sortedBy { it.unitPrice }
            SortPriceState.HighToLow -> items.sortedByDescending { it.unitPrice }
            SortPriceState.None -> items
        }

    private val _itemSelected = MutableStateFlow<Item?>(null)
    val itemSelected: StateFlow<Item?> = _itemSelected
    private val itemImageChanged = MutableStateFlow(false)

    fun closeSearchWidgetState() {
        savedStateHandle[SEARCH_WIDGET_STATE] = SearchWidgetState.CLOSED
    }

    fun openSearchWidgetState() {
        savedStateHandle[SEARCH_WIDGET_STATE] = SearchWidgetState.OPENED
    }

    fun onSearchQueryChanged(searchText: String) {
        savedStateHandle[SEARCH_QUERY] = searchText
    }

    fun onCategorySelected(category: String) {
        val selectedCategoriesMutable = filterUiState.value.selectedCategories.toMutableSet()
        selectedCategoriesMutable.add(category)
        _filterUiState.update { it.copy(selectedCategories = selectedCategoriesMutable) }
    }

    fun onCategoryUnselected(category: String) {
        val selectedCategoriesMutable = filterUiState.value.selectedCategories.toMutableSet()
        selectedCategoriesMutable.remove(category)
        _filterUiState.update { it.copy(selectedCategories = selectedCategoriesMutable) }
    }

    fun onSortFilterStockChanged(newStockFilter: FilterStockState) {
        _filterUiState.update { it.copy(stockFilter = newStockFilter) }
    }

    fun onSortPriceChanged(newPriceSort: SortPriceState) {
        _filterUiState.update { it.copy(sortPrice = newPriceSort) }
    }

    fun onClearFilter() {
        _filterUiState.update { FilterUiState() }
    }

    fun checkNetworkAndAddItem(targetItem: Item, imageBitmap: Bitmap?) {
        viewModelScope.launch {
            if (isOnline.value) {
                val isDuplicate = (itemsUiState.value as? ItemsUIState.Success)
                    ?.items
                    ?.containsKey(targetItem.sku) == true
                println("isDuplicate: $isDuplicate")
                if (isDuplicate) {
                    showSnackbarMessage(R.string.feature_item_error_duplicate)
                    return@launch
                }
                uploadImageAndAddItem(targetItem, imageBitmap)
            } else {
                showSnackbarMessage(uiString.core_ui_error_network)
            }
        }
    }

    private suspend fun uploadImageAndAddItem(
        targetItem: Item,
        imageBitmap: Bitmap?,
    ) {
        imageUseCase.uploadImage(bitmap = imageBitmap, imageName = targetItem.sku).collect {
            when (it) {
                is Resource.Loading -> {}
                is Resource.Error -> {
                    showSnackbarMessage(it.message as Int)
                }

                is Resource.Empty -> {
                    addItem(targetItem)
                }

                is Resource.Success -> {
                    addItem(targetItem.copy(imageUrl = it.data))
                }
            }
        }
    }

    private suspend fun addItem(targetItem: Item) {
        handleResponseAddAndUpdateItem(addItemsUseCase(targetItem))
    }

    fun updateItemImage() {
        this.itemImageChanged.value = true
    }

    fun checkNetworkAndUpdateItem(
        targetItem: Item,
        imageBitmap: Bitmap?,
    ) {
        if (isOnline.value) {

            checkItemImageChangeAndItemUpdate(targetItem, imageBitmap)
        } else {
            showSnackbarMessage(uiString.core_ui_error_network)
        }
    }

    private fun checkItemImageChangeAndItemUpdate(
        targetItem: Item,
        imageBitmap: Bitmap?,
    ) {
        if (itemImageChanged.value) {
            replaceImageAndUpdateItem(targetItem, imageBitmap)
            itemImageChanged.value = false
        } else {
            updateItem(targetItem)
        }
    }

    private fun replaceImageAndUpdateItem(
        targetItem: Item,
        imageBitmap: Bitmap?,
    ) {
        viewModelScope.launch {
            imageUseCase.replaceOrUploadImage(
                imageBitmap,
                itemSelected.value?.imageUrl,
                targetItem.sku,
            )
                .collect {
                    when (it) {
                        is Resource.Loading -> {}
                        is Resource.Error -> {
                            showSnackbarMessage(it.message as Int)
                        }

                        is Resource.Success -> {
                            updateItem(targetItem.copy(imageUrl = it.data))
                        }

                        is Resource.Empty -> {
                            updateItem(targetItem.copy(imageUrl = itemSelected.value?.imageUrl))
                        }
                    }
                }
        }
    }

    private fun updateItem(
        targetItem: Item,
    ) {
        viewModelScope.launch {
            val previousItem = itemSelected.value
            if (previousItem?.name != targetItem.name ||
                previousItem.unitPrice != targetItem.unitPrice ||
                previousItem.quantity != targetItem.quantity ||
                previousItem.imageUrl != targetItem.imageUrl
            ) {
                handleResponseAddAndUpdateItem(updateItemUseCase(targetItem))
            } else {
                showSnackbarMessage(R.string.feature_item_error_update_item_message)
            }
        }
    }

    private fun handleResponseAddAndUpdateItem(deleteResult: Resource<Int>) {
        when (deleteResult) {
            is Resource.Loading -> {}
            is Resource.Error, is Resource.Empty -> {
                showSnackbarMessage((deleteResult as Resource.Error).message as Int)
            }

            is Resource.Success -> {
                showSnackbarMessage(deleteResult.data)
            }
        }
    }

    fun checkNetworkAndDeleteItem() {
        if (isOnline.value) {
            val targetItem =
                _itemSelected.value
                    ?: return showSnackbarMessage(uiString.core_ui_error_unknown)
            deleteImageAndDeleteItem(targetItem)
        } else {
            showSnackbarMessage(uiString.core_ui_error_network)
        }
    }

    private fun deleteImageAndDeleteItem(targetItem: Item) {
        viewModelScope.launch {
            imageUseCase.deleteImage(imageUrl = targetItem.imageUrl).collect {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        showSnackbarMessage(it.message as Int)
                        deleteItem(targetItem)
                    }

                    is Resource.Success, is Resource.Empty -> {
                        deleteItem(targetItem)
                    }
                }
            }
        }
    }

    private fun deleteItem(targetItem: Item) {
        viewModelScope.launch {
            handleResponseDeleteItem(deleteItemUseCase(targetItem))
        }
    }

    private fun handleResponseDeleteItem(deleteResult: Resource<Int>) {
        when (deleteResult) {
            is Resource.Loading -> {}
            is Resource.Error -> {
                showSnackbarMessage(deleteResult.message as Int)
            }

            is Resource.Success, is Resource.Empty -> {
                showSnackbarMessage((deleteResult as Resource.Success).data)
            }
        }
    }

    fun setItemSelected(targetItem: Item) {
        _itemSelected.value = targetItem
    }

    fun snackbarMessageShown() {
        userMessage.value = null
    }

    fun showSnackbarMessage(messageResource: Int) {
        userMessage.value = messageResource
    }
}

private const val SEARCH_QUERY = "searchQuery"
private const val SEARCH_WIDGET_STATE = "searchWidgetState"