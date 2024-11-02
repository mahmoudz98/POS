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

import com.casecode.pos.core.firebase.services.SUBSCRIPTION_COST_FIELD
import com.casecode.pos.core.firebase.services.SUBSCRIPTION_DURATION_FIELD
import com.casecode.pos.core.firebase.services.SUBSCRIPTION_PERMISSIONS_FIELD
import com.casecode.pos.core.firebase.services.SUBSCRIPTION_TYPE_FIELD
import com.casecode.pos.core.model.data.subscriptions.Subscription
import com.casecode.pos.core.model.data.users.SubscriptionBusiness
import com.google.firebase.firestore.DocumentSnapshot

/**
 * Created by Mahmoud Abdalhafeez
 */
fun MutableList<Subscription>.asEntitySubscriptions(document: DocumentSnapshot) {
    val type = document[SUBSCRIPTION_TYPE_FIELD] as String
    val duration = document[SUBSCRIPTION_DURATION_FIELD] as Long
    val cost = document[SUBSCRIPTION_COST_FIELD] as Long

    @Suppress("UNCHECKED_CAST")
    val permissions = document[SUBSCRIPTION_PERMISSIONS_FIELD] as List<String>
    add(Subscription(cost, duration, permissions, type))
}

fun Subscription.asSubscriptionBusiness(): SubscriptionBusiness =
    SubscriptionBusiness(type, cost, duration, permissions)