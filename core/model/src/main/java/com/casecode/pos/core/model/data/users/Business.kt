package com.casecode.pos.core.model.data.users

data class Business(
    val storeType: StoreType? = null,
    val email: String? = null,
    val phone: String? = null,
    @field:JvmField
    val isCompletedStep: Boolean? = false,
    val branches: List<Branch> = listOf(),
)

enum class StoreType(
    val englishName: String,
    val arabicName: String,
) {
    Clothes("Clothes", "ملابس"),
    Coffee("Coffee", "قهوة"),
    Market("Market", "سوبر ماركت"),
}