package com.casecode.pos.core.domain.usecase

import com.casecode.pos.core.domain.repository.PrinterRepository
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.PrinterInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddPrinterUseCase
    @Inject
    constructor(
        private val printerRepository: PrinterRepository,
    ) {
        suspend operator fun invoke(printerInfo: PrinterInfo) = printerRepository.addPrinter(printerInfo)
    }

class GetPrinterUseCase
    @Inject
    constructor(
        private val printerRepository: PrinterRepository,
    ) {
        operator fun invoke(): Flow<Resource<List<PrinterInfo>>> =
            flow {
                emit(Resource.loading())
                emit(printerRepository.getPrinters())
        }
}