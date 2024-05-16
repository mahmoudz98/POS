package com.casecode.data.mapper

import com.casecode.domain.model.subscriptions.Subscription
import com.casecode.domain.model.users.SubscriptionBusiness
import com.casecode.domain.utils.SUBSCRIPTION_COST_FIELD
import com.casecode.domain.utils.SUBSCRIPTION_DURATION_FIELD
import com.casecode.domain.utils.SUBSCRIPTION_PERMISSIONS_FIELD
import com.casecode.domain.utils.SUBSCRIPTION_TYPE_FIELD
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

fun Subscription.asSubscriptionBusiness(): SubscriptionBusiness {
    return SubscriptionBusiness(type, cost, duration, permissions)
}