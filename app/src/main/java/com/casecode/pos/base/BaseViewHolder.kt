package com.casecode.pos.base

import android.content.Context
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<VB: ViewDataBinding, E : Any>(protected open val binding : VB)
    :RecyclerView.ViewHolder(binding.root){
    val context: Context
        get() {
            return itemView.context
        }
    val absolutePosition : Int get(){
       return absoluteAdapterPosition
    }

    lateinit var element:E
    open fun bind(element: E){
        this.element = element
        binding.executePendingBindings()
    }
}