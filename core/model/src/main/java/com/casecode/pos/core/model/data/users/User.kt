package com.casecode.pos.core.model.data.users

import com.casecode.pos.core.model.data.users.Business
import com.casecode.pos.core.model.data.users.Employee
import com.casecode.pos.core.model.data.users.Invoice
import com.casecode.pos.core.model.data.users.Item
import com.casecode.pos.core.model.data.users.SubscriptionBusiness

data class User(
    val business: Business,
    val email: String,
    val employees: List<Employee>,
    val invoices: List<Invoice>,
    val items: List<Item>,
    val name: String,
    val password: String,
    val phoneNumber: String,
    val subscriptionBusiness: SubscriptionBusiness,
    val uid: String) {
   // Add a no-argument constructor
   constructor() : this(
      Business(),
      "",
      emptyList(),
      emptyList(),
      emptyList(),
      "",
      "",
      "",
      SubscriptionBusiness(),
      ""
                       )
}