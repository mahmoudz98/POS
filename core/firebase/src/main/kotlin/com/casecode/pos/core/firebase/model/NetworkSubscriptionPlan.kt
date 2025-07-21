package com.casecode.pos.core.firebase.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a single subscription plan.
 * Corresponds to an object in the 'subscription_plans' remote config array.
 */
@Serializable
data class NetworkSubscriptionPlan(
    @SerialName("id")
    val id: String,
    @SerialName("name_en")
    val nameEn: String,
    @SerialName("name_ar")
    val nameAr: String,
    @SerialName("price")
    val price: Double? = null,
    @SerialName("prices")
    val prices: List<NetworkPlanPrice>? = null,
    @SerialName("isFree")
    val isFree: Boolean,
    @SerialName("features_en")
    val featuresEn: List<String>,
    @SerialName("features_ar")
    val featuresAr: List<String>,
    @SerialName("limits")
    val limits: NetworkPlanLimits
)

/**
 * Represents the multi-currency price for a paid subscription plan.
 */
@Serializable
data class NetworkPlanPrice(
    @SerialName("currency")
    val currency: String,
    val amount: Double
)

/**
 * Represents the resource limits for a subscription plan.
 */
@Serializable
data class NetworkPlanLimits(
    @SerialName("maxBranches")
    val maxBranches: Int,

    @SerialName("maxEmployees")
    val maxEmployees: Int,

    @SerialName("maxItems")
    val maxItems: Int,

    @SerialName("initialCredits")
    val initialCredits: Int
)
