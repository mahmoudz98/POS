package com.casecode.domain.entity

data class Branches(
    val branchCode: Int = 0,
    val branchName: String? = null,
    val phoneNumber: String? = null
) {
    // Add any additional properties or methods here
    constructor() : this(branchCode = 0, branchName = null, phoneNumber = null)
}