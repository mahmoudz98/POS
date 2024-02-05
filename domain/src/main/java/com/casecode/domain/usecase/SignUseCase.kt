package com.casecode.domain.usecase

import android.content.Intent
import android.content.IntentSender
import com.casecode.domain.repository.SignRepository
import com.casecode.domain.utils.Resource
import javax.inject.Inject

class SignInUseCase @Inject constructor(private val signInRepository: SignRepository)
{
   
   suspend fun signIn(): IntentSender? = signInRepository.signIn()
   
   fun signInWithIntent(intent: Intent) =
      signInRepository.signInWithIntent(intent)
   
   suspend fun isRegistrationAndBusinessCompleted(): Resource<Boolean> =
      signInRepository.isRegistrationAndBusinessCompleted()
   
   suspend fun checkRegistration(email: String) = signInRepository.checkRegistration(email)
   fun employeeLogin(uid: String, employeeId: String, password: String) =
      signInRepository.employeeLogin(uid, employeeId, password)
   
}

class SignOutUseCase @Inject constructor(private val signInRepository: SignRepository)
{
   suspend operator fun invoke() = signInRepository.signOut()
}