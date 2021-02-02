package com.example.medicalapp.ui.form.backdrop

import android.content.Context
import android.graphics.Canvas
import android.text.TextUtils
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import com.example.medicalapp.R
import com.example.medicalapp.margin
import com.example.views.dpToPx
import com.example.views.padding
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

    var subheader: TextView

    init {
        orientation = VERTICAL

        setBackgroundResource(R.drawable.backdrop_background)

        subheader = MaterialTextView(context).apply {
            width = MATCH_PARENT
            height = WRAP_CONTENT
            ellipsize = TextUtils.TruncateAt.END
            maxLines = 1
            isSingleLine = true
            TextViewCompat.setTextAppearance(this, R.style.AppTheme_TextAppearance_Title)
            
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

    fun getSubheaderHeight() = subheader.bottom
}