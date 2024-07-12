package com.casecode.pos.core.model.data.permissions

import com.casecode.pos.core.model.data.users.StoreType

data class Permission1(
    val description: String,
    val name: String
)

enum class Permission(
    val englishName: String,
    val arabicName: String,
) {
    ADMIN("Admin", ""),
    SALE("Sale", ""),
    NONE("None", ""),
}
fun String.toPermission(): Permission? {
    return Permission.entries.find { type ->
        type.arabicName == this || type.englishName.lowercase() == this.lowercase()
    }
}