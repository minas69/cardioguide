package com.example.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.EditText
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import java.text.DateFormat
import java.time.LocalDate
import java.util.*

class DatePicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val textInputLayout: TextInputLayout
    private val editText: EditText
    private var picker: MaterialDatePicker<Long>? = null
    private var fragmentManager: FragmentManager? = null
    private var onTextChangedListener: ((LocalDate) -> Unit)? = null

    var inputType: Int
        set(value) {
            editText.inputType = value
        }
        get() = editText.inputType

    var hint: CharSequence?
        set(value) {
            textInputLayout.hint = value
        }
        get() = textInputLayout.hint

    var inputText: CharSequence?
        set(value) {
            editText.setText(value)
        }
        get() = editText.text

    var suffix: CharSequence?
        set(value) {
            textInputLayout.suffixText = value
        }
        get() = textInputLayout.suffixText

    init {
        inflate(context, R.layout.layout_text_field, this)

        textInputLayout = getChildAt(0) as TextInputLayout
        editText = textInputLayout.editText!!

        editText.isFocusable = false

        editText.setOnClickListener {
            picker?.show(fragmentManager!!, picker.toString())
        }
    }

    fun setOnDateChangedListener(fragmentManager: FragmentManager, listener: (Long) -> Unit) {
        val builder = MaterialDatePicker.Builder.datePicker()
        val datePicker = builder.build()
        datePicker.addOnPositiveButtonClickListener {
            editText.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(Date(it)))
            listener(it)
        }
        picker = datePicker
        this.fragmentManager = fragmentManager
    }

//    fun afterTextChanged(afterTextChanged: (CharSequence) -> Unit) {
//        editText.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(editable: Editable) {
//                afterTextChanged(editable)
//            }
//
//            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
//        })
//    }

}