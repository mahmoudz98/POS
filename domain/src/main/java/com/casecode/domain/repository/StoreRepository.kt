package com.casecode.domain.repository

import com.casecode.domain.utils.Resource
import com.casecode.domain.model.stores.Store
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton


typealias Stores = List<Store>
typealias StoresResponse = Resource<Stores>
@Singleton
 interface StoreRepository {
    fun getStores(): Flow<StoresResponse>


}