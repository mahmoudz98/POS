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
package com.casecode.pos.core.data.repository.business

import com.casecode.pos.core.common.AppDispatchers
import com.casecode.pos.core.common.Dispatcher
import com.casecode.pos.core.domain.repository.business.CurrencyRepository
import com.casecode.pos.core.firebase.datasource.ConfigDataSource
import com.casecode.pos.core.model.data.business.Currency
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(
    private val configDataSource: ConfigDataSource,

    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : CurrencyRepository {

    /** In‑memory cache (invalidate whenever the app restarts or when you decide). */
    private var cached: List<Currency>? = null

    override suspend fun getSupportedCurrencies(): Result<List<Currency>> =
        withContext(ioDispatcher) {
            cached?.let { return@withContext Result.success(it) }

            runCatching {
                configDataSource.getSupportedCurrencies()
            }.onSuccess { cached = it }
                .onFailure { e -> e }
        }
}
