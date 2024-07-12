package com.casecode.pos.core.model.data

sealed  interface LoginStateResult {
    data object Loading:LoginStateResult
    data class SuccessLoginAdmin(val uid: String) : LoginStateResult
    data class NotCompleteBusiness(val uid: String) : LoginStateResult
    data class EmployeeLogin(val employee :EmployeeLoginData): LoginStateResult
    data object NotSignIn : LoginStateResult
    data object Error : LoginStateResult

}