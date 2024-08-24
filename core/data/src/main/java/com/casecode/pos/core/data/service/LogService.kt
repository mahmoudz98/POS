package com.casecode.pos.core.data.service

interface LogService {
    fun logNonFatalCrash(throwable: Throwable)

    fun log(message: String)
}