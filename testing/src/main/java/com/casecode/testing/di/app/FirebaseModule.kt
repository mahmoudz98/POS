
package com.casecode.testing.di.app

import com.casecode.di.app.FirebaseModule
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.mockk

@Module
@TestInstallIn(
   components = [SingletonComponent::class],
   replaces = [FirebaseModule::class],
              )
object TestFirebaseModule
{
   
 /*   @Provides
   @Singleton
   fun provideFirebaseAuthMockk(): FirebaseAuth = mockk() */
   
   @Provides
   fun provideFirebaseFirestoreMockk(): FirebaseFirestore = mockk()
   
}
