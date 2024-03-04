package com.casecode.pos.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckedTextView

class AutoCompleteAdapter(context: Context, items: List<String?>) :
    ArrayAdapter<String?>(context, android.R.layout.simple_list_item_single_choice, items) {
    private var selectedItemPosition = -1

    fun setSelectedItem(position: Int) {
        selectedItemPosition = position
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)

        val itemText = getItem(position)
        val textView = view.findViewById<CheckedTextView>(android.R.id.text1)

        // Check if the item is selected
        if (position == selectedItemPosition) {
            textView.isChecked = true
        } else {
            textView.isChecked = false

        }

        textView.text = itemText

        return view
    }
}