package com.casecode.domain.entity

data class PointOfSaleResponse(
    val customers: List<Customer>,
    val plans: List<Plan>,
    val roles: List<Role>,
    val stores: List<Store>
)