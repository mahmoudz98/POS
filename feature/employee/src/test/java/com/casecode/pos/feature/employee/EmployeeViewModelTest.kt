/*
package com.casecode.pos.viewmodel

import com.casecode.pos.R
import com.casecode.pos.ui.employee.EmployeeViewModel
import com.casecode.testing.base.BaseTest
import com.casecode.testing.util.MainDispatcherRule
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.`is`
import org.junit.Rule
import org.junit.Test

class EmployeeViewModelTest: BaseTest() {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Subject under test
    private lateinit var viewModel: EmployeeViewModel

    override fun init() {
        viewModel = EmployeeViewModel(networkMonitor,getEmployees,
            getBusiness,
            addEmployee, updateEmployee)
    }

    @Test
    fun addEmployee_hasEmployee_returnAddEmployeeMessage() {
        // Given
        viewModel.addEmployee("mahmoud22", "131434", "1234", "branch1", "sale")

        MatcherAssert.assertThat(viewModel.userMessage.value?.peekContent(), `is`(R.string.add_employee_success))
    }

    @Test
    fun addEmployee_nameEmployeeDuplicateEmployee_returnMessageDuplicateName() {
        // Given
        viewModel.addEmployee("Mahmoud", "131434", "1234", "branch1", "sale")

        MatcherAssert.assertThat(viewModel.userMessage.value?.peekContent(), `is`(R.string.employee_name_duplicate))
    }
    @Test
    fun addEmployee_error_returnMessageError() {

        employeesBusinessRepository setReturnError true
        viewModel.addEmployee("Mahmoud1223", "131434", "1234", "branch1", "sale")

        MatcherAssert.assertThat(viewModel.userMessage.value?.peekContent(), `is`(com.casecode.pos.data.R.string.add_employees_business_failure))
    }

    @Test
    fun updateEmployee_hasChangeOldEmployee_returnMessageUpdateSuccess() {
        // Given
        viewModel.setEmployeeSelected(employeesBusinessRepository.fakeEmployees[0])
        // When
        viewModel.updateEmployee("Mahmoud1223", "131434", "1234", "branch1", "sale")

        MatcherAssert.assertThat(viewModel.userMessage.value?.peekContent(), `is`(R.string.update_employee_success))
    }
    @Test
    fun updateEmployee_nameEmployeeDuplicateEmployee_returnMessageDuplicateName() {
        // Given
        viewModel.setEmployeeSelected(employeesBusinessRepository.fakeEmployees[0])
        // When
        val nameDuplicate = employeesBusinessRepository.fakeEmployees[1].name
        viewModel.updateEmployee(nameDuplicate, "131434", "1234", "branch1", "sale")

        MatcherAssert.assertThat(viewModel.userMessage.value?.peekContent(), `is`(R.string.employee_name_duplicate))
    }
    @Test
    fun updateEmployee_hasSameOldEmployee_returnMessageUpdateEmployeeFailed() {
        // Given
        viewModel.setEmployeeSelected(employeesBusinessRepository.fakeEmployees[0])
        val name = employeesBusinessRepository.fakeEmployees[0].name
        val phoneNumber = employeesBusinessRepository.fakeEmployees[0].phoneNumber
        val password = employeesBusinessRepository.fakeEmployees[0].password!!
        val branchName = employeesBusinessRepository.fakeEmployees[0].branchName!!
        val permission = employeesBusinessRepository.fakeEmployees[0].permission
        // When

        viewModel.updateEmployee(name, phoneNumber, password, branchName, permission)

        MatcherAssert.assertThat(viewModel.userMessage.value?.peekContent(), `is`(R.string.update_employee_fail))
    }
    @Test
    fun updateEmployee_error_returnMessageError() {

        employeesBusinessRepository setReturnError true
        viewModel.setEmployeeSelected(employeesBusinessRepository.fakeEmployees[0])
        viewModel.updateEmployee("Mahmoud1223", "131434", "1234", "branch1", "sale")

        MatcherAssert.assertThat(viewModel.userMessage.value?.peekContent(), `is`(com.casecode
            .pos.data.R.string.employee_update_business_failure))
    }
    @Test
    fun updateEmployee_network_unavailable_returnMessageError() {
        // Given
        viewModel.setEmployeeSelected(employeesBusinessRepository.fakeEmployees[0])
        // When
        networkMonitor.setConnected(false)
        viewModel.updateEmployee("Mahmoud1223", "131434", "1234", "branch1", "sale")
        // Then
        MatcherAssert.assertThat(viewModel.userMessage.value?.peekContent(), `is`(R.string.network_error))
    }


}*/