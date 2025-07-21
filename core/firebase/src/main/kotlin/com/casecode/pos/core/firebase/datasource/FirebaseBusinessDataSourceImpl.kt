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
package com.casecode.pos.core.firebase.datasource

import com.casecode.pos.core.firebase.model.NetworkBillingEvent
import com.casecode.pos.core.firebase.model.NetworkBranch
import com.casecode.pos.core.firebase.model.NetworkBusiness
import com.casecode.pos.core.firebase.model.NetworkSubscription
import com.casecode.pos.core.firebase.model.NetworkTaxRate
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseBusinessDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore,
) : BusinessNetworkDataSource {
    override suspend fun createInitialBusiness(
        business: NetworkBusiness,
        initialBranches: List<NetworkBranch>,
        initialTaxes: List<NetworkTaxRate>,
        initialSubscription: NetworkSubscription,
        initialBillingEvent: NetworkBillingEvent,
    ): String {
        return db.runTransaction { transaction ->
            val businessRef = db.collection(BUSINESSES_COLLECTION_PATH).document()
            val ownerUid = business.ownerUid

            val potentialCode = generateCompanyCode(business.name, ownerUid)
            val networkBusiness =
                business.copy(id = businessRef.id, companyCode = potentialCode)
            transaction.set(businessRef, networkBusiness)

            initialBranches.forEach { domainBranch ->
                val branchRef = businessRef.collection(BRANCHES_SUBCOLLECTION_PATH).document()
                transaction.set(branchRef, domainBranch.copy(id = branchRef.id))
            }
            initialTaxes.forEach { domainTax ->
                val taxRef = businessRef.collection(TAX_RATES_SUBCOLLECTION_PATH).document()
                transaction.set(taxRef, domainTax.copy(id = taxRef.id))
            }

            val subRef =
                businessRef.collection(SUBSCRIPTION_SUBCOLLECTION_PATH).document(SUB_CURRENT_DOC_ID)
            transaction.set(subRef, initialSubscription)

            val eventRef = businessRef.collection(BILLING_EVENTS_SUBCOLLECTION_PATH).document()
            transaction.set(eventRef, initialBillingEvent.copy(id = eventRef.id))

            businessRef.id
        }.await()
    }
    private fun generateCompanyCode(name: String, uid: String): String {
        val namePart = name.trim().filter(Char::isLetterOrDigit).take(3).uppercase()
        val uidPart = uid.filter(Char::isLetterOrDigit).take(4).uppercase()
        return "$namePart-$uidPart"
    }

    override suspend fun findBusinessByOwner(ownerUid: String): NetworkBusiness? {
        val doc = db.collection(BUSINESSES_COLLECTION_PATH).document(ownerUid).get().await()
        return doc.toObject(NetworkBusiness::class.java)
    }

    override suspend fun companyCodeExists(companyCode: String): Boolean {
        val snapshot =
            db.collection(BUSINESSES_COLLECTION_PATH)
                .whereEqualTo(BUSINESS_COMPANY_CODE_FIELD, companyCode).limit(1)
                .get().await()
        return !snapshot.isEmpty
    }

    override suspend fun getBranches(businessId: String): List<NetworkBranch> {
        val snapshot = getBranchesCollection(businessId)
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .get().await()

        return snapshot.toObjects(NetworkBranch::class.java)
    }

    override suspend fun addBranch(
        businessId: String,
        branch: NetworkBranch,
    ): String {
        val docRef = getBranchesCollection(businessId).document()
        docRef.set(branch.copy(id = docRef.id)).await()
        return docRef.id
    }

    /**
     * A helper function to get a reference to the branches sub-collection for a specific business.
     */
    private fun getBranchesCollection(businessId: String) =
        db.collection(BUSINESSES_COLLECTION_PATH).document(businessId).collection(BRANCHES_SUBCOLLECTION_PATH)

    companion object {
        const val BUSINESSES_COLLECTION_PATH = "businesses"
        const val BRANCHES_SUBCOLLECTION_PATH = "branches"
        const val TAX_RATES_SUBCOLLECTION_PATH = "taxRates"
        const val SUBSCRIPTION_SUBCOLLECTION_PATH = "subscription"
        const val BILLING_EVENTS_SUBCOLLECTION_PATH = "billingEvents"
        const val SUB_CURRENT_DOC_ID = "current"
        const val BUSINESS_COMPANY_CODE_FIELD = "companyCode"
    }
}