package com.casecode.pos.core.domain.utils

sealed interface AddEmployeeResult {
    object Success : AddEmployeeResult
    data class Error(val message: Int) : AddEmployeeResult
}