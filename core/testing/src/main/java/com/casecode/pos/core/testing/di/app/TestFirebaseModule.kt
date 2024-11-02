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
package com.casecode.pos.core.testing.di.app

import com.casecode.pos.core.firebase.services.di.FirebaseModule
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [FirebaseModule::class],
)
object TestFirebaseModule {
    private const val HOST = "127.0.0.1"
    private const val AUTH_PORT = 9099
    private const val FIRESTORE_PORT = 8080

    @Provides
    fun provideFirebaseAuthMockk(): FirebaseAuth? {
        if (Firebase.auth.currentUser != null) {
            return Firebase.auth.also { it.useEmulator(HOST, AUTH_PORT) }
        }
        return null
    }

    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore =
        Firebase.firestore.also { it.useEmulator(HOST, FIRESTORE_PORT) }

    @Provides
    fun provideFirebaseStorage(): FirebaseStorage = Firebase.storage

    @Provides
    fun provideSignInRequest(): GetGoogleIdOption = GetGoogleIdOption("test")
}