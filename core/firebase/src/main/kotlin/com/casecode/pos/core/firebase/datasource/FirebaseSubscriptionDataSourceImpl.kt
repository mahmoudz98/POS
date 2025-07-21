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

import com.casecode.pos.core.firebase.BILLING_EVENT_DATE_FIELD
import com.casecode.pos.core.firebase.SUB_CREDIT_BALANCE_FIELD
import com.casecode.pos.core.firebase.datasource.FirebaseBusinessDataSourceImpl.Companion.BILLING_EVENTS_SUBCOLLECTION_PATH
import com.casecode.pos.core.firebase.datasource.FirebaseBusinessDataSourceImpl.Companion.BUSINESSES_COLLECTION_PATH
import com.casecode.pos.core.firebase.datasource.FirebaseBusinessDataSourceImpl.Companion.SUBSCRIPTION_SUBCOLLECTION_PATH
import com.casecode.pos.core.firebase.datasource.FirebaseBusinessDataSourceImpl.Companion.SUB_CURRENT_DOC_ID
import com.casecode.pos.core.firebase.model.NetworkBillingEvent
import com.casecode.pos.core.firebase.model.NetworkSubscription
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseSubscriptionDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore,
) : SubscriptionNetworkDataSource {

    /**
     * Fetches the single, current subscription state document for a business.
     */
    override suspend fun getSubscription(businessId: String): NetworkSubscription? {
        return db.collection(BUSINESSES_COLLECTION_PATH).document(businessId)
            .collection(SUBSCRIPTION_SUBCOLLECTION_PATH).document(SUB_CURRENT_DOC_ID)
            .get().await()
            .toObject(NetworkSubscription::class.java)
    }

    /**
     * Fetches the entire billing history for a business from Firestore, ordered by most recent.
     */
    override suspend fun getBillingHistory(businessId: String): List<NetworkBillingEvent> {
        return db.collection(BUSINESSES_COLLECTION_PATH).document(businessId)
            .collection(BILLING_EVENTS_SUBCOLLECTION_PATH)
            .orderBy(BILLING_EVENT_DATE_FIELD, Query.Direction.DESCENDING)
            .get().await()
            .toObjects(NetworkBillingEvent::class.java)
    }

    /**
     * Atomically performs two operations: updates the credit balance on the current subscription
     * document and creates a new billing event document to log the transaction.
     */
    override suspend fun updateCreditBalanceAndLogEvent(
        businessId: String,
        newBalance: Long,
        event: NetworkBillingEvent,
    ) {
        // A Firestore transaction is the only way to safely perform these two
        // separate writes as a single atomic unit.
        db.runTransaction { transaction ->
            val subRef = db.collection(BUSINESSES_COLLECTION_PATH).document(businessId)
                .collection(SUBSCRIPTION_SUBCOLLECTION_PATH).document(SUB_CURRENT_DOC_ID)

            val historyRef = db.collection(BUSINESSES_COLLECTION_PATH).document(businessId)
                .collection(BILLING_EVENTS_SUBCOLLECTION_PATH).document()

            // Perform the writes within the transaction block
            transaction.update(subRef, SUB_CREDIT_BALANCE_FIELD, newBalance)
            transaction.set(historyRef, event.copy(id = historyRef.id))
        }.await()
    }
}