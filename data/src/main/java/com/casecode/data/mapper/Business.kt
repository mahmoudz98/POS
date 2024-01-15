package com.casecode.data.mapper

import com.casecode.domain.model.users.Business
import com.casecode.domain.utils.BRANCHES_CODE_FIELD
import com.casecode.domain.utils.BRANCHES_COLLECTION_PATH
import com.casecode.domain.utils.BRANCHES_NAME_FIELD
import com.casecode.domain.utils.BRANCHES_PHONE_NUMBER_FIELD
import com.casecode.domain.utils.BUSINESS_FIELD
import com.casecode.domain.utils.BUSINESS_EMAIL_FIELD
import com.casecode.domain.utils.BUSINESS_PHONE_NUMBER_FIELD
import com.casecode.domain.utils.BUSINESS_STORE_TYPE_FIELD
/**
 * Created by Mahmoud Abdalhafeez
 */

fun Business.toBusinessRequest(): HashMap<String, HashMap<String, Any?>>
{
   val branchesRequest = mutableListOf<Map<String, Any?>>()
   for (branch in branches)
   {
      val branchData = hashMapOf(
         BRANCHES_CODE_FIELD to branch.branchCode,
         BRANCHES_NAME_FIELD to branch.branchName,
         BRANCHES_PHONE_NUMBER_FIELD to branch.phoneNumber)
      branchesRequest.add(branchData)
   }
   return hashMapOf(
      BUSINESS_FIELD to hashMapOf(
         BUSINESS_STORE_TYPE_FIELD to storeType,
         BUSINESS_EMAIL_FIELD to email,
         BUSINESS_PHONE_NUMBER_FIELD to phone,
         BRANCHES_COLLECTION_PATH to branchesRequest
                                 )
                   )
}