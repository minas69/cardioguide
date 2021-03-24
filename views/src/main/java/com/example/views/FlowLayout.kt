package com.example.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.children
import kotlin.math.max

class FlowLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    var adapter: Adapter? = null
        set(value) {
            field = value
            createViews()
        }

    private val dividerHeight: Int = 1.dpToPx()
    private val dividerPaddingHorizontal: Int = 0
    private val dividerPaddingVertical: Int = 0
    private val dividerDrawable = ContextCompat.getDrawable(context, R.drawable.divider)

    init {
        setBackgroundResource(android.R.color.transparent)
    }

    private fun createViews() {
        removeAllViews()

        val adapterNotNull = adapter ?: return
        val viewCount = adapterNotNull.getChildCount()
        repeat(viewCount) { position ->
            val view = adapterNotNull.onCreateView(this, position)
            addView(view)
        }
    }

    fun hasDividerBeforeChild(childIndex: Int): Boolean {
//        return !allViewsAreGoneBefore(childIndex)
        return false
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

//        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
//        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val w = MeasureSpec.getSize(widthMeasureSpec)

//        var widthUsed = 0
//        var heightUsed = 0
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

    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>?) {
        dispatchFreezeSelfOnly(container)
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>?) {
        dispatchThawSelfOnly(container)
    }

    override fun onSaveInstanceState(): Parcelable {
        return SavedState(super.onSaveInstanceState()).apply {
            childrenStates = saveChildViewStates()
        }
    }

    private fun saveChildViewStates() = arrayListOf<SparseArray<Parcelable>>().apply {
        children.forEach { child ->
            val container = SparseArray<Parcelable>()
            child.saveHierarchyState(container)
            add(container)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        when (state) {
            is SavedState -> {
                super.onRestoreInstanceState(state.superState)
                state.childrenStates?.let { restoreChildViewStates(it) }
            }
            else -> super.onRestoreInstanceState(state)
        }
    }

    private fun restoreChildViewStates(childViewStates: List<SparseArray<Parcelable>>) {
        for ((i, child) in children.withIndex()) {
            child.restoreHierarchyState(childViewStates[i])
        }
    }

    @Suppress("DEPRECATION")
    @RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        setPadding(
            insets.systemWindowInsetLeft,
            insets.systemWindowInsetTop,
            insets.systemWindowInsetRight,
            insets.systemWindowInsetBottom
        )

        return insets.consumeSystemWindowInsets()
    }

    @Suppress("UNCHECKED_CAST", "unused")
    internal class SavedState : BaseSavedState {

        var childrenStates: List<SparseArray<Parcelable>>? = null

        constructor(superState: Parcelable?) : super(superState)

        constructor(source: Parcel) : super(source) {
            val size = source.readInt()
            if (size > 0) {
                val states = mutableListOf<SparseArray<Parcelable>>()
                repeat(size) {
                    val container = source.readSparseArray<Parcelable>(javaClass.classLoader)
                    if (container != null) {
                        states.add(container)
                    }
                }

                childrenStates = states
            }
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)

            out.writeInt(childrenStates?.size ?: 0)
            childrenStates?.forEach {
                out.writeSparseArray(it)
            }
        }

        companion object {
            @JvmField
            val CREATOR = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(source: Parcel) = SavedState(source)
                override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
            }
        }
    }

    interface Adapter {

        fun onCreateView(parent: ViewGroup, position: Int): View

        fun getChildCount(): Int

    }

}
