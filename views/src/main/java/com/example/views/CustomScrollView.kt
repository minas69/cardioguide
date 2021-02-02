package com.example.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView

class CustomScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : NestedScrollView(context, attrs) {

    private val dividerDrawable = ContextCompat.getDrawable(context, R.drawable.divider)
    private val dividerHeight = 1.dpToPx()
    private val dividerPadding = 24.dpToPx()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (dividerDrawable != null && scrollY > 0) {
            val top = scrollY
            dividerDrawable.setBounds(paddingLeft + dividerPadding, top,
                width - paddingRight - dividerPadding, top + dividerHeight)
            dividerDrawable.draw(canvas)
        }
    }

}
