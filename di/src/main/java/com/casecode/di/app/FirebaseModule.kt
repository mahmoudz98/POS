package com.casecode.di.app

import com.casecode.pos.di.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.firestore.persistentCacheSettings
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.security.MessageDigest
import java.util.UUID
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
    fun provideSignInRequest(): GetGoogleIdOption {
        val hashedNonce = createHashedNonce()
        val webClient = BuildConfig.web_client_id
        return GetGoogleIdOption.Builder().setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClient).setAutoSelectEnabled(true).setNonce(hashedNonce).build()

    }

    private fun createHashedNonce(): String {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }
        return hashedNonce
    }
}