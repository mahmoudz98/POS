package com.casecode.pos.core.testing.repository

import com.casecode.pos.core.domain.repository.AddBusiness
import com.casecode.pos.core.domain.repository.BusinessRepository
import com.casecode.pos.core.domain.repository.CompleteBusiness
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Branch
import com.casecode.pos.core.model.data.users.Business
import com.casecode.pos.core.testing.base.BaseTestRepository
import javax.inject.Inject

class TestBusinessRepository
    @Inject
    constructor() :
    BaseTestRepository(),
        BusinessRepository {
        private var business: Business = Business()

        override suspend fun getBusiness(): Resource<Business> {
            if (shouldReturnError) return Resource.error("s")
            return Resource.success(business)
            // return business
        }

        override suspend fun setBusiness(business: Business): AddBusiness = Resource.Success(true)

        override suspend fun completeBusinessSetup(): CompleteBusiness = Resource.success(true)

    override suspend fun addBranch(branch: Branch): Resource<Boolean> {
        TODO("Not yet implemented")
    }

    fun sendAddBusiness(business: Business) {
        this.business = business
    }

    override fun init() {
    }
}