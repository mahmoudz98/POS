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
package com.casecode.pos.core.model.data.business

import kotlinx.serialization.Serializable

@Serializable
data class PlanLimits(
    val maxBranches: Int,
    val maxEmployees: Int,
    val maxItems: Int,
    val initialCredits: Long,
)

@Serializable
data class SubscriptionPlan(
    val id: String,
    val name: String,
    val price: Double,
    val isFree: Boolean,
    val features: List<String>,
    val limits: PlanLimits,
)