package com.casecode.domain.entity

import com.casecode.domain.model.stores.Store
import com.casecode.domain.model.users.Customer

data class PointOfSaleResponse(
     val customers: List<Customer>,
     val plans: List<Plan>,
     val roles: List<Role>,
     val stores: List<Store>,
                              )