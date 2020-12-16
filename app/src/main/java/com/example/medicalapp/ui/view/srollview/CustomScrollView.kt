package com.example.medicalapp.ui.view.srollview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.widget.ScrollView
import androidx.annotation.RequiresApi
import androidx.core.widget.NestedScrollView
import com.example.medicalapp.R
import com.example.medicalapp.dpToPx

class CustomScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : NestedScrollView(context, attrs) {

    @RequiresApi(Build.VERSION_CODES.M)
    private val paint = Paint().apply {
        color = context.getColor(R.color.darkGrayColor)
        strokeWidth = 6.0f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (scrollY > 0) {
                val top = scrollY.toFloat()
                val startX = 24.dpToPx().toFloat()
                val stopX = (width - 24.dpToPx()).toFloat()
                canvas.drawLine(startX, top, stopX, top, paint)
            }
        }
    }

}
