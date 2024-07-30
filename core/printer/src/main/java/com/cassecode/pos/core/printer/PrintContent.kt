package com.cassecode.pos.core.printer

import com.casecode.pos.core.model.data.users.Item

sealed interface PrintContent {
    data class Receipt(val invoiceId: String, val phone: String, val items: List<Item>):PrintContent
    data class QrCode(val item: Item):PrintContent
    data object Test : PrintContent
}