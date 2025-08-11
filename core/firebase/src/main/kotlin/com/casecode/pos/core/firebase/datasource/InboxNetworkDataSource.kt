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

import com.casecode.pos.core.firebase.model.NetworkInboxSignal
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

private const val INBOX_PATH = "inbox"

/**
 * A data source that listens to a business-specific "inbox" in the Firebase Realtime Database
 * for lightweight synchronization signals from the server.
 */
interface InboxNetworkDataSource {
    /**
     * Listens for new synchronization signals for the specified business.
     *
     * Each emission from the [Flow] is a new [NetworkInboxSignal] that has been added to the
     * business's inbox and has not yet been processed by this device.
     *
     * @param businessId The ID of the business to listen to.
     * @return A [Flow] of [NetworkInboxSignal] objects, each paired with its unique signal ID.
     */
    fun listenForSyncSignals(businessId: String): Flow<Pair<String, NetworkInboxSignal>>

    /**
     * Posts a synchronization signal to the inbox of a specific business.
     *
     * @param businessId The ID of the business whose inbox will receive the signal.
     * @param signal The lightweight [NetworkInboxSignal] to post.
     * @return A [Result] indicating the success or failure of the operation.
     */
    suspend fun postSignal(businessId: String, signal: NetworkInboxSignal): Result<Unit>
}

@Singleton
class FirebaseInboxNetworkDataSource @Inject constructor(
    private val database: FirebaseDatabase,
) : InboxNetworkDataSource {

    override fun listenForSyncSignals(businessId: String): Flow<Pair<String, NetworkInboxSignal>> =
        callbackFlow {
            if (businessId.isBlank()) {
                Timber.w("Business ID is blank. Cannot listen for inbox signals.")
                close(IllegalArgumentException("Business ID cannot be blank"))
                return@callbackFlow
            }

            val businessInboxRef = database.getReference(INBOX_PATH).child(businessId)

            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        Timber.d("Business inbox for $businessId is empty or does not exist.")
                        return
                    }

                    snapshot.children.forEach { childSnapshot ->
                        val signalId = childSnapshot.key ?: return@forEach
                        try {
                            val signal = childSnapshot.getValue(NetworkInboxSignal::class.java)
                            if (signal != null) {
                                trySend(signalId to signal)
                            }
                        } catch (e: Exception) {
                            Timber.e(e)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Timber.e(
                        error.toException(),
                        "Inbox listener for business $businessId was cancelled.",
                    )
                    close(error.toException())
                }
            }

            businessInboxRef.addValueEventListener(listener)

            awaitClose {
                Timber.d("Closing inbox listener for business: $businessId")
                businessInboxRef.removeEventListener(listener)
            }
        }

    override suspend fun postSignal(businessId: String, signal: NetworkInboxSignal): Result<Unit> {
        return try {
            if (businessId.isBlank()) {
                throw IllegalArgumentException("Business ID cannot be blank.")
            }
            database.getReference(INBOX_PATH).child(businessId).push().setValue(signal).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
