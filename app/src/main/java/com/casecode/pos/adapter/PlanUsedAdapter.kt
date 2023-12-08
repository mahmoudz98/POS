/*
package com.casecode.pos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.casecode.domain.entity.PlanUsed
import com.casecode.pos.R
import com.casecode.pos.base.BaseAdapter
import com.casecode.pos.base.BaseViewHolder
import com.casecode.pos.databinding.ItemPlansStorageBinding
import timber.log.Timber

class PlanUsedAdapter(val itemCLick: (PlanUsed) -> Unit) :
   BaseAdapter<PlanUsed>(PlanDiffCallback)
{
   
   companion object PlanDiffCallback : DiffUtil.ItemCallback<PlanUsed>()
   {
      
      override fun areItemsTheSame(oldItem: PlanUsed, newItem: PlanUsed): Boolean
      {
         
         return oldItem == newItem
      }
      
      
      override fun areContentsTheSame(oldItem: PlanUsed, newItem: PlanUsed): Boolean
      {
         return oldItem.planName.equals(newItem.planName) && oldItem.planCode == newItem.planCode
      }
      
      
   }
   
   inner class PlanStorageViewHolder(binding: ItemPlansStorageBinding) :
      BaseViewHolder<ItemPlansStorageBinding, PlanUsed>(binding)
   {
      init
      {
         binding.apply {
            itemView.setOnClickListener {
               
               plan?.run {
                  itemCLick(this)
               }
            }
         }
      }
      
      override fun bind(element: PlanUsed)
      {
         super.bind(element)
         Timber.e("element = $element")
         binding.plan = element
      }
      
   }
   
   override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
                                  ): BaseViewHolder<out ViewDataBinding, PlanUsed>
   {
      return PlanStorageViewHolder(
         ItemPlansStorageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
                                        )
                                  )
   }
   
   
   override fun getItemViewType(position: Int): Int
   {
      return R.layout.item_plans_storage
   }
   
   */
/**
    *  parent list is immutable and override here to use mutableList.
    *//*

   override fun submitList(list: MutableList<PlanUsed>?)
   {
      Timber.e("list of plan used  = ${list?.size}")
      
      super.submitList(
         list?.let { ArrayList(it) })
   }
}
*/
