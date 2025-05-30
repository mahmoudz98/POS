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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn

context(ViewModel)
@Suppress("CONTEXT_RECEIVERS_DEPRECATED")
fun <T> Flow<T>.stateInWhileSubscribed(initialValue: T): StateFlow<T> = stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = initialValue,
)

context(ViewModel)
@Suppress("CONTEXT_RECEIVERS_DEPRECATED")
fun <T> Flow<T>.shareInWhileSubscribed(replay: Int): SharedFlow<T> = shareIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000),
    replay = replay,
)