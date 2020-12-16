package com.example.medicalapp.ui.view.backdrop

import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.widget.Toolbar

internal class BackdropUtils {

    fun findToolbar(viewGroup: ViewGroup): Toolbar? {
        for (chileId in 0..viewGroup.childCount) {
            val childView = viewGroup.getChildAt(chileId)
            if (childView is Toolbar) {
                return childView
            }
        }

        return null
    }

    fun fadeIn(view: View, animationDuration: Long) {
        if (view.alpha == 1.0f) {
            view.visibility = View.VISIBLE
            return
        }
        view.animate()
            .withStartAction { view.visibility = View.VISIBLE }
            .setInterpolator(AccelerateDecelerateInterpolator())
            .alpha(1.0f)
            .setDuration(animationDuration)
            .start()
    }

    fun fadeOut(view: View, animationDuration: Long) {
        if (view.alpha == 0.0f) {
            view.visibility = View.GONE
            return
        }
        view.animate()
            .withStartAction { view.visibility = View.VISIBLE }
            .withEndAction { view.visibility = View.GONE }
            .setInterpolator(AccelerateDecelerateInterpolator())
            .alpha(0.0f)
            .setDuration(animationDuration)
            .start()
    }

}