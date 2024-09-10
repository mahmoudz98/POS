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
package com.casecode.pos.core.domain.utils

sealed interface Resource<out T> {
    data class Success<T>(
        val data: T,
    ) : Resource<T>

    data class Error<T>(
        val message: Any?,
    ) : Resource<T>

    data object Loading : Resource<Nothing>

    data class Empty<T>(
        val message: Any? = null,
    ) : Resource<T> {
        val data: T? = null
    }

    companion object {
        inline fun <reified T> success(data: T): Resource<T> = Success(data)

        inline fun <reified T> error(message: Any?): Resource<T> = Error(message)

        fun <T> loading(): Resource<T> = Loading

        inline fun <reified T> empty(
            message: Any? = null,
        ): Resource<T> = Empty(message)
    }
}