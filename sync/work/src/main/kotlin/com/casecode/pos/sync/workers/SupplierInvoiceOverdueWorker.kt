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
import androidx.tracing.traceAsync
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import com.casecode.pos.core.analytics.AnalyticsHelper
import com.casecode.pos.core.common.AppDispatchers.IO
import com.casecode.pos.core.common.Dispatcher
import com.casecode.pos.core.domain.usecase.GetSupplierInvoicesOverdueUseCase
import com.casecode.pos.core.notifications.Notifier
import com.casecode.pos.sync.initializers.SyncSupplierInvoicesOverdueConstraints
import com.casecode.pos.sync.initializers.supplierInvoiceOverdueForegroundInfo
import com.casecode.pos.sync.logSyncSupplierInvoicesOverdueFinished
import com.casecode.pos.sync.logSyncSupplierInvoicesOverdueStarted
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.util.concurrent.TimeUnit
import kotlin.time.DurationUnit

@HiltWorker
internal class SupplierInvoiceOverdueWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val getSupplierInvoicesOverdueUseCase: GetSupplierInvoicesOverdueUseCase,
    private val notifier: Notifier,
    private val analyticsHelper: AnalyticsHelper,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun getForegroundInfo(): ForegroundInfo = appContext
        .supplierInvoiceOverdueForegroundInfo()

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        traceAsync("SupplierInvoiceOverdue", 0) {
            try {
                analyticsHelper.logSyncSupplierInvoicesOverdueStarted()
                val overdueInvoices = getSupplierInvoicesOverdueUseCase()
                analyticsHelper.logSyncSupplierInvoicesOverdueFinished(overdueInvoices.isNotEmpty())
                if (overdueInvoices.isNotEmpty()) {
                    notifier.postOverdueNotifications(overdueInvoices)
                }
                Result.success()
            } catch (e: Exception) {
                Result.retry()
            }
        }
    }

    companion object {
        /**
         * Creates a periodic work request for handling overdue supplier invoices.
         *
         * This function sets up a periodic work request using WorkManager to execute the
         * [SupplierInvoiceOverdueWorker] (delegated through [DelegatingWorker]) every 24 hours.
         * The work is subject to network connectivity constraints defined by [SyncSupplierInvoicesOverdueConstraints].
         *
         * @return A [androidx.work.PeriodicWorkRequest] configured for the overdue supplier invoice task.
         */
        fun startPeriodicOverdueWork() = PeriodicWorkRequestBuilder<DelegatingWorker>(
            24,
            TimeUnit.HOURS,
        )
            .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
            .setConstraints(SyncSupplierInvoicesOverdueConstraints)
            .setInputData(SupplierInvoiceOverdueWorker::class.delegatedData())
            .build()

        private fun calculateInitialDelay(): Long {
            val now = Clock.System.now()
            val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
            val targetTime = LocalDateTime(today, LocalTime(9, 0, 0))
            val targetInstant = targetTime.toInstant(TimeZone.currentSystemDefault())

            val delay = if (targetInstant > now) {
                targetInstant - now
            } else {
                val tomorrow = today.plus(1, DateTimeUnit.DAY)
                val tomorrowTargetTime = LocalDateTime(tomorrow, LocalTime(9, 0, 0))
                tomorrowTargetTime.toInstant(TimeZone.currentSystemDefault()) - now
            }

            return delay.toLong(DurationUnit.MILLISECONDS)
        }
    }
}