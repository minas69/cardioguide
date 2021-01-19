package com.example.views

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class CheckBoxSection @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val labelTextView: TextView
    private val itemsLayout: LinearLayout
    val flowLayout: FlowLayout

    private var onCheckedChangeListener: ((List<Int>?) -> Unit)? = null

    var label: CharSequence?
        set(value) {
            labelTextView.text = value
        }
        get() = labelTextView.text

    var items: List<String> = listOf()
        set(value) {
            field = value
            applyItems(value)
        }

    private var _checked = mutableListOf<Int>()
    val checked: List<Int>
        get() = _checked

    init {
        orientation = VERTICAL
        inflate(context, R.layout.layout_check_box_section, this)

        labelTextView = getChildAt(0) as TextView
        itemsLayout = getChildAt(1) as LinearLayout
        flowLayout = getChildAt(2) as FlowLayout
    }

    private fun applyItems(items: List<String>) {
        itemsLayout.removeAllViews()
        for ((j, item) in items.withIndex()) {
            (inflateCheckBox()).run {
                id = j
                text = item
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        _checked.add(j)
                    } else {
                        _checked.remove(j)
                    }
                    dispatchToListener()
                }
                itemsLayout.addView(this)
            }
        }
    }

    private fun dispatchToListener() {
        onCheckedChangeListener?.invoke(if (checked.isNotEmpty()) checked else null)
    }

    fun setOnCheckedChangeListener(listener: (List<Int>?) -> Unit) {
        onCheckedChangeListener = listener
    }

    private fun inflateCheckBox() =
        (inflate(context, R.layout.layout_check_box, null) as CheckBox)

}
