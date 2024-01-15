package com.casecode.testing.di.app

import com.casecode.di.app.FirebaseModule
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.mockk
import javax.inject.Singleton

@Module
@TestInstallIn(
   components = [SingletonComponent::class],
   replaces = [FirebaseModule::class],
              )
object TestFirebaseModule
{
   private const val HOST = "127.0.0.1"
   private const val AUTH_PORT = 9099
   private const val FIRESTORE_PORT = 8080
 
   
   @Singleton
   @Provides
   fun provideFirebaseAuthMockk(): FirebaseAuth =
      Firebase.auth.also { it.useEmulator(HOST, AUTH_PORT) }
   
   @Singleton
   @Provides
   fun provideFirebaseFirestoreMockk(): FirebaseFirestore = Firebase.firestore.also { it.useEmulator(HOST, FIRESTORE_PORT) }
   
}
