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
package com.casecode.pos.core.testing.dao

import com.casecode.pos.core.database.dao.BusinessDao
import com.casecode.pos.core.database.model.BusinessEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class TestBusinessDao : BusinessDao {

    private val businessesFlow = MutableStateFlow<List<BusinessEntity>>(emptyList())

    var insertedBusiness: BusinessEntity? = null
        private set

    override fun getBusinessByOwner(ownerUid: String): Flow<BusinessEntity?> {
        return businessesFlow.asStateFlow().map { list ->
            list.find { it.ownerUid == ownerUid }
        }
    }

    override suspend fun insertOrReplaceBusiness(business: BusinessEntity) {
        insertedBusiness = business
        val currentList = businessesFlow.value.toMutableList()
        currentList.removeAll { it.businessId == business.businessId }
        currentList.add(business)
        businessesFlow.value = currentList
    }

    override suspend fun getBusinessByCompanyCode(companyCode: String): BusinessEntity? {
        // Return configured value if set, otherwise check actual businesses
        return businessesFlow.value.find { it.companyCode == companyCode }
    }

    override suspend fun deleteBusiness(businessId: String) {
        TODO("Not yet implemented")
    }

    fun reset() {
        businessesFlow.value = emptyList()
        insertedBusiness = null
    }
}
