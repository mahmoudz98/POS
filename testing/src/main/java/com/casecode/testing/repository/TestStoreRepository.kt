package com.casecode.testing.repository

import com.casecode.domain.model.stores.Store
import com.casecode.domain.repository.StoreRepository
import com.casecode.domain.repository.StoresResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class TestStoreRepository: StoreRepository
{
   var stores : List<Store> = mutableListOf()
   
   
   override fun getStores(): Flow<StoresResponse>
   {
      return flowOf()
   }
}