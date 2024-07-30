package com.casecode.pos.core.model.data.users

enum class StoreType(
    val englishName: String,
    val arabicName: String,
) {
    Clothes("Clothes", "ملابس"),
    Coffee("Coffee", "قهوة"),
    Market("Market", "سوبر ماركت"),
}