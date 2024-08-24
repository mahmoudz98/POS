package com.casecode.pos.core.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent

fun moveToSignInActivity(context: Context) {
    val intent = Intent(context, Class.forName("com.casecode.pos.ui.signIn.SignInActivity"))
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