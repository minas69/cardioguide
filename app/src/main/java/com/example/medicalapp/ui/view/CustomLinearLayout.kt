package com.example.medicalapp.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.example.medicalapp.R
import com.example.medicalapp.dpToPx
import com.example.medicalapp.layoutParams
import kotlin.math.max

open class CustomLinearLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    open val dividerHeight: Int = 1.dpToPx()
    open val dividerPaddingHorizontal: Int = 0
    open val dividerPaddingVertical: Int = 0
    private val dividerDrawable = ContextCompat.getDrawable(context, R.drawable.divider)

    init {
        setBackgroundResource(android.R.color.transparent)
    }

    open fun hasDividerBeforeChild(childIndex: Int): Boolean {
        return !allViewsAreGoneBefore(childIndex)
    }

    private fun allViewsAreGoneBefore(childIndex: Int): Boolean {
        for (i in childIndex - 1 downTo 0) {
            val child = getChildAt(i)
            if (child.visibility != View.GONE) {
                return false
            }
        }

        return true
    }

    override fun checkLayoutParams(p: LayoutParams?): Boolean {
        return p is MarginLayoutParams
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun generateLayoutParams(p: LayoutParams?): LayoutParams {
        return generateDefaultLayoutParams()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var totalLength = 0
        var maxWidth = 0
        var childState = 0

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val w = MeasureSpec.getSize(widthMeasureSpec)

        var widthUsed = 0
        var heightUsed = 0
        children.withIndex().forEach { (i, child) ->
            if (child.visibility == View.GONE) {
                return@forEach
            }

            measureChild(child, MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY), heightMeasureSpec)
//            widthUsed = child.measuredWidth
//            heightUsed = child.measuredHeight
            totalLength += child.measuredHeight
            maxWidth = max(maxWidth, child.measuredWidth)

            if (hasDividerBeforeChild(i)) {
                totalLength += dividerHeight + dividerPaddingVertical * 2
            }

            childState = combineMeasuredStates(childState, child.measuredState)
        }

        totalLength += paddingTop + paddingBottom

        val heightSize = totalLength
        val heightSizeAndState = resolveSizeAndState(heightSize, heightMeasureSpec, 0)

        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
            heightSizeAndState)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var childTop = paddingTop

        children.withIndex().forEach { (i, child) ->
            if (child.visibility == View.GONE) {
                return@forEach
            }

            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight

            val childLeft = paddingLeft

            if (hasDividerBeforeChild(i)) {
                childTop += dividerHeight + dividerPaddingVertical * 2
            }

            child.setChildFrame(childLeft, childTop, childWidth, childHeight)
            childTop += childHeight
        }
    }

    private fun View.setChildFrame(left: Int, top: Int, width: Int, height: Int) {
        layout(left, top, left + width, top + height)
    }

    override fun onDraw(canvas: Canvas) {
        if (dividerDrawable == null) {
            return
        }

        children.withIndex().forEach { (i, child) ->
            if (child.visibility != GONE) {
                if (hasDividerBeforeChild(i)) {
                    child.layoutParams<MarginLayoutParams> {
                        val top = child.top - topMargin - dividerHeight - dividerPaddingVertical
                        drawDivider(canvas, dividerDrawable, top)
                    }
                }
            }
        }
    }

    private fun drawDivider(canvas: Canvas, drawable: Drawable, top: Int) {
        drawable.setBounds(paddingLeft + dividerPaddingHorizontal, top,
            width - paddingRight - dividerPaddingHorizontal, top + dividerHeight)
        drawable.draw(canvas)
    }

}
