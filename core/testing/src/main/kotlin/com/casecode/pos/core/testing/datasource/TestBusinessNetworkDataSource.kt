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
package com.casecode.pos.core.testing.datasource

import com.casecode.pos.core.firebase.datasource.BusinessNetworkDataSource
import com.casecode.pos.core.firebase.model.NetworkBillingEvent
import com.casecode.pos.core.firebase.model.NetworkBranch
import com.casecode.pos.core.firebase.model.NetworkBusiness
import com.casecode.pos.core.firebase.model.NetworkSubscription
import com.casecode.pos.core.firebase.model.NetworkTaxRate
import java.util.UUID

class TestBusinessNetworkDataSource : BusinessNetworkDataSource {

    private val businesses = mutableMapOf<String, NetworkBusiness>()
    private val branches = mutableListOf<NetworkBranch>()

    // Test configuration state
    private var networkError: Throwable? = null

    override suspend fun createInitialBusiness(
        business: NetworkBusiness,
        initialBranches: List<NetworkBranch>,
        initialTaxes: List<NetworkTaxRate>,
        initialSubscription: NetworkSubscription,
        initialBillingEvent: NetworkBillingEvent,
    ): NetworkBusiness {
        networkError?.let { throw it }

        val newId = UUID.randomUUID().toString()
        val newBusiness = business.copy(id = newId)
        businesses[newId] = newBusiness
        return newBusiness
    }

    override suspend fun findBusinessByOwner(ownerUid: String): NetworkBusiness? {
        networkError?.let { throw it }

        return businesses.values.find { it.ownerUid == ownerUid }
    }

    override suspend fun getBusinessByCompanyCode(companyCode: String): NetworkBusiness? {
        networkError?.let { throw it }
        return businesses.values.find { it.companyCode == companyCode }
    }

    override suspend fun getBranches(businessId: String): List<NetworkBranch> {
        networkError?.let { throw it }

        return branches
    }

    override suspend fun addBranch(businessId: String, branch: NetworkBranch): String {
        networkError?.let { throw it }
        branches.add(branch)
        return ""
    }

    fun addNetworkBusiness(business: NetworkBusiness) {
        businesses[business.id] = business
    }
    fun setBranches(businessId: String, branches: List<NetworkBranch>) {
        this.branches.addAll(branches)
    }

    fun setNetworkError(error: Throwable) {
        networkError = error
    }

    fun clearNetworkError() {
        networkError = null
    }

    fun reset() {
        businesses.clear()
        networkError = null
    }
}
