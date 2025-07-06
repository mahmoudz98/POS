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

    abstract fun init()

    open infix fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    open infix fun setReturnEmpty(value: Boolean) {
        shouldReturnEmpty = value
    }
}

/**
 * An improved base class for test repositories that allows for simulating
 * specific, controlled failures.
 */
abstract class FakeRepository {

    // Instead of a boolean, we hold an optional Throwable.
    // If this is not null, repository methods should fail with this exception.
    private var failureThrowable: Throwable? = null

    /**
     * Configures the fake to return a Result.failure with the given Throwable
     * on its next suspending call.
     *
     * @param throwable The exception to be returned. Common examples include
     *                  IOException for network errors or a custom domain exception.
     */
    fun setFailure(throwable: Throwable) {
        this.failureThrowable = throwable
    }

    /**
     * Resets the repository to its default success state.
     */
    fun returnSuccess() {
        failureThrowable = null
    }

    /**
     * A helper for child classes to easily check if they should fail and with what exception.
     * It returns a failure Result if one is configured, otherwise null.
     */
    protected fun <T> getFailureResult(): Result<T>? {
        return failureThrowable?.let { Result.failure(it) }
    }
}