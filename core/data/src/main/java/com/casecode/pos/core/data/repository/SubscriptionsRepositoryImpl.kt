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
package com.casecode.pos.core.data.repository

import com.casecode.pos.core.common.AppDispatchers.IO
import com.casecode.pos.core.common.Dispatcher
import com.casecode.pos.core.data.model.asEntitySubscriptions
import com.casecode.pos.core.domain.repository.SubscriptionsRepository
import com.casecode.pos.core.domain.repository.SubscriptionsResource
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.firebase.services.FirestoreService
import com.casecode.pos.core.firebase.services.SUBSCRIPTIONS_COLLECTION_PATH
import com.casecode.pos.core.firebase.services.SUBSCRIPTION_COST_FIELD
import com.casecode.pos.core.model.data.subscriptions.Subscription
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Implementation of the [SubscriptionsRepository] interface.
 *
 * @param firestore The FirebaseFirestore instance.
 * @param ioDispatcher A dispatcher for IO-bound operations.
 */
class SubscriptionsRepositoryImpl
@Inject
constructor(
    private val db: FirestoreService,
    private val firestore: FirebaseFirestore,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : SubscriptionsRepository {
    /**
     * Gets the list of Subscription from the [FirebaseFirestore] database.
     *
     * @return A Flow of [Resource<List<Subscription>>] objects.
     */
    override suspend fun getSubscriptions(): SubscriptionsResource = withContext(ioDispatcher) {
        suspendCoroutine<SubscriptionsResource> { continuation ->
            db
                .getCollection(SUBSCRIPTIONS_COLLECTION_PATH)
                .orderBy(SUBSCRIPTION_COST_FIELD)
                .get()
                .addOnSuccessListener {
                    val subscriptions = mutableListOf<Subscription>()
                    it.documents.forEach { document ->
                        subscriptions.asEntitySubscriptions(document)
                    }
                    if (subscriptions.isEmpty()) {
                        continuation.resume(Resource.empty())
                    } else {
                        continuation.resume(Resource.success(subscriptions))
                    }
                }.addOnFailureListener {
                    continuation.resume(Resource.error(it.message))
                }
        }
    }
}