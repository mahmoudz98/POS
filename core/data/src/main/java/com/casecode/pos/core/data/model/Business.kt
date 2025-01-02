/*
 * Designed and developed 2024 by Mahmood Abdalhafeez
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.casecode.pos.core.data.model

import com.casecode.pos.core.firebase.services.BRANCHES_CODE_FIELD
import com.casecode.pos.core.firebase.services.BRANCHES_FIELD
import com.casecode.pos.core.firebase.services.BRANCHES_NAME_FIELD
import com.casecode.pos.core.firebase.services.BRANCHES_PHONE_NUMBER_FIELD
import com.casecode.pos.core.firebase.services.BUSINESS_EMAIL_FIELD
import com.casecode.pos.core.firebase.services.BUSINESS_FIELD
import com.casecode.pos.core.firebase.services.BUSINESS_IS_COMPLETED_STEP_FIELD
import com.casecode.pos.core.firebase.services.BUSINESS_PHONE_NUMBER_FIELD
import com.casecode.pos.core.firebase.services.BUSINESS_STORE_TYPE_FIELD
import com.casecode.pos.core.model.data.users.Branch
import com.casecode.pos.core.model.data.users.Business
import com.casecode.pos.core.model.data.users.StoreType

fun Map<String, Any>.asEntityBusiness(): Business {
    val storeType = this[BUSINESS_STORE_TYPE_FIELD] as? String ?: ""
    val email = this[BUSINESS_EMAIL_FIELD] as? String ?: ""
    val phone = this[BUSINESS_PHONE_NUMBER_FIELD] as? String ?: ""
    val isCompletedStep = this[BUSINESS_IS_COMPLETED_STEP_FIELD] as? Boolean == true

    // Retrieve branches data and apply necessary transformations
    @Suppress("UNCHECKED_CAST")
    val branchesData = this[BRANCHES_FIELD] as? List<Map<String, Any>> ?: emptyList()
    val branches = mutableListOf<Branch>()
    for (branchData in branchesData) {
        // Extract and transform branch data
        val branchCode = (branchData[BRANCHES_CODE_FIELD] as? Long)?.toInt() ?: 0
        val branchName = branchData[BRANCHES_NAME_FIELD] as? String ?: ""
        val phoneNumber = branchData[BRANCHES_PHONE_NUMBER_FIELD] as? String ?: ""
        val branch = Branch(branchCode, branchName, phoneNumber)
        branches.add(branch)
    }
    return Business(StoreType.toStoreType(storeType), email, phone, isCompletedStep, branches)
}

fun Branch.asExternalBranch(): Map<String, Any?> = mapOf(
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