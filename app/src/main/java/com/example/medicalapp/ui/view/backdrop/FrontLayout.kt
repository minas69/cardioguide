package com.example.medicalapp.ui.view.backdrop

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.TextViewCompat
import com.example.medicalapp.R
import com.example.medicalapp.dpToPx
import com.example.medicalapp.margin
import com.example.medicalapp.padding
import com.google.android.material.textview.MaterialTextView

class FrontLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var backgroundDimAlpha = 0.0f
    set(value) {
        field = value
        invalidate()
    }
    private val backgroundDim = ContextCompat.getDrawable(context, R.drawable.backdrop_background)

    private var subheader: MaterialTextView

    init {
        orientation = VERTICAL

        setBackgroundResource(R.drawable.backdrop_background)

        subheader = MaterialTextView(context).apply {
            width = MATCH_PARENT
            height = WRAP_CONTENT
            ellipsize = TextUtils.TruncateAt.END
            textSize = context.resources.getDimension(R.dimen.subheader_text_size)
            maxLines = 1
            isSingleLine = true
            TextViewCompat.setTextAppearance(this, R.style.TextAppearance_AppCompat_Title)
        }
        addView(subheader)

        val horizontalMargin = context.resources.getDimension(R.dimen.activity_horizontal_margin).toInt()
        with (subheader) {
            margin(left = horizontalMargin, right = horizontalMargin)
            padding(top = 16.dpToPx(), bottom = 16.dpToPx())
        }
    }

    override fun onDrawForeground(canvas: Canvas) {
        super.onDrawForeground(canvas)

        backgroundDim?.run {
            setBounds(0, 0, width, height)
            alpha = (backgroundDimAlpha * 255).toInt()
            draw(canvas)
        }
    }

    fun setSubheaderTitle(title: String) {
        subheader.text = title
    }

    fun getSubheaderHeight() = subheader.bottom
}