package com.casecode.pos.core.data.service

import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import timber.log.Timber
import javax.inject.Inject

class LogServiceImpl
    @Inject
    constructor() : LogService {
        override fun logNonFatalCrash(throwable: Throwable) {
            Timber.e("logNonFatalCrash:$throwable")
            Firebase.crashlytics.recordException(throwable)
        }

        override fun log(message: String) {
            Timber.e(message)
            Firebase.crashlytics.log(message)
        }
    }