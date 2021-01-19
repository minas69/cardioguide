package com.example.views

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.FrameLayout
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout

class DropdownField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val textInputLayout: TextInputLayout
    private val autoCompleteTextView: AutoCompleteTextView

    var hint: CharSequence?
        set(value) {
            textInputLayout.hint = value
        }
        get() = textInputLayout.hint

    var items: List<String> = listOf()
        set(value) {
            field = value
            applyItems(value)
        }

//    var suffix: CharSequence?
//        set(value) {
//            textInputLayout.suffixText = value
//        }
//        get() = textInputLayout.suffixText

    init {
        inflate(context, R.layout.layout_dropdown_field, this)

        textInputLayout = getChildAt(0) as TextInputLayout
        autoCompleteTextView = textInputLayout.editText as AutoCompleteTextView
    }

    fun clearSelection() {
        textInputLayout.isHintAnimationEnabled = false
        autoCompleteTextView.setText(null, false)
        textInputLayout.isHintAnimationEnabled = true
    }

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        autoCompleteTextView.setOnItemClickListener { _, _, i, _ ->
            listener(i)
        }
    }

    private fun applyItems(items: List<String>) {
        val adapter = ArrayAdapter(context, R.layout.layout_dropdown_field_item, items)
        autoCompleteTextView.setAdapter(adapter)
    }

}
