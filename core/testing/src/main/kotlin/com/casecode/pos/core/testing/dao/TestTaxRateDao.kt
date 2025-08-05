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

import com.casecode.pos.core.database.dao.TaxRateDao
import com.casecode.pos.core.database.model.TaxRateEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TestTaxRateDao : TaxRateDao {

    private val taxRatesFlow = MutableStateFlow<List<TaxRateEntity>>(emptyList())
    val insertedTaxRates = mutableListOf<TaxRateEntity>()

    override fun getTaxRates(): Flow<List<TaxRateEntity>> {
        return taxRatesFlow.asStateFlow()
    }

    override suspend fun insertOrReplaceTaxRates(taxRates: List<TaxRateEntity>) {
        insertedTaxRates.addAll(taxRates)
        taxRatesFlow.value = taxRates
    }
}