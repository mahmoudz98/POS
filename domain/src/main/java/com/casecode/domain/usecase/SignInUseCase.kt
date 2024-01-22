package com.casecode.domain.usecase

import com.casecode.domain.repository.SignInRepository
import com.google.firebase.auth.AuthCredential
import javax.inject.Inject

class SignInUseCase @Inject constructor(private val signInRepository: SignInRepository) {

    suspend fun checkRegistration(email: String) = signInRepository.checkRegistration(email)

    suspend fun signInWithCredential(credential: AuthCredential) =
        signInRepository.signInWithCredential(credential)

    fun signOut() {
        signInRepository.signOut()
    }

    suspend fun employeeLogin(uid: String, employeeId: String, password: String) =
        signInRepository.employeeLogin(uid, employeeId, password)

}