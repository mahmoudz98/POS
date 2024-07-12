package com.casecode.pos.feature.login_employee

data class LoginEmployeeUiState(
    val userMessage: Int? = null,
    val isOnline: Boolean = false,
    val inProgressLoginEmployee: Boolean = false,
)