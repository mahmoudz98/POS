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
package com.casecode.pos.core.ui.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent

fun moveToSignInActivity(context: Context) {
    val intent = Intent(context, Class.forName("com.casecode.pos.feature.signin.SignInActivity"))
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)
    findActivity<Activity>(context)?.finish()
}

fun moveToMainActivity(context: Context) {
    val intent = Intent(context, Class.forName("com.casecode.pos.MainActivity"))
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)
    findActivity<Activity>(context)?.finish()
}

fun moveToStepperActivity(context: Context) {
    val intent = Intent(context, Class.forName("com.casecode.pos.feature.stepper.StepperActivity"))
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)
    findActivity<Activity>(context)?.finish()
}

private inline fun <reified T> findActivity(context: Context): T? {
    var innerContext = context
    while (innerContext is ContextWrapper) {
        if (innerContext is T) {
            return innerContext
        }
        innerContext = innerContext.baseContext
    }
    return null
}