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
package com.casecode.pos.core.testing.repository

import com.casecode.pos.core.domain.repository.SubscriptionsRepository
import com.casecode.pos.core.domain.repository.SubscriptionsResource
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.subscriptions.Subscription
import org.junit.Before
import javax.inject.Inject

class TestSubscriptionsRepository
@Inject
constructor() : SubscriptionsRepository {
    private var subscriptions: List<Subscription> = subscriptionsFake()

    private var shouldReturnError = false
    private var shouldReturnEmpty = false

    @Before
    fun setup() {
        shouldReturnError = false
        shouldReturnEmpty = false
    }

    /**
     * Gets a Flow of plans.
     *
     * @return A Flow of plans.
     */
    override suspend fun getSubscriptions(): SubscriptionsResource = if (shouldReturnError) {
        Resource.error("Error")
    } else if (shouldReturnEmpty) {
        Resource.empty("Empty")
    } else {
        Resource.success(subscriptions)
    }

    fun sendSubscriptions(subscriptions: List<Subscription>) {
        this.subscriptions = subscriptions
    }

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    fun setReturnEmpty(value: Boolean) {
        shouldReturnEmpty = value
    }

    private fun subscriptionsFake(): List<Subscription> =
        listOf(
            Subscription(
                duration = 30,
                cost = 0,
                type = "basic",
                permissions = listOf("write", "read", "admin"),
            ),
            Subscription(
                duration = 30,
                cost = 20,
                type = "pro",
                permissions = listOf("write", "read", "admin"),
            ),
            Subscription(
                duration = 90,
                cost = 60,
                type = "premium",
                permissions = listOf("write", "read", "admin"),
            ),
        )
}