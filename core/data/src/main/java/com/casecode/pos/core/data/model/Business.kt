package com.casecode.pos.core.data.model

import com.casecode.pos.core.data.utils.BRANCHES_CODE_FIELD
import com.casecode.pos.core.data.utils.BRANCHES_FIELD
import com.casecode.pos.core.data.utils.BRANCHES_NAME_FIELD
import com.casecode.pos.core.data.utils.BRANCHES_PHONE_NUMBER_FIELD
import com.casecode.pos.core.data.utils.BUSINESS_EMAIL_FIELD
import com.casecode.pos.core.data.utils.BUSINESS_FIELD
import com.casecode.pos.core.data.utils.BUSINESS_IS_COMPLETED_STEP_FIELD
import com.casecode.pos.core.data.utils.BUSINESS_PHONE_NUMBER_FIELD
import com.casecode.pos.core.data.utils.BUSINESS_STORE_TYPE_FIELD
import com.casecode.pos.core.model.data.users.Branch
import com.casecode.pos.core.model.data.users.Business
import com.casecode.pos.core.model.data.users.StoreType

fun Map<String, Any>.asEntityBusiness(): Business {
    val storeType = this[BUSINESS_STORE_TYPE_FIELD] as? String ?: ""
    val email = this[BUSINESS_EMAIL_FIELD] as? String ?: ""
    val phone = this[BUSINESS_PHONE_NUMBER_FIELD] as? String ?: ""
    val isCompletedStep = this[BUSINESS_IS_COMPLETED_STEP_FIELD] as? Boolean ?: false

    // Retrieve branches data and apply necessary transformations
    @Suppress("UNCHECKED_CAST")
    val branchesData =
        this[BRANCHES_FIELD] as? List<Map<String, Any>> ?: emptyList()
    val branches = mutableListOf<Branch>()
    for (branchData in branchesData) {
        // Extract and transform branch data
        val branchCode = (branchData[BRANCHES_CODE_FIELD] as? Long)?.toInt() ?: 0
        val branchName = branchData[BRANCHES_NAME_FIELD] as? String ?: ""
        val phoneNumber = branchData[BRANCHES_PHONE_NUMBER_FIELD] as? String ?: ""
        val branch = Branch(branchCode, branchName, phoneNumber)
        branches.add(branch)
    }

    return Business(storeType.toStoreType(), email, phone, isCompletedStep, branches)
}

fun Branch.asExternalBranch(): Map<String, Any?> =
    mapOf(
        BRANCHES_CODE_FIELD to branchCode,
        BRANCHES_NAME_FIELD to branchName,
        BRANCHES_PHONE_NUMBER_FIELD to phoneNumber,
    )

fun Business.asExternalBusiness(): HashMap<String, HashMap<String, Any?>> {
    val branchesRequest = mutableListOf<Map<String, Any?>>()
    for (branch in branches) {
        val branchData =
            hashMapOf(
                BRANCHES_CODE_FIELD to branch.branchCode,
                BRANCHES_NAME_FIELD to branch.branchName,
                BRANCHES_PHONE_NUMBER_FIELD to branch.phoneNumber,
            )
        branchesRequest.add(branchData)
    }

    return hashMapOf(
        BUSINESS_FIELD to
            hashMapOf(
                BUSINESS_STORE_TYPE_FIELD to storeType,
                BUSINESS_EMAIL_FIELD to email,
                BUSINESS_PHONE_NUMBER_FIELD to phone,
                BUSINESS_IS_COMPLETED_STEP_FIELD to false,
                BRANCHES_FIELD to branchesRequest,
            ),
    )
}

fun String.toStoreType(): StoreType? =
    StoreType.entries.find { type ->
        type.arabicName == this || type.englishName.lowercase() == this.lowercase()
    }