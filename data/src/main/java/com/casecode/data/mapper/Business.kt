package com.casecode.data.mapper

import com.casecode.domain.model.users.Branch
import com.casecode.domain.model.users.Business
import com.casecode.domain.model.users.StoreType
import com.casecode.domain.utils.BRANCHES_CODE_FIELD
import com.casecode.domain.utils.BRANCHES_COLLECTION_PATH
import com.casecode.domain.utils.BRANCHES_NAME_FIELD
import com.casecode.domain.utils.BRANCHES_PHONE_NUMBER_FIELD
import com.casecode.domain.utils.BUSINESS_EMAIL_FIELD
import com.casecode.domain.utils.BUSINESS_FIELD
import com.casecode.domain.utils.BUSINESS_IS_COMPLETED_STEP_FIELD
import com.casecode.domain.utils.BUSINESS_PHONE_NUMBER_FIELD
import com.casecode.domain.utils.BUSINESS_STORE_TYPE_FIELD

/**
 * Created by Mahmoud Abdalhafeez
 */
fun Map<String, Any>.asEntityBusiness(): Business {
    val storeType = this[BUSINESS_STORE_TYPE_FIELD] as? String ?: ""
    val email = this[BUSINESS_EMAIL_FIELD] as? String ?: ""
    val phone = this[BUSINESS_PHONE_NUMBER_FIELD] as? String ?: ""
    val isCompletedStep = this[BUSINESS_IS_COMPLETED_STEP_FIELD] as? Boolean ?: false

    // Retrieve branches data and apply necessary transformations
    @Suppress("UNCHECKED_CAST")
    val branchesData =
        this[BRANCHES_COLLECTION_PATH] as? List<Map<String, Any>> ?: emptyList()
    val branches = mutableListOf<Branch>()
    for (branchData in branchesData) {
        // Extract and transform branch data
        val branchCode = branchData[BRANCHES_CODE_FIELD] as? Int ?: 0
        val branchName = branchData[BRANCHES_NAME_FIELD] as? String ?: ""
        val phoneNumber = branchData[BRANCHES_PHONE_NUMBER_FIELD] as? String ?: ""
        val branch = Branch(branchCode, branchName, phoneNumber)
        branches.add(branch)
    }

    return Business(storeType.toStoreType(), email, phone, isCompletedStep, branches)
}

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
                BRANCHES_COLLECTION_PATH to branchesRequest,
            ),
    )
}

fun String.toStoreType(): StoreType? {
    return StoreType.entries.find { type ->
        type.arabicName == this || type.englishName.lowercase() == this.lowercase()
    }
}