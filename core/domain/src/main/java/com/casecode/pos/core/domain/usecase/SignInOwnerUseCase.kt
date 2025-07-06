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

import com.casecode.pos.core.domain.repository.business.AuthRepository
import com.casecode.pos.core.domain.repository.business.BranchRepository
import com.casecode.pos.core.domain.repository.business.BusinessRepository
import com.casecode.pos.core.domain.repository.business.SessionRepository
import com.casecode.pos.core.domain.service.LogService
import com.casecode.pos.core.domain.utils.OwnerLoginResult
import com.casecode.pos.core.model.data.business.BusinessStatus
import java.io.IOException
import javax.inject.Inject

class SignInOwnerUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val businessRepository: BusinessRepository,
    private val branchRepository: BranchRepository,
    private val sessionRepository: SessionRepository,
    private val logService: LogService,
) {
    suspend operator fun invoke(idToken: String): OwnerLoginResult {
        logService.log("SignInOwnerUseCase: Attempting Google Sign-In.")
        val user = authRepository.signInWithGoogle(idToken).getOrNull()
        if (user == null) {
            return OwnerLoginResult.AuthenticationFailed
        }
        val businessResult = businessRepository.findBusinessByOwner(user.uid)
        val business = businessResult.getOrNull()
        if (businessResult.isFailure) {
            val exception = businessResult.exceptionOrNull()
            return if (exception is IOException) {
                OwnerLoginResult.NetworkError
            } else {
                OwnerLoginResult.GeneralError(
                    exception!!,
                )
            }
        }
        if (business == null || business.status != BusinessStatus.ACTIVE) {
            logService.log("SignInOwnerUseCase: Business not found or not active. Directing to onboarding.")
            sessionRepository.startOwnerSession(user, false, "", "")

            return OwnerLoginResult.AccountNeedsOnboarding
        }
        logService.log("SignInOwnerUseCase: Business found: ${business.id}. Fetching branches.")
        val branchesResult = branchRepository.getBranches(business.id)
        val branches = branchesResult.getOrNull()

        if (branchesResult.isFailure) {
            val exception = branchesResult.exceptionOrNull()!!
            logService.logNonFatalCrash(exception)
            return if (exception is IOException) {
                OwnerLoginResult.NetworkError
            } else {
                OwnerLoginResult.GeneralError(
                    exception,
                )
            }
        }

        if (branches.isNullOrEmpty()) {
            logService.log("SignInOwnerUseCase: Business is active but has no branches. Directing to onboarding to fix.")
            sessionRepository.startOwnerSession(user, false, "", "")

            return OwnerLoginResult.AccountNeedsOnboarding
        }

        val firstBranchId = branches.first().id
        logService.log("SignInOwnerUseCase: Branches found. Starting session for branch: $firstBranchId")
        sessionRepository.startOwnerSession(user, true, business.id, firstBranchId)

        return OwnerLoginResult.Success
    }
}