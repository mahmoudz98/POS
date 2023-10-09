package com.casecode.domain.model.users

import com.casecode.domain.entity.EmployeeRole
import com.casecode.domain.entity.Schedule

data class Employee(
     val name: String,
     val phoneNumber: String,
     val password: String,
     val branchName: String,
     val permission: String
)
{
   // Add a no-argument constructor
   constructor() : this("", "", "", "", "")
}