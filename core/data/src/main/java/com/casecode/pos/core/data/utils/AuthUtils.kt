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
package com.casecode.pos.core.data.utils

import com.casecode.pos.core.data.R
import com.casecode.pos.core.domain.repository.AuthRepository
import com.casecode.pos.core.domain.utils.Resource

suspend inline fun <T> AuthRepository.ensureUserExistsOrReturnError(onUserNotFound: (Resource<T>) -> Unit) {
    if (!this.hasUser()) {
        onUserNotFound(Resource.Error(R.string.core_data_uid_empty))
    }
}

suspend inline fun AuthRepository.ensureUserExists(onUserNotFound: (Int) -> Unit) {
    if (!this.hasUser()) {
        onUserNotFound((R.string.core_data_uid_empty))
    }
}