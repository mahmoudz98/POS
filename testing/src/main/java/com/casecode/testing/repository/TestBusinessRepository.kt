package com.casecode.testing.repository

import com.casecode.domain.model.users.Branch
import com.casecode.domain.model.users.Business
import com.casecode.domain.repository.AddBusiness
import com.casecode.domain.repository.BusinessRepository
import com.casecode.domain.repository.CompleteBusiness
import com.casecode.domain.utils.Resource
import com.casecode.testing.base.BaseTestRepository
import javax.inject.Inject

class TestBusinessRepository @Inject constructor() : BusinessRepository, BaseTestRepository() {

    private var business: Business = Business()


    override suspend fun getBusiness(): Resource<Business> {
       if(shouldReturnError) return Resource.error("s")
        return Resource.success(business)
        // return business
    }

    override suspend fun setBusiness(business: Business, uid: String): AddBusiness {

        return Resource.Success(true)

    }

    override suspend fun completeBusinessSetup(uid: String): CompleteBusiness {
        return Resource.success(true)
    }

    override suspend fun addBranch(branch: Branch): Resource<Boolean> {
        TODO("Not yet implemented")
    }

    fun sendAddBusiness(business: Business) {

        this.business = business
    }

    override fun init() {

    }

}