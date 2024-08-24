package com.casecode.pos.core.model.data.stores

import com.casecode.pos.core.model.data.users.Item

data class Store(
    var basicItems: MutableList<Item>? = null,
    val storeCode: Long? = null,
    val storeType: String? = null,
)