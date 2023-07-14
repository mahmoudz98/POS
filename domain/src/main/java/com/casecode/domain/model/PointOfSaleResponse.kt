package com.casecode.domain.model

data class PointOfSaleResponse(
    val customers: List<Customer>,
    val plans: List<Plan>,
    val roles: List<Role>,
    val stores: List<Store>
)