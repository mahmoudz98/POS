package com.casecode.pos.feature.item

import com.casecode.pos.core.model.data.users.Item

sealed interface ItemsUIState {
    data class Success(val items: List<Item>) : ItemsUIState
    data object Error : ItemsUIState
    data object Empty : ItemsUIState
    data object Loading : ItemsUIState
}
