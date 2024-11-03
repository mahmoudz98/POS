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
package com.casecode.pos.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.casecode.pos.lint.designsystem.DesignSystemDetector

class PosIssueRegistry : IssueRegistry() {
    override val issues =
        listOf(
            DesignSystemDetector.ISSUE,
            TestMethodNameDetector.FORMAT,
            TestMethodNameDetector.PREFIX,
        )

    override val api: Int = CURRENT_API

    override val minApi: Int = 12

    override val vendor: Vendor =
        Vendor(
            vendorName = "POS",
            feedbackUrl = "https://github.com/mahmoudz98/POS/issues",
            contact = "https://github.com/mahmoudz98/POS",
        )
}