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
package com.casecode.pos.core.data.model

import com.casecode.pos.core.firebase.model.NetworkPlanLimits
import com.casecode.pos.core.firebase.model.NetworkPlanPrice
import com.casecode.pos.core.firebase.model.NetworkSubscriptionPlan
import com.casecode.pos.core.firebase.model.SupportedCurrency
import com.casecode.pos.core.model.business.Currency
import com.casecode.pos.core.model.business.PlanLimits
import com.casecode.pos.core.model.business.PlanPrice
import com.casecode.pos.core.model.business.SubscriptionPlan

fun SupportedCurrency.asExternalModel(): Currency = Currency(
    code = this.code,
    nameEn = this.nameEn,
    nameAr = this.nameAr,
    symbol = this.symbol,
    decimalPlaces = this.decimalPlaces,
)
fun NetworkSubscriptionPlan.asExternalModel(): SubscriptionPlan = SubscriptionPlan(
    id = this.id,
    nameEn = this.nameEn,
    nameAr = this.nameAr,
    prices = this.prices?.map { it.asExternalModel() },
    isFree = this.isFree,
    featuresEn = this.featuresEn,
    featuresAr = this.featuresAr,
    limits = this.limits.asExternalModel(),
)
fun NetworkPlanPrice.asExternalModel(): PlanPrice = PlanPrice(
    currency = this.currency,
    amount = this.amount,
)
fun NetworkPlanLimits.asExternalModel(): PlanLimits = PlanLimits(
    maxBranches = this.maxBranches,
    maxEmployees = this.maxEmployees,
    maxItems = this.maxItems,
    initialCredits = this.initialCredits,
)
