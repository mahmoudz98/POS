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

import com.casecode.pos.core.firebase.datasource.FirebaseBusinessDataSourceImpl.Companion.BUSINESSES_COLLECTION_PATH
import com.casecode.pos.core.firebase.datasource.FirebaseBusinessDataSourceImpl.Companion.TAX_RATES_SUBCOLLECTION_PATH
import com.casecode.pos.core.firebase.model.NetworkTaxRate
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseTaxDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore,
) : TaxNetworkDataSource {

    /**
     * A helper function to get a reference to the tax rates sub-collection for a specific business.
     */
    private fun getTaxRatesCollection(businessId: String) =
        db.collection(BUSINESSES_COLLECTION_PATH).document(businessId).collection(TAX_RATES_SUBCOLLECTION_PATH)

    /**
     * Fetches all configured tax rates for a given business from Firestore.
     */
    override suspend fun getTaxRates(businessId: String): List<NetworkTaxRate> {
        return getTaxRatesCollection(businessId).get().await()
            .toObjects(NetworkTaxRate::class.java)
    }

    /**
     * Adds a new tax rate document to a business's taxRates sub-collection in Firestore.
     * @return The ID of the newly created Firestore document.
     */
    override suspend fun addTaxRate(businessId: String, taxRate: NetworkTaxRate): String {
        val docRef = getTaxRatesCollection(businessId).document()

        // Save the DTO, after stamping the generated ID onto it
        docRef.set(taxRate.copy(id = docRef.id)).await()

        return docRef.id
    }
}