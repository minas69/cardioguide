package com.example.views

import android.content.Context
import android.graphics.Color
import android.text.*
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class RadioSection @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val labelTextView: TextView
    private val radioGroup: RadioGroup
    val flowLayout: FlowLayout

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

    var checked: Int? = null

    var isRequired: Boolean = false
        set(value) {
            field = value
            val labelText = label
            if (value && labelText != null) {
                val spannable = SpannableString("$labelText*")
                spannable.setSpan(ForegroundColorSpan(Color.RED), labelText.length, labelText.length + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                labelTextView.setText(spannable, TextView.BufferType.SPANNABLE)
            }
        }

    init {
        orientation = VERTICAL
        inflate(context, R.layout.layout_radio_section, this)

        labelTextView = getChildAt(0) as TextView
        radioGroup = getChildAt(1) as RadioGroup
        flowLayout = getChildAt(2) as FlowLayout
    }

    private fun applyItems(items: List<String>) {
        radioGroup.removeAllViews()
        for ((j, item) in items.withIndex()) {
            inflateRadioButton().run {
                id = j
                text = item
                radioGroup.addView(this)
                layoutParams.width = LayoutParams.MATCH_PARENT
            }
        }
    }

    fun clearSelection() {
        radioGroup.clearCheck()
    }

    fun setOnCheckedChangedListener(listener: (Int) -> Unit) {
        radioGroup.setOnCheckedChangeListener { _, i ->
            checked = i
            listener(i)
        }
    }

    private fun inflateRadioButton() =
        (inflate(context, R.layout.layout_radio_section_item, null) as RadioButton)

}
