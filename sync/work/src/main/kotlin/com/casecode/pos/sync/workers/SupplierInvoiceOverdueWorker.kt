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
import com.casecode.pos.core.common.AppDispatchers.IO
import com.casecode.pos.core.common.Dispatcher
import com.casecode.pos.core.domain.usecase.GetSupplierInvoicesOverdueUseCase
import com.casecode.pos.core.notifications.Notifier
import com.casecode.pos.sync.initializers.SyncConstraints
import com.casecode.pos.sync.initializers.supplierInvoiceOverdueForegroundInfo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.TimeUnit

@HiltWorker
internal class SupplierInvoiceOverdueWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val getSupplierInvoicesOverdueUseCase: GetSupplierInvoicesOverdueUseCase,
    private val notifier: Notifier,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun getForegroundInfo(): ForegroundInfo = appContext
        .supplierInvoiceOverdueForegroundInfo()

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        traceAsync("SupplierInvoiceOverdue", 0) {
            try {
                val overdueInvoices = getSupplierInvoicesOverdueUseCase()
                Timber.d("overdueInvoices: $overdueInvoices")
                if (overdueInvoices.isNotEmpty()) {
                    notifier.postOverdueNotifications(overdueInvoices)
                }
                Result.success()
            } catch (e: Exception) {
                Timber.e("Error fetch supplier invoices overdue: ${e.message}")
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
         * The work is subject to network connectivity constraints defined by [SyncConstraints].
         *
         * @return A [androidx.work.PeriodicWorkRequest] configured for the overdue supplier invoice task.
         */
        fun startPeriodicSupplierInvoiceOverdueWork() = PeriodicWorkRequestBuilder<DelegatingWorker>(24, TimeUnit.HOURS)
            .setConstraints(SyncConstraints)
            .setInputData(SupplierInvoiceOverdueWorker::class.delegatedData())
            .build()
    }
}