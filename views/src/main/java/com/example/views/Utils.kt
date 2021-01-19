package com.example.views

import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup

inline fun <reified T : ViewGroup.LayoutParams> View.layoutParams(block: T.() -> Unit) {
    if (layoutParams is T) block(layoutParams as T)
}

fun Int.dpToPx(): Int {
    return this.toFloat().dpToPx()
}

fun Float.dpToPx(): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    ).toInt()
}
