package com.casecode.di.app

import android.content.Context
import com.casecode.pos.di.BuildConfig
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.firestore.persistentCacheSettings
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object FirebaseModule {

    /**
     * Configures the Firestore settings.
     */
    private val setting = firestoreSettings {
        // Use memory cache
        setLocalCacheSettings(memoryCacheSettings {})
        // Use persistent disk cache (default)
        setLocalCacheSettings(persistentCacheSettings { })
    }
    /**
     * Provides an instance of FirebaseAuth.
     */
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()


    /**
     * Provides an instance of FirebaseFirestore.
     *
     * @return A FirebaseFirestore instance.
     */
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance().apply {
        firestoreSettings = setting
    }

    @Provides
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideSignInClient(
        @ApplicationContext context: Context,
    ) = Identity.getSignInClient(context)

    @Provides
    fun provideSignInRequest(): BeginSignInRequest {
        val webClient = BuildConfig.web_client_id
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(webClient)
                    .build(),
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}