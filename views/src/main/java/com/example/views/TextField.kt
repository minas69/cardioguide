package com.example.views

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class TextField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val textInputLayout: TextInputLayout
    private val editText: EditText
    private var onTextChangedListener: ((CharSequence) -> Unit)? = null

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

        editText.addTextChangedListener {
            if (it != null) {
                onTextChangedListener?.invoke(it)
            }
        }
    }

    fun clear() {
        textInputLayout.isHintAnimationEnabled = false
        editText.text.clear()
        textInputLayout.isHintAnimationEnabled = true
    }

    fun setOnTextChangedListener(listener: (CharSequence) -> Unit) {
        onTextChangedListener = listener
    }

    fun afterTextChanged(afterTextChanged: (CharSequence) -> Unit) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable) {
                afterTextChanged(editable)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

}
