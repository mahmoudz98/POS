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
package com.casecode.pos.core.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent

fun moveToSignInActivity(context: Context) {
    val intent = Intent(context, Class.forName("com.casecode.pos.feature.signin.SignInActivity"))
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)
    context.findActivity().finish()
}

fun moveToMainActivity(context: Context) {
    val intent = Intent(context, Class.forName("com.casecode.pos.ui.main.MainActivity"))
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)
    context.findActivity().finish()
}

fun moveToStepperActivity(context: Context) {
    val intent = Intent(context, Class.forName("com.casecode.pos.feature.stepper.StepperActivity"))
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)
    context.findActivity().finish()
}

private fun Context.getActivityOrNull(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }

    return null
}

private tailrec fun Context.findActivity(): Activity =
    when (this) {
        is Activity -> this
        is ContextWrapper -> this.baseContext.findActivity()
        else -> throw IllegalArgumentException("Could not find activity!")
    }