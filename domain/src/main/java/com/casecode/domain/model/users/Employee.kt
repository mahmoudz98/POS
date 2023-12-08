package com.casecode.domain.model.users

data class Employee(
     var name: String,
     val phoneNumber: String,
     val password: String? = null,
     val branchName: String? = null,
     val permission: String,
                   )
{
   // Add a no-argument constructor
   constructor() : this("", "", "", "", "")
}