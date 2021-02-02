package com.example.medicalapp.ui.form.backdrop

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.medicalapp.R

class NumberView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val initial: TextView

    private var number: Int? = null
    private var mSelected = false

    private var colorAccent = ContextCompat.getColor(context, R.color.colorAccent)
    private var colorGray = ContextCompat.getColor(context, R.color.lightGrayColor)

    init {
        inflate(context, R.layout.layout_number_view, this)
        
        initial = getChildAt(0) as TextView
//        clipToOutline = true
    }

    fun setNumber(number: Int?, selected: Boolean) {
        this.number = number
        this.mSelected = selected
        updateView()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        if (!isInEditMode) {
            updateView()
        }
    }

    private fun updateView() {
        if (number != null) {
            if (mSelected) {
                setBackgroundResource(R.drawable.filled_circle)
                initial.setTextColor(colorAccent)
            } else {
                setBackgroundResource(R.drawable.outlined_circle)
                initial.setTextColor(colorGray)
            }
            initial.text = number.toString()
        }
    }

}
