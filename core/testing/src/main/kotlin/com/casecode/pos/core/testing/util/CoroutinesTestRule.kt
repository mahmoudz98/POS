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
package com.casecode.pos.core.testing.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class CoroutinesTestRule(
    private val dispatcher: TestDispatcher = StandardTestDispatcher(),
) : TestRule {
    override fun apply(
        base: Statement?,
        description: Description?,
    ): Statement = object : Statement() {
        override fun evaluate() {
            beforeEach()
            try {
                base?.evaluate()
            } finally {
                afterEach()
            }
        }
    }

    private fun beforeEach() {
        Dispatchers.setMain(dispatcher)
    }

    private fun afterEach() {
        Dispatchers.resetMain()
    }
}