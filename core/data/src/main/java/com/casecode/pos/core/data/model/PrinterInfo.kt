package com.casecode.pos.core.data.model

import com.casecode.pos.core.data.utils.PRINTER_INFO_CONNECTION_TYPE_FIELD
import com.casecode.pos.core.data.utils.PRINTER_INFO_IS_CURRENT_SELECTED_FIELD
import com.casecode.pos.core.data.utils.PRINTER_INFO_NAME_FIELD
import com.casecode.pos.core.data.utils.PRINTER_INFO_SIZE_FIELD
import com.casecode.pos.core.model.data.PrinterInfo

fun PrinterInfo.asExternalMapper(): Map<String, Any?>{
    return mapOf(
        PRINTER_INFO_NAME_FIELD to this.name,
        PRINTER_INFO_CONNECTION_TYPE_FIELD to this.connectionTypeInfo,
        PRINTER_INFO_IS_CURRENT_SELECTED_FIELD to this.isCurrentSelected,
        PRINTER_INFO_SIZE_FIELD to this.widthPaper,
    )

}