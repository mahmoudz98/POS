package com.casecode.domain.usecase

import com.casecode.service.AuthService
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(private val authService: AuthService) {
    operator fun invoke () = authService.currentUser
}