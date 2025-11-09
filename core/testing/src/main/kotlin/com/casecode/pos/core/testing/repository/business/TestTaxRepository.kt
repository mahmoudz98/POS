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

import com.casecode.pos.core.domain.repository.business.TaxRepository
import com.casecode.pos.core.model.business.TaxRate
import com.casecode.pos.core.testing.base.TestRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestTaxRepository @Inject constructor() : TestRepository(), TaxRepository {

    private val taxRatesByBusiness = mutableMapOf<String, MutableList<TaxRate>>()

    override suspend fun getTaxRates(businessId: String): Result<List<TaxRate>> {
        getFailureResult<List<TaxRate>>()?.let { return it }
        return Result.success(taxRatesByBusiness[businessId] ?: emptyList())
    }

    override suspend fun addTaxRate(businessId: String, taxRate: TaxRate): Result<String> {
        getFailureResult<String>()?.let { return it }

        val newId = taxRate.id!!.ifEmpty { UUID.randomUUID().toString() }
        val rateWithId = taxRate.copy(id = newId)
        val rates = taxRatesByBusiness.getOrPut(businessId) { mutableListOf() }
        rates.add(rateWithId)

        return Result.success(newId)
    }

    fun setTaxRatesForBusiness(businessId: String, taxRates: List<TaxRate>) {
        taxRatesByBusiness[businessId] = taxRates.toMutableList()
    }

    override fun clear() {
        taxRatesByBusiness.clear()
        returnSuccess()
    }
}
