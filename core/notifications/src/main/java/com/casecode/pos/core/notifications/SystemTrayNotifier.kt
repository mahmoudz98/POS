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
package com.casecode.pos.core.notifications

import android.Manifest.permission
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.InboxStyle
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.casecode.pos.core.model.data.users.SupplierInvoice
import com.casecode.pos.core.model.utils.toFormattedDateString
import com.casecode.pos.core.model.utils.toFormattedString
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val MAX_NUM_NOTIFICATIONS = 5
private const val TARGET_ACTIVITY_NAME = "com.casecode.pos.MainActivity"
private const val OVERDUE_NOTIFICATION_REQUEST_CODE = 0
private const val OVERDUE_NOTIFICATION_SUMMARY_ID = 1
private const val OVERDUE_NOTIFICATION_CHANNEL_ID = "OVERDUE_INVOICE_CHANNEL"
private const val OVERDUE_NOTIFICATION_GROUP = "OVERDUE_NOTIFICATIONS"
private const val DEEP_LINK_SCHEME_AND_HOST = "https://www.pos.casecode.com"
private const val DEEP_LINK_BILL_PATH = "bill"
private const val DEEP_LINK_BASE_PATH = "$DEEP_LINK_SCHEME_AND_HOST/$DEEP_LINK_BILL_PATH"
const val DEEP_LINK_SUPPLIER_INVOICE_OVERDUE_ID_KEY = "billId"
const val DEEP_LINK_URI_PATTERN =
    "$DEEP_LINK_BASE_PATH/{$DEEP_LINK_SUPPLIER_INVOICE_OVERDUE_ID_KEY}"

@Singleton
internal class SystemTrayNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
) : Notifier {
    override fun postOverdueNotifications(supplierInvoices: List<SupplierInvoice>) = with(context) {
        if (checkSelfPermission(this, permission.POST_NOTIFICATIONS) != PERMISSION_GRANTED) {
            return
        }

        val truncatedSupplierInvoices = supplierInvoices.take(MAX_NUM_NOTIFICATIONS)

        val supplierInvoicesNotifications = truncatedSupplierInvoices.map { invoice ->
            createSupplierInvoiceOverdueNotification {
                setSmallIcon(R.drawable.core_notifications_ic_pos)
                    .setContentTitle(
                        context.getString(
                            R.string.core_notifications_overdue_invoice_notification_title,
                            invoice.billNumber,
                        ),
                    )
                    .setContentText(
                        context.getString(
                            R.string.core_notifications_overdue_invoice_notification_content,
                            invoice.supplierName,
                        ),
                    )
                    .setStyle(supplierInvoiceOverdueNotificationStyle(invoice))
                    .setContentIntent(supplierInvoiceOverduePendingIntent(invoice))
                    .setGroup(OVERDUE_NOTIFICATION_GROUP)
                    .setAutoCancel(true)
            }
        }
        val summaryNotification = createSupplierInvoiceOverdueNotification {
            val title = getString(
                R.string.core_notifications_supplier_invoice_notification_group_summary,
                truncatedSupplierInvoices.size,
            )
            setContentTitle(title)
                .setContentText(title)
                .setSmallIcon(R.drawable.core_notifications_ic_pos)
                // Build summary info into InboxStyle template.
                .setStyle(
                    supplierInvoiceOverdueSummaryNotificationStyle(
                        truncatedSupplierInvoices,
                        title,
                    ),
                )
                .setGroup(OVERDUE_NOTIFICATION_GROUP)
                .setGroupSummary(true)
                .setAutoCancel(true)
                .build()
        }

        // Send the notifications
        val notificationManager = NotificationManagerCompat.from(this)
        supplierInvoicesNotifications.forEachIndexed { index, notification ->
            notificationManager.notify(
                truncatedSupplierInvoices[index].invoiceId.hashCode(),
                notification,
            )
        }
        notificationManager.notify(OVERDUE_NOTIFICATION_SUMMARY_ID, summaryNotification)
    }

    /**
     * Creates an inbox style summary notification for supplier invoice overdue.
     * This style is used for the expanded notification view.
     */
    private fun supplierInvoiceOverdueSummaryNotificationStyle(
        supplierInvoicesOverdue: List<SupplierInvoice>,
        title: String,
    ): InboxStyle = supplierInvoicesOverdue.fold(InboxStyle()) { inboxStyle, invoice ->
        inboxStyle.addLine(invoice.billNumber)
    }
        .setBigContentTitle(title)
        .setSummaryText(title)

    /**
     * Creates an inbox style notification for supplier invoice overdue.
     * THis style is used for the expended notification view.
     */
    private fun supplierInvoiceOverdueNotificationStyle(
        supplierInvoicesOverdue: SupplierInvoice,
    ): InboxStyle = InboxStyle()
        .addLine(
            context.getString(
                R.string.core_notifications_supplier_invoice_notification_supplier_text,
                supplierInvoicesOverdue.supplierName,
            ),
        )
        .addLine(
            context.getString(
                R.string.core_notifications_supplier_invoice_notification_amount_due_text,
                supplierInvoicesOverdue.restDueAmount.toFormattedString(),
            ),
        )
        .addLine(
            context.getString(
                R.string.core_notifications_supplier_invoice_notification_due_date_text,
                supplierInvoicesOverdue.dueDate.toFormattedDateString(),
            ),
        )
        .addLine(
            context.getString(
                R.string.core_notifications_supplier_invoice_notification_status_overdue_text,
            ),
        )

    /**
     * Creates a notification for configured for supplier invoice overdue
     */
    private fun Context.createSupplierInvoiceOverdueNotification(
        block: NotificationCompat.Builder.() -> Unit,
    ): Notification {
        ensureNotificationChannelExists()
        return NotificationCompat.Builder(
            this,
            OVERDUE_NOTIFICATION_CHANNEL_ID,
        )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .apply(block)
            .build()
    }

    /**
     * Ensures that a notification channel is present if applicable
     */
    private fun Context.ensureNotificationChannelExists() {
        if (VERSION.SDK_INT < VERSION_CODES.O) return

        val channel = NotificationChannel(
            OVERDUE_NOTIFICATION_CHANNEL_ID,
            getString(R.string.core_notifications_supplier_invoice_notification_channel_overdue),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description =
                getString(
                    R.string.core_notifications_supplier_invoice_overdue_notification_channel_description,
                )
        }
        // Register the channel with the system
        NotificationManagerCompat.from(this).createNotificationChannel(channel)
    }

    private fun Context.supplierInvoiceOverduePendingIntent(
        supplierInvoice: SupplierInvoice,
    ): PendingIntent? = PendingIntent.getActivity(
        this,
        OVERDUE_NOTIFICATION_REQUEST_CODE,
        Intent().apply {
            action = Intent.ACTION_VIEW
            data = supplierInvoice.supplierInvoiceOverdueDeepLinkUri()
            component = ComponentName(
                packageName,
                TARGET_ACTIVITY_NAME,
            )
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    private fun SupplierInvoice.supplierInvoiceOverdueDeepLinkUri() = "$DEEP_LINK_BASE_PATH/$invoiceId".toUri()
}