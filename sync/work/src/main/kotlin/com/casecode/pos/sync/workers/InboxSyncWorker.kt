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
package com.casecode.pos.sync.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import com.casecode.pos.core.database.dao.LocalSignalDao
import com.casecode.pos.core.database.model.LocalSignalEntity
import com.casecode.pos.core.datastore.PosPreferencesDataSource
import com.casecode.pos.core.firebase.datasource.InboxNetworkDataSource
import com.casecode.pos.core.model.LoginStateResult
import com.casecode.pos.core.model.SyncableEntityType
import com.casecode.pos.sync.initializers.SyncConstraints
import com.casecode.pos.sync.initializers.syncForegroundInfo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.util.concurrent.TimeUnit

@HiltWorker
class InboxSyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val preferencesDataSource: PosPreferencesDataSource,
    private val inboxNetworkDataSource: InboxNetworkDataSource,
    private val localSignalDao: LocalSignalDao,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun getForegroundInfo(): ForegroundInfo =
        appContext.syncForegroundInfo()

    override suspend fun doWork(): Result {
        // This worker will run long-term, collecting the login state.
        preferencesDataSource.sessionData.collect { loginState ->
            Timber.e("inboxWork:loginData:$loginState")
            val businessId = when (loginState) {
                is LoginStateResult.OwnerLoggedIn -> loginState.businessId
                is LoginStateResult.EmployeeLoggedIn -> loginState.businessId
                else -> null
            }
            Timber.e("inboxWork:businessId:$businessId")

            if (businessId != null) {
                listenForAndQueueInboxSignals(businessId)
            }
        }

        return Result.success()
    }

    private suspend fun listenForAndQueueInboxSignals(businessId: String) {
        inboxNetworkDataSource.listenForSyncSignals(businessId).collect { (signalId, signal) ->

            // 1. Save the signal to the local queue
            localSignalDao.insertSignal(
                LocalSignalEntity(
                    id = signalId,
                    entityType = SyncableEntityType.fromValue(signal.entityType),
                    entityId = signal.entityId,
                ),
            )
        }
    }
    companion object {
        /**
         * Expedited one time work to sync data on app startup
         */
        fun startUpSyncWork() = PeriodicWorkRequestBuilder<DelegatingWorker>(15, TimeUnit.MINUTES)
            .setConstraints(SyncConstraints)
            .setInputData(InboxSyncWorker::class.delegatedData())
            .build()
    }
}
