/*
package com.casecode.pos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.casecode.pos.R
import com.casecode.pos.databinding.ItemAutoCompleteBinding

import timber.log.Timber

class AutoCompleteAdapter(context: Context, items: MutableList<String?>) :
    ArrayAdapter<String?>(context, R.layout.item_auto_complete, items) {
    private var  selectedItemPosition:Int = -1

    fun setSelectedItem(position: Int) {
        selectedItemPosition = position

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemAutoCompleteBinding = from(convertView, inflater, parent)

        Timber.e("getView")
        val itemText = getItem(position)
        binding.item = itemText
        binding.text1.isChecked = position == selectedItemPosition
        return binding.root
    }
    companion object{
        private fun from(
            convertView: View?,
            inflater: LayoutInflater,
            parent: ViewGroup,
        ): ItemAutoCompleteBinding {
            val binding: ItemAutoCompleteBinding = if (convertView == null) {
                DataBindingUtil.inflate(inflater, R.layout.item_auto_complete, parent, false)
            } else {
                DataBindingUtil.getBinding(convertView)!!
            }
            return binding
        }
    }
}*/