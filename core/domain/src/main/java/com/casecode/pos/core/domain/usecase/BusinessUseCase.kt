/*
 * Designed and developed 2024 by Mahmood Abdalhafeez
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.casecode.pos.core.domain.usecase

import com.casecode.pos.core.domain.R
import com.casecode.pos.core.domain.repository.AddBusiness
import com.casecode.pos.core.domain.repository.BusinessRepository
import com.casecode.pos.core.domain.repository.CompleteBusiness
import com.casecode.pos.core.domain.utils.AddBranchBusinessResult
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Branch
import com.casecode.pos.core.model.data.users.Business
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SetBusinessUseCase
@Inject
constructor(
    private val businessRep: BusinessRepository,
) {
    operator fun invoke(business: Business): Flow<AddBusiness> = flow {
        emit(Resource.loading())

        if (business.branches.isEmpty()) {
            emit(Resource.empty(R.string.core_domain_branches_empty))
            return@flow
        }
        if (business.storeType?.name.isNullOrBlank()) {
            emit(Resource.empty(R.string.core_domain_store_type_business_empty))
            return@flow
        }
        if (business.phone?.isEmpty() == true) {
            emit(Resource.empty(R.string.core_domain_phone_business_empty))
            return@flow
        }
        if (business.email?.isEmpty() == true) {
            emit(Resource.empty(R.string.core_domain_email_business_empty))
            return@flow
        }
        // If all validations pass, proceed to save the business
        emit(businessRep.setBusiness(business))
    }
}

class CompleteBusinessUseCase
@Inject
constructor(
    private val businessRepo: BusinessRepository,
) {
    suspend operator fun invoke(): CompleteBusiness = businessRepo.completeBusinessSetup()
}

class GetBusinessUseCase
@Inject
constructor(
    private val businessRep: BusinessRepository,
) {
    suspend operator fun invoke() = businessRep.getBusiness()
}

class AddBranchBusinessUseCase
@Inject
constructor(
    private val businessRep: BusinessRepository,
) {
    suspend operator fun invoke(branch: Branch): AddBranchBusinessResult {
        if (branch.branchCode == -1) {
            return AddBranchBusinessResult.Error(R.string.core_domain_branch_code_empty)
        }

        return businessRep.addBranch(branch)
    }
}