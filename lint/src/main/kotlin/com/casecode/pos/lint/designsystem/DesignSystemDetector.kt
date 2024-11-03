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
package com.casecode.pos.lint.designsystem

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UQualifiedReferenceExpression

/**
 * A detector that checks for incorrect usages of Compose Material APIs over equivalents in
 * the POS design system module.
 */
class DesignSystemDetector :
    Detector(),
    Detector.UastScanner {
    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(
        UCallExpression::class.java,
        UQualifiedReferenceExpression::class.java,
    )

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {
            override fun visitCallExpression(node: UCallExpression) {
                val name = node.methodName ?: return
                val preferredName = METHOD_NAMES[name] ?: return
                reportIssue(context, node, name, preferredName)
            }

            override fun visitQualifiedReferenceExpression(node: UQualifiedReferenceExpression) {
                val name = node.receiver.asRenderString()
                val preferredName = RECEIVER_NAMES[name] ?: return
                reportIssue(context, node, name, preferredName)
            }
        }

    companion object {
        @JvmField
        val ISSUE: Issue =
            Issue.create(
                id = "DesignSystem",
                briefDescription = "Design system",
                explanation =
                """This check highlights calls in code that use Compose
                   Material composables instead of equivalents
                   from the POS design system module.
                """.trimMargin(),
                category = Category.CUSTOM_LINT_CHECKS,
                priority = 7,
                severity = Severity.ERROR,
                implementation =
                Implementation(
                    DesignSystemDetector::class.java,
                    Scope.JAVA_FILE_SCOPE,
                ),
            )

        // Unfortunately :lint is a Java module and thus can't depend on the :core-designsystem
        // Android module, so we can't use composable function references (eg. ::Button.name)
        // instead of hardcoded names.
        val METHOD_NAMES =
            mapOf(
                "MaterialTheme" to "PosTheme",
                "Button" to "PosButton",
                "OutlinedButton" to "PosOutlinedButton",
                "TextButton" to "PosTextButton",
                "FilterChip" to "PosFilterChip",
                "ElevatedFilterChip" to "PosFilterChip",
                "NavigationBar" to "PosNavigationBar",
                "NavigationBarItem" to "PosNavigationBarItem",
                "NavigationRail" to "PosNavigationRail",
                "NavigationRailItem" to "PosNavigationRailItem",
                "TabRow" to "PosTabRow",
                "Tab" to "PosTab",
                "IconToggleButton" to "PosIconToggleButton",
                "FilledIconToggleButton" to "PosIconToggleButton",
                "FilledTonalIconToggleButton" to "PosIconToggleButton",
                "OutlinedIconToggleButton" to "PosIconToggleButton",
                "CenterAlignedTopAppBar" to "PosTopAppBar",
                "SmallTopAppBar" to "PosTopAppBar",
                "MediumTopAppBar" to "PosTopAppBar",
                "LargeTopAppBar" to "PosTopAppBar",
            )
        val RECEIVER_NAMES =
            mapOf(
                "Icons" to "PosIcons",
            )

        fun reportIssue(
            context: JavaContext,
            node: UElement,
            name: String,
            preferredName: String,
        ) {
            context.report(
                ISSUE,
                node,
                context.getLocation(node),
                "Using $name instead of $preferredName",
            )
        }
    }
}