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
package com.casecode.pos.core.ui

import android.util.Patterns
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import timber.log.Timber

fun validatePhoneNumber(
    phoneNumber: String,
    countryIsoCode: String,
): Int? {
    if (phoneNumber.isEmpty()) return R.string.core_ui_error_phone_empty
    val phoneNumberUtil = PhoneNumberUtil.getInstance()
    val countryCode =
        try {
            phoneNumberUtil.getCountryCodeForRegion(countryIsoCode)
        } catch (e: Exception) {
            Timber.e(e)
            null
        }

    if (countryCode == null) {
        return null
    }
    val phoneNumberProto = Phonenumber.PhoneNumber()

    return try {
        phoneNumberProto.countryCode = countryCode
        phoneNumberProto.nationalNumber = phoneNumber.toLong()
        if (!phoneNumberUtil.isValidNumber(phoneNumberProto)) {
            return R.string.core_ui_error_phone_invalid
        } else {
            null
        }
    } catch (e: Exception) {
        Timber.e(e)
        R.string.core_ui_error_phone_invalid
    }
}

fun validateEmail(email: String): Int? =
    when {
        email.isEmpty() -> R.string.core_ui_error_email_empty
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> R.string.core_ui_email_invalid
        else -> null
    }