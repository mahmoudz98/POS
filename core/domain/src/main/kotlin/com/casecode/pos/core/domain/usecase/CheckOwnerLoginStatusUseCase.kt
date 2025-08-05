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

import com.casecode.pos.core.domain.repository.business.BusinessRepository
import com.casecode.pos.core.domain.service.LogService
import com.casecode.pos.core.domain.utils.OwnerLoginStatus
import com.casecode.pos.core.model.business.BusinessStatus
import com.casecode.pos.core.model.users.User
import javax.inject.Inject

class CheckOwnerLoginStatusUseCase @Inject constructor(
    private val businessRepository: BusinessRepository,
    private val logService: LogService,
) {
    suspend operator fun invoke(user: User): Result<OwnerLoginStatus> {
        logService.log("Checking login status for owner: ${user.uid}")

        return try {
            val business = businessRepository.findBusinessByOwner(user.uid).getOrThrow()

            val status = when (business?.status) {
                // Case 1: Business exists and is ACTIVE. Login is successful.
                BusinessStatus.ACTIVE -> {
                    logService.log("Status: Business is ACTIVE for businessId: ${business.id}")
                    OwnerLoginStatus.Active(business)
                }
                // Case 2: Business exists but is still pending. User must complete the wizard.
                BusinessStatus.PENDING_ONBOARDING -> {
                    logService.log("Status: Onboarding is PENDING for businessId: ${business.id}")
                    OwnerLoginStatus.OnboardingPending(business)
                }
                // Case 3: Business might be inactive or suspended. For login, we treat this as
                // something to be handled, but not a successful login. Let's send them to an error/info screen.
                // For simplicity here, we can route them back to the wizard or show a message.
                BusinessStatus.INACTIVE, BusinessStatus.SUSPENDED -> {
                    logService.log("Status: Business is ${business.status} for businessId: ${business.id}")
                    // This needs a product decision. Let's assume they need to contact support or re-activate.
                    // For now, let's treat it as incomplete onboarding.
                    OwnerLoginStatus.OnboardingPending(business)
                }
                // Case 4: No business document exists for this authenticated user.
                null -> {
                    logService.log("Status: No business found. User needs onboarding.")
                    OwnerLoginStatus.NeedsOnboarding
                }
            }
            Result.success(status)
        } catch (e: Exception) {
            logService.logNonFatalCrash(e)
            Result.failure(e)
        }
    }
}