package com.casecode.domain.repository

import com.casecode.domain.model.users.Branch
import com.casecode.domain.model.users.Business
import com.casecode.domain.utils.Resource
import javax.inject.Singleton

typealias AddBusiness = Resource<Boolean>
typealias CompleteBusiness = Resource<Boolean>

interface BusinessRepository
{
   
   suspend fun getBusiness(): Resource<Business>
   suspend fun setBusiness(business: Business, uid: String): AddBusiness
   suspend fun completeBusinessSetup(uid: String):CompleteBusiness
    suspend fun addBranch(branch: Branch):Resource<Boolean>
}