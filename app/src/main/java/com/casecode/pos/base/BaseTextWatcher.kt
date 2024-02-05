package com.casecode.pos.base

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText

abstract class BaseTextWatcher : TextWatcher {
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun afterTextChanged(p0: Editable?) {}
}

inline fun EditText.doAfterTextChangedListener(
     crossinline afterTextChanged: (text: Editable?) -> Unit
                                              ) {
    
    val textWatcher = object : BaseTextWatcher()
    {
        override fun afterTextChanged(p0: Editable?) {
            afterTextChanged.invoke(p0)
        }
        
        override fun beforeTextChanged(p0: CharSequence?, p1: Int,
                                       p2: Int, p3: Int) {}
        
        override fun onTextChanged(text: CharSequence?, start: Int, before:
        Int, count: Int) {}
    }
    
    onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
        if (hasFocus) {
            addTextChangedListener(textWatcher)
        } else {
            removeTextChangedListener(textWatcher)
        }
    }
    
}