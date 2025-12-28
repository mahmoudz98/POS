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
import javax.inject.Inject

class ValidateCompanyCodeUseCase @Inject constructor(
    private val businessRepository: BusinessRepository,
) {
    suspend operator fun invoke(companyCode: String): Result<Boolean> {
        if (companyCode.length < 5 || !companyCode.contains("-")) {
            return Result.failure(IllegalArgumentException("Invalid Company Code format."))
        }
        return businessRepository.getBusinessByCompanyCode(companyCode).map { it != null }
    }
}
