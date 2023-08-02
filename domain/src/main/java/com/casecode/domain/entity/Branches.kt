package com.casecode.domain.entity

data class Branches(
    val branchCode: Int = 0,
    val branchName: String = "",
    val phoneNumber: String = ""
) {
    // Add any additional properties or methods here
    constructor() : this(0,"","")
}

// =================================================================================================
// or use this class
// =================================================================================================
//class Branches {
//    var branchCode: Int? = null
//    var branchName: String? = null
//    var phoneNumber: String? = null
//
//    constructor() {
//        // Required empty constructor for Firestore deserialization
//    }
//
//    constructor(branchCode: Int?, branchName: String?, phoneNumber: String?) {
//        this.branchCode = branchCode
//        this.branchName = branchName
//        this.phoneNumber = phoneNumber
//    }
//}
// =================================================================================================