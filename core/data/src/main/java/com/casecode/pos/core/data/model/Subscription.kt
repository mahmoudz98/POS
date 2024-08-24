package com.casecode.pos.core.data.model

import com.casecode.pos.core.data.utils.SUBSCRIPTION_COST_FIELD
import com.casecode.pos.core.data.utils.SUBSCRIPTION_DURATION_FIELD
import com.casecode.pos.core.data.utils.SUBSCRIPTION_PERMISSIONS_FIELD
import com.casecode.pos.core.data.utils.SUBSCRIPTION_TYPE_FIELD
import com.casecode.pos.core.model.data.subscriptions.Subscription
import com.casecode.pos.core.model.data.users.SubscriptionBusiness
import com.google.firebase.firestore.DocumentSnapshot

/**
 * Created by Mahmoud Abdalhafeez
 */
fun MutableList<Subscription>.asEntitySubscriptions(document: DocumentSnapshot) {
    val type = document[SUBSCRIPTION_TYPE_FIELD] as String
    val duration = document[SUBSCRIPTION_DURATION_FIELD] as Long
    val cost = document[SUBSCRIPTION_COST_FIELD] as Long

    @Suppress("UNCHECKED_CAST")
    val permissions = document[SUBSCRIPTION_PERMISSIONS_FIELD] as List<String>
    add(Subscription(cost, duration, permissions, type))
}

fun Subscription.asSubscriptionBusiness(): SubscriptionBusiness = SubscriptionBusiness(type, cost, duration, permissions)