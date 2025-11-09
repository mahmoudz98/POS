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

import com.casecode.pos.core.domain.repository.business.CurrencyRepository
import com.casecode.pos.core.model.business.Currency
import com.casecode.pos.core.testing.base.TestRepository
import javax.inject.Inject

class TestCurrencyRepository @Inject constructor() : TestRepository(), CurrencyRepository {

    private var supportedCurrencies: List<Currency> = emptyList()

    override suspend fun getSupportedCurrencies(): Result<List<Currency>> {
        getFailureResult<List<Currency>>()?.let { return it }
        return Result.success(supportedCurrencies)
    }

    fun setSupportedCurrencies(currencies: List<Currency>) {
        this.supportedCurrencies = currencies
    }

    override fun clear() {
        supportedCurrencies = emptyList()
        returnSuccess()
    }
}
