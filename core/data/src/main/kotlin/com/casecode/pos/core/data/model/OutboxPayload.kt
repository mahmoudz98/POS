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

import com.casecode.pos.core.firebase.model.NetworkBillingEvent
import com.casecode.pos.core.firebase.model.NetworkBranch
import com.casecode.pos.core.firebase.model.NetworkBusiness
import com.casecode.pos.core.firebase.model.NetworkSubscription
import com.casecode.pos.core.firebase.model.NetworkTaxRate
import kotlinx.serialization.Serializable

/**
 * Sealed class representing the different types of payloads for Outbox Commands.
 * This ensures type-safety when serializing and deserializing command data.
 */
@Serializable
sealed class OutboxPayload {
    /**
     * Payload for the CREATE_BUSINESS command.
     * Contains all data necessary to create a new business on the backend.
     */
    @Serializable
    data class CreateBusinessPayload(
        val business: NetworkBusiness,
        val initialBranches: List<NetworkBranch>,
        val initialTaxes: List<NetworkTaxRate>,
        val initialSubscription: NetworkSubscription,
        val initialBillingEvent: NetworkBillingEvent,
    ) : OutboxPayload()

    /**
     * Payload for the ADD_BRANCH command.
     * Contains data necessary to add a new branch to an existing business on the backend.
     */
    @Serializable
    data class AddBranchPayload(
        val businessId: String,
        val branch: NetworkBranch,
    ) : OutboxPayload()

    // TODO: Add other command payloads as needed
}