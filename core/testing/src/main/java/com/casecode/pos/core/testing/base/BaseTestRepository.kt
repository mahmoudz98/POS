package com.casecode.pos.core.testing.base

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

    abstract  fun init()

  open infix fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

  open infix fun setReturnEmpty(value: Boolean) {
        shouldReturnEmpty = value
    }
}