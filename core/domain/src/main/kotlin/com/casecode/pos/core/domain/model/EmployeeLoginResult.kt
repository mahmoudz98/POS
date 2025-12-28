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
package com.casecode.pos.core.domain.model

/**
 * A sealed interface representing all possible outcomes of an employee sign-in attempt.
 */
sealed interface EmployeeLoginResult {
    /**
     * Success! The employee is authenticated and has access to the system.
     * The app should navigate to the appropriate screen based on role.
     */
    object Success : EmployeeLoginResult

    /**
     * The provided company code was invalid.
     */
    object InvalidCompanyCode : EmployeeLoginResult

    /**
     * The provided employee ID was not found.
     */
    object EmployeeNotFound : EmployeeLoginResult

    /**
     * The provided password was incorrect.
     */
    object InvalidCredentials : EmployeeLoginResult

    /**
     * A recoverable network error occurred during the process.
     */
    object NetworkError : EmployeeLoginResult

    /**
     * An unexpected or unrecoverable error occurred.
     */
    data class GeneralError(val exception: Throwable) : EmployeeLoginResult
}
