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
package com.casecode.pos.sync.initializers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import com.casecode.pos.sync.R

const val OVERDUE_SUPPLIER_INVOICES = "overdue_supplier_invoices"
private const val OVERDUE_NOTIFICATION_ID = 0
private const val OVERDUE_NOTIFICATION_CHANNEL_ID = "OverdueNotifications"

// All sync work needs an internet connectionS
val SyncConstraints
    get() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

/**
 * Foreground information for supplierInvoiceOverdue on lower API levels when sync workers are being
 * run with a foreground service
 */
fun Context.supplierInvoiceOverdueForegroundInfo() = ForegroundInfo(
    OVERDUE_NOTIFICATION_ID,
    supplierInvoiceOverdueWorkNotification(),
)

/**
 * Notification displayed on lower API levels when supplierInvoiceOverdue workers are being
 * run with a foreground service
 */
private fun Context.supplierInvoiceOverdueWorkNotification(): Notification {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            OVERDUE_NOTIFICATION_CHANNEL_ID,
            getString(R.string.sync_work_supplier_invoice_overdue_notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = getString(
                R.string.sync_work_supplier_invoice_overdue_notification_channel_description,
            )
        }
        // Register the channel with the system
        val notificationManager: NotificationManager? =
            getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

        notificationManager?.createNotificationChannel(channel)
    }

    return NotificationCompat.Builder(
        this,
        OVERDUE_NOTIFICATION_CHANNEL_ID,
    )
        .setSmallIcon(
            com.casecode.pos.core.notifications.R.drawable.core_notifications_ic_pos,
        )
        .setContentTitle(getString(R.string.sync_work_notification_title))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()
}