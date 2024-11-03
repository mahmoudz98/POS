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
import com.casecode.pos.core.data.R
import com.casecode.pos.core.data.model.asSubscriptionBusinessModel
import com.casecode.pos.core.data.model.asSubscriptionRequest
import com.casecode.pos.core.data.utils.ensureUserExists
import com.casecode.pos.core.domain.repository.AddSubscriptionBusiness
import com.casecode.pos.core.domain.repository.AuthRepository
import com.casecode.pos.core.domain.repository.SubscriptionsBusinessRepository
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.firebase.services.FirestoreService
import com.casecode.pos.core.firebase.services.SUBSCRIPTION_BUSINESS_FIELD
import com.casecode.pos.core.firebase.services.USERS_COLLECTION_PATH
import com.casecode.pos.core.model.data.users.SubscriptionBusiness
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.UnknownHostException
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SubscriptionsBusinessRepositoryImpl
@Inject
constructor(
    private val db: FirestoreService,
    private val auth: AuthRepository,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : SubscriptionsBusinessRepository {
    override suspend fun setSubscriptionBusiness(
        subscriptionBusiness: SubscriptionBusiness,
    ): AddSubscriptionBusiness {
        return withContext(ioDispatcher) {
            try {
                auth.ensureUserExists<Boolean> {
                    return@withContext it
                }
                val currentUID = auth.currentUserId()

                suspendCoroutine<AddSubscriptionBusiness> { continuation ->
                    val addSubscriptionBusinessRequest =
                        subscriptionBusiness.asSubscriptionRequest()
                    db
                        .updateDocumentWithTask(
                            USERS_COLLECTION_PATH,
                            currentUID,
                            mapOf(
                                SUBSCRIPTION_BUSINESS_FIELD to
                                    FieldValue.arrayUnion(
                                        addSubscriptionBusinessRequest,
                                    ),
                            ),
                        ).addOnSuccessListener {
                            Timber.d("Subscription business is added successfully")
                            continuation.resume(Resource.Companion.success(true))
                        }.addOnFailureListener {
                            continuation.resume(
                                Resource.Companion.error(
                                    R.string.core_data_add_subscription_business_failure,
                                ),
                            )
                        }
                }
            } catch (_: UnknownHostException) {
                Resource.Companion.error(R.string.core_data_add_subscription_business_network)
            } catch (e: Exception) {
                Timber.e("Exception while adding business: $e")
                Resource.Companion.error(R.string.core_data_add_subscription_business_failure)
            }
        }
    }

    override fun getSubscriptionsBusiness(): Flow<Resource<List<SubscriptionBusiness>>> =
        flow<Resource<List<SubscriptionBusiness>>> {
            auth.ensureUserExists<List<SubscriptionBusiness>> {
                emit(it)
                return@flow
            }
            val currentUID = auth.currentUserId()
            db.listenToCollection(USERS_COLLECTION_PATH, currentUID).collect { snapshot ->
                @Suppress("UNCHECKED_CAST")
                val subscriptionBusinessMap =
                    snapshot.get(
                        SUBSCRIPTION_BUSINESS_FIELD,
                    ) as? List<Map<String, Any>>
                if (subscriptionBusinessMap.isNullOrEmpty()) {
                    emit(Resource.empty())
                } else {
                    emit(Resource.success(asSubscriptionBusinessModel(subscriptionBusinessMap)))
                }
            }
        }.catch {
            emit(Resource.error(R.string.core_data_add_subscription_business_failure))
        }.flowOn(ioDispatcher)
}