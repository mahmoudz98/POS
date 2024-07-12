package com.casecode.pos.core.domain.usecase

import com.casecode.pos.core.domain.R
import com.casecode.pos.core.domain.repository.AddBusiness
import com.casecode.pos.core.domain.repository.BusinessRepository
import com.casecode.pos.core.domain.repository.CompleteBusiness
import com.casecode.pos.core.domain.utils.EmptyType
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Branch
import com.casecode.pos.core.model.data.users.Business
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class SetBusinessUseCase @Inject constructor(private val businessRep: BusinessRepository) {
    suspend operator fun invoke(business: Business): Flow<AddBusiness> = flow {
        emit(Resource.loading())

        if (business.branches.isEmpty()) {
            emit(Resource.empty(EmptyType.DATA, R.string.branches_empty))
            return@flow
        }
        if (business.storeType?.name.isNullOrBlank()) {
            emit(Resource.empty(EmptyType.DATA, R.string.store_type_business_empty))
            return@flow
        }
        if (business.phone?.isEmpty() == true) {
            emit(Resource.empty(EmptyType.DATA, R.string.phone_business_empty))
            return@flow
        }
        if (business.email?.isEmpty() == true) {
            emit(Resource.empty(EmptyType.DATA, R.string.email_business_empty))
            return@flow
        }

        // If all validations pass, proceed to save the business
        emit(businessRep.setBusiness(business))
    }
}

class CompleteBusinessUseCase @Inject constructor(private val businessRepo: BusinessRepository) {
    suspend operator fun invoke(): CompleteBusiness {

        return businessRepo.completeBusinessSetup()
    }
}

class GetBusinessUseCase @Inject constructor(private val businessRep: BusinessRepository) {

    suspend operator fun invoke(): Flow<Resource<Business>> {
        return flow {
            emit(Resource.loading())
            emit(businessRep.getBusiness())
        }
    }
}

class AddBranchBusinessUseCase @Inject constructor(private val businessRep: BusinessRepository) {
    suspend operator fun invoke(branch: Branch) = businessRep.addBranch(branch)

}