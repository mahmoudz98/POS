package com.casecode.domain.model.stores

import com.casecode.domain.model.users.Item


data class Store(
     var basicItems: MutableList<Item>? = null,
     val storeCode: Long? = null,
     val storeType: String? = null
) {

}