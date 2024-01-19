package com.casecode.testing.repository

import com.casecode.domain.model.stores.Store
import com.casecode.domain.repository.StoreRepository
import com.casecode.domain.repository.StoresResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestStoreRepository@Inject constructor(): StoreRepository
{
   var stores : List<Store> = mutableListOf()
   
   
   override fun getStores(): Flow<StoresResponse>
   {
      return flowOf()
   }
}