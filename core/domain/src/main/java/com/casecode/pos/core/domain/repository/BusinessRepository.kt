package com.casecode.pos.core.domain.repository

import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Branch
import com.casecode.pos.core.model.data.users.Business

typealias AddBusiness = Resource<Boolean>
typealias CompleteBusiness = Resource<Boolean>

interface BusinessRepository {
    suspend fun getBusiness(): Resource<Business>
    suspend fun setBusiness(business: Business): AddBusiness
    suspend fun addBranch(branch: Branch): Resource<Boolean>
    suspend fun completeBusinessSetup(): CompleteBusiness
}