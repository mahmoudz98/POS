package com.casecode.pos.core.data.model

import com.casecode.pos.core.data.utils.SUBSCRIPTIONS_COLLECTION_PATH
import com.casecode.pos.core.data.utils.SUBSCRIPTION_COST_FIELD
import com.casecode.pos.core.data.utils.SUBSCRIPTION_DURATION_FIELD
import com.casecode.pos.core.data.utils.SUBSCRIPTION_PERMISSIONS_FIELD
import com.casecode.pos.core.data.utils.SUBSCRIPTION_TYPE_FIELD
import com.casecode.pos.core.model.data.users.SubscriptionBusiness
import timber.log.Timber

/**
 * Created by Mahmoud Abdalhafeez
 */
fun SubscriptionBusiness.asSubscriptionRequest(): HashMap<String, Any> {
    val subscriptionRequest =
        hashMapOf(
            SUBSCRIPTION_TYPE_FIELD to type,
            SUBSCRIPTION_COST_FIELD to cost,
            SUBSCRIPTION_DURATION_FIELD to duration,
            SUBSCRIPTION_PERMISSIONS_FIELD to permissions,
        )
    return hashMapOf(SUBSCRIPTIONS_COLLECTION_PATH to subscriptionRequest)
}

fun asSubscriptionBusinessModel(subscriptions: List<Map<String, Any>>): List<SubscriptionBusiness> =
    subscriptions.map {
        Timber.e("asSubscriptionBusinessModel: $it")
        @Suppress("UNCHECKED_CAST")
        SubscriptionBusiness(
            it[SUBSCRIPTION_TYPE_FIELD] as? String,
            it[SUBSCRIPTION_COST_FIELD] as? Long,
            it[SUBSCRIPTION_DURATION_FIELD] as? Long,
            it[SUBSCRIPTION_PERMISSIONS_FIELD] as? List<String>,
        )
    }