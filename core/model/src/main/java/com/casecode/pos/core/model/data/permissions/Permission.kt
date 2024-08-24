package com.casecode.pos.core.model.data.permissions

data class Permission1(
    val description: String,
    val name: String,
)

enum class Permission(
    val englishName: String,
    val arabicName: String,
) {
    ADMIN("Admin", ""),
    SALE("Sales", ""),
    NONE("None", ""),
}

fun String.toPermission(): Permission? =
    Permission.entries.find { type ->
        type.arabicName == this || type.englishName.lowercase() == this.lowercase()
    }