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
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import com.casecode.pos.core.domain.service.LogService
import com.casecode.pos.core.domain.utils.Syncable
import com.casecode.pos.sync.initializers.SyncConstraints
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

@HiltWorker
class OutboxSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncables: Set<@JvmSuppressWildcards Syncable>,
    private val logService: LogService,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        logService.log("SyncWorker: Starting full sync process.")

        try {
            logService.log("SyncWorker: Starting syncUp phase.")
            val syncUpResults = syncables.map {
                async { it.syncUp() }
            }.awaitAll()

            if (syncUpResults.any { !it }) {
                logService.log("SyncWorker: syncUp phase failed for at least one repository. Retrying.")
                return@withContext Result.retry()
            }

            logService.log("SyncWorker: Starting syncDown phase.")
            val syncDownResults = syncables.map {
                async { it.syncDown() }
            }.awaitAll()

            if (syncDownResults.all { it }) {
                logService.log("SyncWorker: Full sync completed successfully.")
                Result.success()
            } else {
                logService.log("SyncWorker: syncDown phase failed for at least one repository. Retrying.")
                Result.retry()
            }
        } catch (e: Exception) {
            logService.log("SyncWorker: Sync failed with a top-level exception: ${e.message}")
            Result.retry()
        }
    }

    companion object {
        /**
         * Expedited one time work to sync data on app startup
         */
        fun startUpSyncWork() = OneTimeWorkRequestBuilder<DelegatingWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setConstraints(SyncConstraints)
            .setInputData(OutboxSyncWorker::class.delegatedData())
            .build()
    }
}