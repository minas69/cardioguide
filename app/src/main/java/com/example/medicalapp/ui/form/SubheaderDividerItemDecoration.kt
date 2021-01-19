package com.example.medicalapp.ui.form

import android.content.Context
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.medicalapp.R
import com.example.medicalapp.dpToPx

class SubheaderDividerItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val dividerDrawable = ContextCompat.getDrawable(context, R.drawable.divider)
    private val dividerHeight = 1.dpToPx()
    private val dividerPadding
        = context.resources.getDimension(R.dimen.activity_horizontal_margin).toInt()

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val drawable = dividerDrawable ?: return
        val layoutManager = parent.layoutManager ?: return

        val scrollY = layoutManager.computeVerticalScrollOffset(state)
        if (scrollY > 0) {
            drawable.setBounds(dividerPadding, 0, parent.width - dividerPadding, dividerHeight)
            drawable.draw(c)
        }
    }

}