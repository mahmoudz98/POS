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
package com.casecode.pos.core.testing.di

import com.casecode.pos.core.firebase.di.FirebaseModule
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [FirebaseModule::class],
)
object TestFirebaseModule {
    private const val HOST = "10.0.2.2"
    const val AUTH_PORT = 9099
    private const val FIRESTORE_PORT = 8080
    private const val STORAGE_PORT = 9199

    @Provides
    @Singleton
    fun provideFirebaseAuth(app: FirebaseApp): FirebaseAuth {
        return FirebaseAuth.getInstance(app).apply { useEmulator(HOST, AUTH_PORT) }
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(app: FirebaseApp): FirebaseFirestore {
        return FirebaseFirestore.getInstance(app).apply {
            firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setHost("$HOST:$FIRESTORE_PORT")
                .setSslEnabled(false)
                .build()
        }
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(app: FirebaseApp): FirebaseStorage {
        return FirebaseStorage.getInstance(app).apply { useEmulator(HOST, STORAGE_PORT) }
    }

    @Provides
    fun provideSignInRequest(): GetGoogleIdOption = GetGoogleIdOption.Builder().setServerClientId("test").build()
}