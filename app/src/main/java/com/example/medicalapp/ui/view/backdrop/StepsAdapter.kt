package com.example.medicalapp.ui.view.backdrop

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.example.medicalapp.Step
import com.example.medicalapp.R
import com.example.medicalapp.dpToPx
import com.example.medicalapp.inflate
import kotlinx.android.synthetic.main.step_item.view.*

class StepsAdapter(
    val context: Context,
    val data: List<Step>,
    selectedStep: Int = 0,
    val onItemClickListener: (position: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var selected: Int = selectedStep
        set(value) {
            if (field != value) {
                notifyItemChanged(field)
                notifyItemChanged(value)
                field = value
            }
        }

    private var colorWhite = ContextCompat.getColor(context, android.R.color.white)
    private var colorGray = ContextCompat.getColor(context, R.color.lightGrayColor)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
        = ViewHolder(parent.inflate(R.layout.step_item))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        with (holder.itemView) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                number.setNumber(position + 1, position == selected)
                if (position == selected) {
                    title.setTextColor(colorWhite)
                } else {
                    title.setTextColor(colorGray)
                }
            }
            title.text = data[position].name

            setOnClickListener {
                selected = position
                onItemClickListener(position)
            }
        }
    }

    override fun getItemCount() = data.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class JoinItemDecoration : RecyclerView.ItemDecoration() {

        private val JOIN_LENGTH = 24.dpToPx()

        private val paint = Paint().apply {
            color = ContextCompat.getColor(context, R.color.lightGrayColor)
            strokeWidth = 3.0f
        }

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)

            val viewPosition = parent.getChildAdapterPosition(view)
            if (viewPosition == RecyclerView.NO_POSITION) return

            if (viewPosition < itemCount - 1) {
                outRect.bottom += JOIN_LENGTH
            }
        }

        override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            super.onDraw(c, parent, state)

            for (child in parent.children) {
                val viewPosition = parent.getChildAdapterPosition(child)
                if (viewPosition == RecyclerView.NO_POSITION) return

                if (viewPosition < itemCount - 1) {
                    val top = (child.bottom + 8.dpToPx()).toFloat()
                    val left = 40.dpToPx().toFloat()
                    c.drawLine(
                        left,
                        top,
                        left,
                        top + JOIN_LENGTH,
                        paint
                    )
                }
            }
        }

    }
}