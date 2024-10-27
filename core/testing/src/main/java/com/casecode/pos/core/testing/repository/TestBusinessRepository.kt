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
package com.casecode.pos.core.testing.repository

import com.casecode.pos.core.domain.repository.AddBusiness
import com.casecode.pos.core.domain.repository.BusinessRepository
import com.casecode.pos.core.domain.repository.CompleteBusiness
import com.casecode.pos.core.domain.utils.AddBranchBusinessResult
import com.casecode.pos.core.domain.utils.BusinessResult
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
    var business: Business = Business()

    override suspend fun getBusiness(): BusinessResult {
        if (shouldReturnError) return BusinessResult.Error(-1)
        return BusinessResult.Success(business)
        // return business
    }

    override suspend fun setBusiness(business: Business): AddBusiness = Resource.Success(true)

    override suspend fun completeBusinessSetup(): CompleteBusiness = if (shouldReturnError) {
        Resource.error(-1)
    } else {
        Resource.success(true)
    }

    override suspend fun addBranch(branch: Branch): AddBranchBusinessResult {
        if (shouldReturnError) return AddBranchBusinessResult.Error(-1)

        return AddBranchBusinessResult.Success
    }

    fun sendAddBusiness(business: Business) {
        this.business = business
    }

    override fun init() {
    }
}