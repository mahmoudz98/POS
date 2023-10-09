package com.casecode.domain.usecase

import com.casecode.domain.repository.StoreRepository
import javax.inject.Inject

class GetStoreUseCase @Inject constructor(private val storeRepository: StoreRepository) {
   operator  fun invoke() = storeRepository.getStores()

}