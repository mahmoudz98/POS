package com.casecode.pos.core.firebase.model



/**
 * Represents a lightweight sync signal received from the Firebase Realtime Database "inbox".
 * This signal informs the client that a specific entity has been updated on the server.
 *
 * @property entityType A string representing the type of entity that was updated (e.g., "ITEM", "INVOICE").
 * @property entityId The unique identifier of the entity that was updated.
 * @property lastUpdated A server-side timestamp indicating when the update occurred.
 */
data class NetworkInboxSignal(
    val entityType: String? = null,
    val entityId: String? = null,
    val lastUpdated: Long? = null,
)
