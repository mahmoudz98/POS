/*
package com.casecode.pos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.casecode.domain.model.users.Employee
import com.casecode.pos.R
import com.casecode.pos.base.BaseAdapter
import com.casecode.pos.base.BaseViewHolder
import com.casecode.pos.databinding.ItemEmployeeBinding
import timber.log.Timber

class EmployeeAdapter(val itemClick: (Employee) -> Unit) :
   BaseAdapter<Employee>(EmployeeDiffCallback)
{
   companion object EmployeeDiffCallback : DiffUtil.ItemCallback<Employee>()
   {
      override fun areItemsTheSame(oldItem: Employee, newItem: Employee): Boolean
      {
         return oldItem === newItem
      }
      
      override fun areContentsTheSame(oldItem: Employee, newItem: Employee): Boolean
      {
         return oldItem == newItem
      }
      
   }
   
   inner class EmployeeViewHolder(binding: ItemEmployeeBinding) :
      BaseViewHolder<ItemEmployeeBinding, Employee>(binding)
   {
      
      init
      {
         binding.apply {
            itemView.setOnClickListener {
               employee?.run {
                  itemClick(this)
               }
            }
         }
      }
      
      override fun bind(element: Employee)
      {
         super.bind(element)
         Timber.e("element employee: $element")
         binding.employee = element
      }
   }
   
   override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
                                  ): BaseViewHolder<out ViewDataBinding, Employee>
   {
      return EmployeeViewHolder((ItemEmployeeBinding.inflate(LayoutInflater.from(parent.context),
         parent,
         false)))
   }
   
   override fun getItemViewType(position: Int): Int
   {
      return R.layout.item_employee
   }
   
   */
/**
    *  parent list is immutable and override here to use mutableList.
    *//*

   override fun submitList(list: MutableList<Employee>?)
   {
      super.submitList(
         list?.let { ArrayList(it) })
   }
}*/