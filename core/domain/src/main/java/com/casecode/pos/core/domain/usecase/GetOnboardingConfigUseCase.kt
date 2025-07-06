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

import com.casecode.pos.core.domain.repository.business.SubscriptionRepository
import com.casecode.pos.core.model.data.business.OnboardingConfig
import javax.inject.Inject

/**
 * Fetches the necessary configuration for the onboarding flow, including
 * available subscription plans and supported currencies from a remote source.
 */
class GetOnboardingConfigUseCase @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
) {
    suspend operator fun invoke(): Result<OnboardingConfig> {
        return subscriptionRepository.getOnboardingConfig()
    }
}