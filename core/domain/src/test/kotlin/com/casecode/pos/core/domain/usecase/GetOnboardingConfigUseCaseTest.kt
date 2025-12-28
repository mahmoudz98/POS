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

import com.casecode.pos.core.model.business.Currency
import com.casecode.pos.core.model.business.OnboardingConfig
import com.casecode.pos.core.model.business.PlanLimits
import com.casecode.pos.core.model.business.SubscriptionPlan
import com.casecode.pos.core.testing.repository.business.TestSubscriptionRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetOnboardingConfigUseCaseTest {
    private lateinit var testSubscriptionRepository: TestSubscriptionRepository
    private lateinit var getOnboardingConfigUseCase: GetOnboardingConfigUseCase

    @Before
    fun setup() {
        testSubscriptionRepository = TestSubscriptionRepository()
        getOnboardingConfigUseCase = GetOnboardingConfigUseCase(testSubscriptionRepository)
    }

    @Test
    fun configAvailable_ReturnsConfig() = runTest {
        val config = OnboardingConfig(
            plans = listOf(
                SubscriptionPlan(
                    id = "basic",
                    nameEn = "Basic",
                    nameAr = "أساسي",
                    isFree = true,
                    featuresEn = emptyList(),
                    featuresAr = emptyList(),
                    limits = PlanLimits(1, 1, 100, 1000),
                ),
            ),
            currencies = listOf(
                Currency("USD", "US Dollar", "دولار أمريكي", "$", 2),
                Currency("EUR", "Euro", "يورو", "€", 2),
            ),
        )
        testSubscriptionRepository.setOnboardingConfig(config)

        val result = getOnboardingConfigUseCase()

        assertTrue(result.isSuccess)
        assertEquals(config, result.getOrNull())
    }

    @Test
    fun repositoryFailure_ReturnsFailure() = runTest {
        testSubscriptionRepository.setFailure(Exception("Network error"))

        val result = getOnboardingConfigUseCase()

        assertTrue(result.isFailure)
    }
}
