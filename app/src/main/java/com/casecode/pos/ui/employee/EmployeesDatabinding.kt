package com.casecode.pos.ui.employee

import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.casecode.domain.model.users.Branch
import com.casecode.domain.model.users.Employee
import com.casecode.pos.adapter.AutoCompleteAdapter
import com.casecode.pos.adapter.EmployeeAdapter
import timber.log.Timber

@BindingAdapter("itemsBranch")
fun AppCompatAutoCompleteTextView.setItemsBranch(branches: List<Branch>?)
{
   Timber.i("items $branches")
   val items = branches?.map { it.branchName }
   if (items != null)
   {
      val adapter = AutoCompleteAdapter(
         context,
         items.toList()
                                       )
      setOnItemClickListener { _, _, position, _ ->
         adapter.setSelectedItem(position)
         setText(adapter.getItem(position), false)
         
      }
      setAdapter(adapter)
   }
}

@BindingAdapter("textSelected")
fun AppCompatAutoCompleteTextView.setSelected(itemSelected: String?)
{
   if (itemSelected.isNullOrBlank()) return
   val adapter = (adapter as AutoCompleteAdapter)
   adapter.runCatching {
      val position = getPosition(itemSelected)
      setSelectedItem(position)
      setText(adapter.getItem(position), false)
   }.onFailure {
      setText(itemSelected, false)
   }
}

@BindingAdapter("bindListEmployee")
fun RecyclerView.bindListEmployee(items: MutableList<Employee>?)
{
   Timber.d("size of items in Employee = ${items?.size}")
   
   items?.let {
      (adapter as EmployeeAdapter).submitList(items)
   }
}