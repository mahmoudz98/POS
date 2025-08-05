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
package com.casecode.pos.feature.onboarding

import androidx.annotation.StringRes
import com.casecode.pos.core.ui.R.string as uiString

/**
 * An enum class representing the distinct steps in the onboarding process,
 * derived directly from the user stories in Epic 1.
 *
 * @property titleRes A string resource ID for the title of the step.
 */
enum class OnboardingStep(@StringRes val titleRes: Int) {
    BUSINESS_INFO(uiString.core_ui_business_info_title),
    FINANCIALS(uiString.core_ui_financials_title),
    SUBSCRIPTION(uiString.core_ui_subscription_plan_title),
    BRANCHES(uiString.core_ui_branches_title),
    ;

    companion object {
        fun fromOrdinal(ordinal: Int) = entries.getOrNull(ordinal) ?: BUSINESS_INFO
    }
}
