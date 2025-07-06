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
package com.casecode.pos.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.casecode.pos.core.model.data.business.Subscription
import com.casecode.pos.core.model.data.business.SubscriptionStatus
import kotlinx.datetime.Instant

@Entity(tableName = "subscription")
data class SubscriptionEntity(
    @PrimaryKey
    @ColumnInfo(name = "plan_id") val planId: String,
    @ColumnInfo(name = "plan_name") val planName: String,
    val status: Int,
    @ColumnInfo(name = "credit_balance") val creditBalance: Long,
    @ColumnInfo(name = "current_period_end_date") val currentPeriodEndDate: Instant?,
    @ColumnInfo(name = "updated_at") val updatedAt: Instant?,
    @ColumnInfo(name = "is_synced") val isSynced: Boolean = false,
)

fun SubscriptionEntity.asExternalModel(): Subscription {
    return Subscription(
        planId = this.planId,
        planName = this.planName,
        status = SubscriptionStatus.fromValue(this.status),
        creditBalance = this.creditBalance,
        currentPeriodEndDate = this.currentPeriodEndDate,
        updatedAt = this.updatedAt,
    )
}

fun Subscription.asEntity(): SubscriptionEntity {
    return SubscriptionEntity(
        planId = this.planId,
        planName = this.planName,
        status = this.status.value,
        creditBalance = this.creditBalance,
        currentPeriodEndDate = this.currentPeriodEndDate,
        updatedAt = this.updatedAt,
    )
}