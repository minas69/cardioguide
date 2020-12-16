package com.example.medicalapp.ui.view.backdrop

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import com.example.medicalapp.R
import kotlinx.android.synthetic.main.number_view.view.*

@RequiresApi(Build.VERSION_CODES.M)
class NumberView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private var number: Int? = null
    private var mSelected = false

    private var colorAccent = context.getColor(R.color.colorAccent)
    private var colorGray = context.getColor(R.color.lightGrayColor)

    init {
        inflate(context, R.layout.number_view, this)
        clipToOutline = true
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
