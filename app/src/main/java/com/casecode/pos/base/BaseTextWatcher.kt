package com.casecode.pos.base

import android.text.Editable
import android.text.TextWatcher
import timber.log.Timber

abstract class BaseTextWatcher : TextWatcher {
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        Timber.i("beforeTextChanged: ${p0?.isEmpty()}")

    }

    override fun afterTextChanged(p0: Editable?) {
        Timber.i("afterTextChanged: ${p0?.isEmpty()}")
    }
}