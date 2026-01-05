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
package com.casecode.pos.core.ui.parameterprovider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.casecode.pos.core.model.business.Employee
import com.casecode.pos.core.model.business.EmployeeRole

class EmployeesPreviewParameterProvider : PreviewParameterProvider<List<Employee>> {
    override val values: Sequence<List<Employee>>
        get() =
            sequenceOf(
                listOf(
                    Employee(
                        id = "1234",
                        phone = "213123213",
                        name = "John Doe",
                        role = EmployeeRole.CASHIER,
                        assignedBranchId = "branch1",
                    ),
                    Employee(
                        id = "2",
                        phone = "12345556",
                        name = "Jane Smith",
                        role = EmployeeRole.MANAGER,
                        assignedBranchId = "branch1",
                    ),
                    Employee(
                        id = "3",
                        phone = "12312331231",
                        name = "Alice Johnson",
                        role = EmployeeRole.OWNER,
                        assignedBranchId = "branch3",
                    ),
                    Employee(
                        id = "3123",
                        phone = "12312331231",
                        name = "Alice Johnson",
                        role = EmployeeRole.OWNER,
                        assignedBranchId = "branch3",
                    ),
                    Employee(
                        id = "31212",
                        phone = "12312331231",
                        name = "Alice Johnson",
                        role = EmployeeRole.OWNER,
                        assignedBranchId = "branch3",
                    ),
                    Employee(
                        id = "3343434",
                        phone = "12312331231",
                        name = "Alice Johnson",
                        role = EmployeeRole.OWNER,
                        assignedBranchId = "branch3",
                    ),
                    Employee(
                        id = "55553",
                        phone = "12312331231",
                        name = "Alice Johnson",
                        role = EmployeeRole.OWNER,
                        assignedBranchId = "branch3",
                    ),
                ),
            )
}
