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

import com.casecode.pos.core.domain.exceptions.ValidationException
import com.casecode.pos.core.domain.repository.business.BusinessRepository
import com.casecode.pos.core.domain.service.LogService
import com.casecode.pos.core.model.PurchaseResult
import com.casecode.pos.core.model.data.business.BillingEvent
import com.casecode.pos.core.model.data.business.Branch
import com.casecode.pos.core.model.data.business.BranchStatus
import com.casecode.pos.core.model.data.business.Business
import com.casecode.pos.core.model.data.business.BusinessStatus
import com.casecode.pos.core.model.data.business.Subscription
import com.casecode.pos.core.model.data.business.SubscriptionPlan
import com.casecode.pos.core.model.data.business.TaxRate
import com.casecode.pos.core.model.data.business.Vertical
import kotlinx.datetime.Clock
import javax.inject.Inject

/**
 * A data class to cleanly transport all collected onboarding data to this use case.
 */
data class OnboardingData(
    val ownerUid: String,
    val businessName: String,
    val businessVertical: Vertical,
    val currencyCode: String,
    val selectedPlan: SubscriptionPlan,
    val paymentResult: PurchaseResult,
    val initialBranchName: String,
    val initialTaxRate: TaxRate,
)

/**
 * The main use case that orchestrates the final, atomic creation of a new business
 * and all its initial resources after the user completes the onboarding wizard.
 */
class CreateBusinessUseCase @Inject constructor(
    private val businessRepository: BusinessRepository,
    private val logService: LogService,
) {
    suspend operator fun invoke(data: OnboardingData): Result<String> {
        logService.log("CreateBusinessUseCase: Executing for owner: ${data.ownerUid}")

        if (data.businessName.length < 2) {
            return Result.failure(ValidationException("Business name must be at least 2 characters."))
        }
        if (data.initialBranchName.isBlank()) {
            return Result.failure(ValidationException("Initial branch name cannot be empty."))
        }
        if (!data.paymentResult.wasSuccessful) {
            return Result.failure(ValidationException("Cannot create business with a failed payment."))
        }
        // TODO: refactor this code to get all value business needed
        val newBusiness = Business(
            id = "",
            name = data.businessName,
            ownerUid = data.ownerUid,
            vertical = data.businessVertical,
            companyCode = "", // Repo will generate
            currencyCode = data.currencyCode,
            status = BusinessStatus.ACTIVE,
            email = "",
            phone = "",
            updatedAt = Clock.System.now(),
            createdAt = Clock.System.now(),
        )

        val initialBranch = Branch(
            id = "",
            name = data.initialBranchName,
            phone = null,
            status = BranchStatus.OPEN,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now(),
        )

        val initialSubscription = Subscription.fromPlan(data.selectedPlan)

        val initialBillingEvent = BillingEvent.forPlanActivation(
            plan = data.selectedPlan,
            currencyCode = data.currencyCode,
            paymentId = data.paymentResult.providerTransactionId,
        )

        // 3. Delegate to the repository for the final atomic write
        logService.log("CreateBusinessUseCase: Validation passed. Calling repository to persist new business.")
        return businessRepository.createInitialBusiness(
            business = newBusiness,
            initialBranches = listOf(initialBranch),
            initialTaxes = listOf(data.initialTaxRate),
            initialSubscription = initialSubscription,
            initialBillingEvent = initialBillingEvent,
        )
    }
}