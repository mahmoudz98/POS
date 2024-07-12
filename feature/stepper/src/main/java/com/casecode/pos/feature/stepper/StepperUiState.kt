package com.casecode.pos.feature.stepper

import com.casecode.pos.core.model.data.subscriptions.Subscription
import com.casecode.pos.core.model.data.users.Branch
import com.casecode.pos.core.model.data.users.Employee

data class StepperBusinessUiState(
    val isOnline: Boolean = false,
    val isLoading: Boolean = false,
    val storeType: String = "",
    val emailBusiness: String = "",
    val phoneBusiness: String = "",
    val branches: List<Branch> = emptyList(),
    val branchSelected: Branch = Branch(),
    val subscriptions: List<Subscription> = emptyList(),
    val currentSubscription: Subscription = Subscription(),
    val employees: MutableList<Employee> = mutableListOf(),
    val employeeSelected: Employee = Employee(),
)

data class ButtonsStepState(
    val buttonNextStep: Boolean = false,
    val buttonPreviousStep: Boolean = false,
    val buttonCompletedSteps: Boolean = false,
)