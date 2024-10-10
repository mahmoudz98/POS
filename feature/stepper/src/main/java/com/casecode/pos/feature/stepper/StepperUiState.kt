/*
 * Designed and developed 2024 by Mahmood Abdalhafeez
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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