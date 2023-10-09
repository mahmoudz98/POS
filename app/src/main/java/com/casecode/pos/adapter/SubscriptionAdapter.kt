package com.casecode.pos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.casecode.domain.model.subscriptions.Subscription
import com.casecode.pos.R
import com.casecode.pos.base.BaseAdapter
import com.casecode.pos.base.BaseViewHolder
import com.casecode.pos.databinding.ItemSubscriptionBinding
import timber.log.Timber

class SubscriptionAdapter(val itemClick: (Subscription) -> Unit) :
   BaseAdapter<Subscription>(SubscriptionDiffCallback)
{
   companion object SubscriptionDiffCallback : DiffUtil.ItemCallback<Subscription>()
   {
      
      override fun areItemsTheSame(oldItem: Subscription, newItem: Subscription): Boolean
      {
         
         return oldItem === newItem
      }
      
      
      override fun areContentsTheSame(oldItem: Subscription, newItem: Subscription): Boolean
      {
         return oldItem == newItem
      }
      
      
   }
   
   inner class SubscriptionViewHolder(binding: ItemSubscriptionBinding) :
      BaseViewHolder<ItemSubscriptionBinding, Subscription>(binding)
   {
      init
      {
         binding.apply {
            // TODO: custom to add configure with google play.
            // btnPlansPay.setOnClickListener {
            itemView.setOnClickListener {
               
               subscription?.run {
                  itemClick(this)
               }
            }
         }
      }
      
      override fun bind(element: Subscription)
      {
         super.bind(element)
         Timber.e("element = $element")
         binding.subscription = element
      }
   }
   
   override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
                                  ): BaseViewHolder<out ViewDataBinding, Subscription>
   {
      return SubscriptionViewHolder(
         ItemSubscriptionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
                                        )
                                   )
   }
   
   
   override fun getItemViewType(position: Int): Int
   {
      return R.layout.item_subscription
   }
   
}