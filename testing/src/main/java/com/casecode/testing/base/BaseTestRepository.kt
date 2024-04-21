package com.casecode.testing.base

import org.junit.Before

abstract class BaseTestRepository {
    protected var shouldReturnError = false
    protected var shouldReturnEmpty = false

    @Before
    fun setup() {
        shouldReturnError = false
        shouldReturnEmpty = false
        init()
    }

    abstract fun init()

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    fun setReturnEmpty(value: Boolean) {
        shouldReturnEmpty = value
    }
}