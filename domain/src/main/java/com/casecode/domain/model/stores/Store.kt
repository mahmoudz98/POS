package com.casecode.domain.model.stores

import com.casecode.domain.entity.BasicItem

data class Store(
     var basicItems: MutableList<BasicItem>? = null,
     val storeCode: Long? = null,
     val storeType: String? = null
) {

}