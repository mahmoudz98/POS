package com.casecode.pos.core.domain.utils

import com.casecode.pos.core.model.data.users.Business

sealed interface BusinessResult {
    data class Success(val data: Business) : BusinessResult
    data class Error(val message: Int?) : BusinessResult

}