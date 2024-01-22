package com.casecode.domain.usecase

import com.casecode.domain.repository.BusinessRepository
import javax.inject.Inject

class GetBusinessUseCase @Inject constructor(private val businessRep: BusinessRepository) {

    suspend operator fun invoke(uid: String) = businessRep.getBusiness(uid)
}