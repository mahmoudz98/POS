package com.casecode.domain.usecase

import com.casecode.domain.model.users.Business
import com.casecode.domain.repository.BusinessRepository
import javax.inject.Inject


class SetBusinessUseCase @Inject constructor(private val businessRep: BusinessRepository)
{
   suspend operator fun invoke(business: Business, uid: String) =
      businessRep.setBusiness(business, uid)
}