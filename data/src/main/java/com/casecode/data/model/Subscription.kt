package com.casecode.data.model

import com.casecode.domain.model.subscriptions.Subscription
import com.casecode.domain.utils.SUBSCRIPTION_COST_FIELD
import com.casecode.domain.utils.SUBSCRIPTION_DURATION_FIELD
import com.casecode.domain.utils.SUBSCRIPTION_PERMISSIONS_FIELD
import com.casecode.domain.utils.SUBSCRIPTION_TYPE_FIELD
import com.google.firebase.firestore.DocumentSnapshot

fun MutableList<Subscription>.asEntitySubscriptions(
     document: DocumentSnapshot )
{
   val type = document[SUBSCRIPTION_TYPE_FIELD] as String
   val duration = document[SUBSCRIPTION_DURATION_FIELD] as Long
   val cost = document[SUBSCRIPTION_COST_FIELD] as Long
   val permissions = document[SUBSCRIPTION_PERMISSIONS_FIELD] as List<*>
   
   add(Subscription(cost, duration, permissions, type))
}