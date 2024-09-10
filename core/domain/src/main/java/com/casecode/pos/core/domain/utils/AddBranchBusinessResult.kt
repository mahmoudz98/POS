package com.casecode.pos.core.domain.utils

sealed interface AddBranchBusinessResult {
    object Success : AddBranchBusinessResult
    data class Error(val message: Int) : AddBranchBusinessResult

}