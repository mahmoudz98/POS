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
package com.casecode.pos.core.domain.exceptions

/**
 * A base, sealed class for all custom, predictable exceptions within the application domain.
 * This allows ViewModels to use a single `when` block to handle all known failure types.
 */
sealed class AppException(message: String) : Exception(message)

class BusinessNotFoundException(message: String) : AppException(message)
class InvalidCredentialsException(message: String) : AppException(message)

sealed class AuthException(message: String) : AppException(message)
class GoogleSignInException(message: String, cause: Throwable? = null) : AuthException(message)

/**
 * Thrown when user-provided data fails a specific business rule validation.
 * The message should be clear enough for a developer to understand the issue.
 * This is a generic validation failure.
 */
open class ValidationException(message: String) : AppException(message)

/**
 * Thrown during business creation if a generated Company Code already exists.
 * This indicates a collision that the retry logic could not resolve.
 */
class CompanyCodeCollisionException(val attemptedCode: String) :
    AppException("Company code '$attemptedCode' is already taken.")

// --- PAYMENT EXCEPTIONS ---

/**
 * A base exception for all payment-related failures.
 */
sealed class PaymentException(message: String) : AppException(message)

/**
 * Represents a failure that originated from the payment provider (Google, Stripe, etc.).
 * @param providerMessage The detailed error message from the provider's SDK.
 */
class PaymentProviderException(val providerMessage: String) :
    PaymentException("Payment provider reported an error: $providerMessage")

/**
 * Represents the case where the user explicitly cancelled the purchase flow.
 * This often does not need to be shown as an error to the user.
 */
class PaymentCancelledException :
    PaymentException("User cancelled the purchase.")

/**
 * Represents a configuration error where the requested plan ID was not found in the
 * active offerings from the payment provider. This is a developer/setup error.
 */
class PlanNotFoundInProviderException(val planId: String) :
    PaymentException("The plan with ID '$planId' was not found in the current offerings.")

// --- GENERAL EXCEPTIONS ---

/**
 * Represents a failure to perform an operation due to insufficient credits.
 */
class InsufficientCreditsException :
    AppException("Operation failed due to insufficient credits.")