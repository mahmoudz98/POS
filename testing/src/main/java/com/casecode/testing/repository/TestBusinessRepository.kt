package com.casecode.testing.repository

import com.casecode.domain.model.users.Business
import com.casecode.domain.repository.AddBusiness
import com.casecode.domain.repository.BusinessRepository
import com.casecode.domain.utils.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestBusinessRepository @Inject constructor(): BusinessRepository
{
   
   private var business: Business = Business()
   
   
   override suspend fun getBusiness(uid: String): Business
   {
      
      return business
   }
   
   override suspend fun setBusiness(business: Business, uid: String): AddBusiness
   {
    
      return   Resource.Success(true)
      
   }
   
   fun sendAddBusiness(business: Business)
   {
      
      this.business = business
   }
}