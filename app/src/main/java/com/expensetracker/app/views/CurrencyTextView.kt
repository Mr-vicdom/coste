package com.expensetracker.app.views

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView

const val CURRENCY_SYMBOL =  "₹"

class CurrencyTextView(context: Context,attributeSet: AttributeSet)
    : androidx.appcompat.widget.AppCompatTextView(context,attributeSet) {

    override fun setText(text: CharSequence?, type: BufferType?) {
        if (text != null && !text.startsWith(CURRENCY_SYMBOL)) {
            val updatedText = "$CURRENCY_SYMBOL $text"
            super.setText(updatedText, type)
        } else  super.setText(text, type)
    }
}