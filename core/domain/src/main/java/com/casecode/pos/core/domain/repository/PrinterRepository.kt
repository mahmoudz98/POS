package com.casecode.pos.core.domain.repository

import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.PrinterInfo

interface PrinterRepository {
    suspend fun getPrinters(): Resource<List<PrinterInfo>>

    suspend fun addPrinter(printerInfo: PrinterInfo): Resource<Int>
}