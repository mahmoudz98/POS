package com.casecode.pos.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.activity.ComponentActivity
import com.casecode.pos.ui.main.MainActivity
import com.casecode.pos.ui.signIn.SignInActivity
import com.casecode.pos.ui.stepper.StepperActivity
import timber.log.Timber

fun moveToSignInActivity(context: Context) {
    val intent = Intent(context, SignInActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)
    context.getActivityOrNull()?.finish()
}
fun moveToMainActivity(context: Context) {
    val intent = Intent(context, MainActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)
    context.getActivityOrNull()?.finish()
}
 fun moveToStepperActivity(context :Context) {
    val intent = Intent(context, StepperActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
     context.startActivity(intent)
     context.getActivityOrNull()?.finish()
}
private fun Context.getActivityOrNull(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }

    return null
}