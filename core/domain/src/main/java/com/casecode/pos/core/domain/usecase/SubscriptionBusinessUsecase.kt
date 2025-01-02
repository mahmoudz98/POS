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
package com.casecode.pos.core.domain.usecase

import com.casecode.pos.core.domain.R
import com.casecode.pos.core.domain.repository.AddSubscriptionBusiness
import com.casecode.pos.core.domain.repository.SubscriptionsBusinessRepository
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.SubscriptionBusiness
import javax.inject.Inject

class GetSubscriptionBusinessUseCase
@Inject
constructor(
    private val subscriptionsRep: SubscriptionsBusinessRepository,
) {
    operator fun invoke() = subscriptionsRep.getSubscriptionsBusiness()
}

class SetSubscriptionBusinessUseCase
@Inject
constructor(
    private val subscriptionsRep: SubscriptionsBusinessRepository,
) {
    suspend operator fun invoke(
        subscriptionBusiness: SubscriptionBusiness,
    ): AddSubscriptionBusiness {
        if (subscriptionBusiness.type.isNullOrEmpty()) {
            return Resource.empty(R.string.core_domain_add_subscription_business_empty)
        }

        return subscriptionsRep.setSubscriptionBusiness(subscriptionBusiness)
    }
}