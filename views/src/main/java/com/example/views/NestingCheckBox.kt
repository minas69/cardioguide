package com.example.views

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class NestingCheckBox @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val checkBox: CheckBox
    val flowLayout: FlowLayout

    var label: CharSequence?
        set(value) {
            checkBox.text = value
        }
        get() = checkBox.text

    var isChecked: Boolean
        set(value) {
            checkBox.isChecked = value
        }
        get() = checkBox.isChecked

    init {
        orientation = VERTICAL
        inflate(context, R.layout.layout_nesting_check_box, this)

        checkBox = getChildAt(0) as CheckBox
        flowLayout = getChildAt(1) as FlowLayout
    }

    fun setOnCheckedChangedListener(listener: (Boolean) -> Unit) {
        checkBox.setOnCheckedChangeListener { _, b ->
            listener(b)
        }
    }

}
