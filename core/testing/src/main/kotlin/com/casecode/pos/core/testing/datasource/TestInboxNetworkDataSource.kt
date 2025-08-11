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

import com.casecode.pos.core.firebase.datasource.InboxNetworkDataSource
import com.casecode.pos.core.firebase.model.NetworkInboxSignal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class TestInboxNetworkDataSource : InboxNetworkDataSource {

    private val signalsFlow = MutableSharedFlow<Pair<String, NetworkInboxSignal>>()
    val postedSignals = mutableListOf<NetworkInboxSignal>()

    override fun listenForSyncSignals(businessId: String): Flow<Pair<String, NetworkInboxSignal>> {
        return signalsFlow.asSharedFlow()
    }

    override suspend fun postSignal(businessId: String, signal: NetworkInboxSignal): Result<Unit> {
        postedSignals.add(signal)
        return Result.success(Unit)
    }
}