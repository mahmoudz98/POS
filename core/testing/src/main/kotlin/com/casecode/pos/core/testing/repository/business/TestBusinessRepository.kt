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
package com.casecode.pos.core.testing.repository.business

import com.casecode.pos.core.domain.repository.business.BusinessRepository
import com.casecode.pos.core.model.business.BillingEvent
import com.casecode.pos.core.model.business.Branch
import com.casecode.pos.core.model.business.Business
import com.casecode.pos.core.model.business.BusinessStatus
import com.casecode.pos.core.model.business.Subscription
import com.casecode.pos.core.model.business.TaxRate
import com.casecode.pos.core.model.business.Vertical
import com.casecode.pos.core.testing.base.TestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Clock

@Singleton
class TestBusinessRepository @Inject constructor() : TestRepository(), BusinessRepository {
    val testBusiness = Business(
        id = "business123",
        name = "Test Business",
        ownerUid = "owner123",
        vertical = Vertical.RETAIL,
        companyCode = "bus-BUBU",
        currencyCode = "USD",
        status = BusinessStatus.ACTIVE,
        email = "test@business.com",
        phone = "1234567890",
        updatedAt = Clock.System.now(),
        createdAt = Clock.System.now(),
    )
    private val _businessFlow = MutableStateFlow<Business?>(null)

    override suspend fun createInitialBusiness(
        business: Business,
        initialBranches: List<Branch>,
        initialTaxes: List<TaxRate>,
        initialSubscription: Subscription,
        initialBillingEvent: BillingEvent,
    ): Result<String> {
        getFailureResult<String>()?.let { return it }
        val currentBusiness = _businessFlow.value
        if (currentBusiness?.ownerUid == business.ownerUid) {
            return Result.failure(Exception("A business already exists for this owner."))
        }

        val newId = UUID.randomUUID().toString()
        val newBusiness = business.copy(id = newId)
        _businessFlow.value = newBusiness // Update the flow when a business is created
        return Result.success(newId)
    }

    override suspend fun findBusinessByOwner(ownerUid: String): Result<Business?> {
        if (failureThrowable != null)return Result.failure(failureThrowable!!)
        return Result.success(_businessFlow.value)
    }

    override suspend fun getBusinessByCompanyCode(companyCode: String): Result<Business?> {
        getFailureResult<Business?>()?.let { return it }
        val exists = _businessFlow.first().takeIf { it?.companyCode == companyCode }
        return Result.success(exists)
    }

    infix fun addBusiness(business: Business?) {
        _businessFlow.value = business
    }

    override fun clear() {
        _businessFlow.value = null
        returnSuccess()
    }

    override suspend fun syncUp(): Boolean {
        return true
    }

    override suspend fun syncDown(): Boolean {
        return true
    }
}
