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
package com.casecode.pos.core.firebase.services

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