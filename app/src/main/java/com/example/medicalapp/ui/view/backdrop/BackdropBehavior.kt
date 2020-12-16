package com.example.medicalapp.ui.view.backdrop

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.IdRes
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.children
import com.example.medicalapp.R


class BackdropBehavior : CoordinatorLayout.Behavior<View> {

    enum class DropState {
        OPEN,
        CLOSE
    }

    interface OnDropListener {

        fun onDrop(dropState: DropState, fromUser: Boolean)
    }

    companion object {
        private const val DEFAULT_DURATION = 200L
        private const val WITHOUT_DURATION = 0L
        private val DEFAULT_DROP_STATE = DropState.CLOSE

        private const val ARG_DROP_STATE = "arg_drop_state"
    }

    private var utils = BackdropUtils()

    private var toolbarId: Int? = null
    private var backLayoutId: Int? = null

    private var toolbar: Toolbar? = null
    private var backLayout: ViewGroup? = null
    private var frontLayout: ViewGroup? = null

    private var topPosition: Int = 0
    private var bottomPosition: Int = 0
    private var bottomInset: Int = 0

    private var iconRes: Int = R.drawable.ic_close

    private var dropState: DropState = DEFAULT_DROP_STATE

    private var needToInitializing = true

    private var frontViewDown = false
    private var frontViewMove = false

    private var dropListeners = mutableListOf<OnDropListener>()

    constructor() : super()

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onSaveInstanceState(parent: CoordinatorLayout, child: View): Parcelable {
        return Bundle().apply {
            putSerializable(ARG_DROP_STATE, dropState)
        }
    }

    override fun onRestoreInstanceState(parent: CoordinatorLayout, child: View, state: Parcelable) {
        super.onRestoreInstanceState(parent, child, state)

        dropState =
            (state as? Bundle)?.getSerializable(ARG_DROP_STATE) as? DropState ?: DEFAULT_DROP_STATE
    }

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        if (toolbarId == null && backLayoutId == null) return false

        return when (dependency.id) {
            toolbarId -> true
            backLayoutId -> true
            else -> false
        }
    }

    override fun onInterceptTouchEvent(
        parent: CoordinatorLayout,
        child: View,
        event: MotionEvent
    ): Boolean {
        if (dropState == DropState.CLOSE) {
            return false
        }

        if (event.action == MotionEvent.ACTION_DOWN) {
            frontLayout?.let {
                frontViewDown = event.y >= it.y
            }
        }

        return frontViewDown
    }

    override fun onTouchEvent(parent: CoordinatorLayout, child: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (frontViewDown && !frontViewMove) {
                    close()
                }

                frontViewMove = false
                frontViewDown = false
            }
            MotionEvent.ACTION_MOVE -> {
                frontViewMove = true
            }
        }

        return frontViewDown
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {

        this.frontLayout = child as? ViewGroup
            ?: throw IllegalArgumentException("BackLayout must extend a ViewGroup")

        when (dependency.id) {
            toolbarId -> toolbar = dependency as? Toolbar
                ?: throw IllegalArgumentException("toolbarId doesn't match Toolbar")

            backLayoutId -> {
                backLayout = dependency as? ViewGroup
                    ?: throw IllegalArgumentException("backLayoutId doesn't match back Layout")

                // TODO (next release): remove this conditional
                if (toolbarId == null) {
                    toolbar = utils.findToolbar(backLayout!!)
                        ?: throw IllegalArgumentException("AppBarLayout mast contain a Toolbar!")
                }
            }
        }

        if (toolbar != null && frontLayout != null && backLayout != null && needToInitializing) {
            initViews(parent, frontLayout!!, toolbar!!, backLayout!!)
        }

        val a = super.onDependentViewChanged(parent, child, dependency)

        return a
    }

    fun setIcon(@IdRes iconRes: Int) {
        this.iconRes = iconRes
    }

    fun setBottomInset(bottomInset: Int) {
        this.bottomInset = bottomInset
        needToInitializing = true
    }

    /**
     * Attach back layout to Backdrop.
     * BackDropLayout must contain a [Toolbar]
     */
    fun attachBackLayout(@IdRes appBarLayoutId: Int) {
        this.backLayoutId = appBarLayoutId
    }

    /**
     * @deprecated — use [BackdropBehavior.attachBackLayout]. This method will be removed in version 0.1.7+
     */
    @Deprecated("Use BackdropBehavior.attachBackLayout")
    fun attachToolbar(@IdRes toolbarId: Int) {
        this.toolbarId = toolbarId
    }

    /**
     * @deprecated — use [BackdropBehavior.attachBackLayout]. This method will be removed in version 0.1.7+
     */
    @Deprecated("Use BackdropBehavior.attachBackLayout")
    fun attachBackContainer(@IdRes backContainerId: Int) {
        this.backLayoutId = backContainerId
    }

    fun addOnDropListener(listener: OnDropListener) {
        dropListeners.add(listener)
    }

    fun removeDropListener(listener: OnDropListener) {
        dropListeners.remove(listener)
    }

    fun open(withAnimation: Boolean = true): Boolean = if (dropState == DropState.OPEN) {
        false
    } else {
        dropState = DropState.OPEN
        if (backLayout != null && toolbar != null && frontLayout != null) {
            drawDropState(frontLayout!!, toolbar!!, backLayout!!, withAnimation)
        } else {
            throw IllegalArgumentException("Toolbar and backContainer must be initialized")
        }
        notifyListeners(false)
        true
    }

    fun close(withAnimation: Boolean = true): Boolean = if (dropState == DropState.CLOSE) {
        false
    } else {
        dropState = DropState.CLOSE
        if (backLayout != null && toolbar != null && frontLayout != null) {
            drawDropState(frontLayout!!, toolbar!!, backLayout!!, withAnimation)
        } else {
            throw IllegalArgumentException("Toolbar and backContainer must be initialized")
        }
        notifyListeners(false)
        true
    }

    private fun initViews(
        parent: CoordinatorLayout,
        frontLayout: ViewGroup,
        toolbar: Toolbar,
        backLayout: ViewGroup
    ) {

        topPosition = backLayout.top + toolbar.height
        bottomPosition = backLayout.top + backLayout.height
        if (bottomPosition > parent.height - bottomInset) {
            bottomPosition = parent.height - bottomInset
            if (this.frontLayout is FrontLayout) {
                bottomPosition -= (this.frontLayout!! as FrontLayout).getSubheaderHeight()
            }
        }
        backLayout.layoutParams.height = bottomPosition - backLayout.top
        frontLayout.layoutParams.height = parent.height - topPosition
        drawDropState(frontLayout, toolbar, backLayout, false)

        with(toolbar) {
            setNavigationIcon(iconRes)
            setNavigationOnClickListener {
//                dropState = when (dropState) {
//                    DropState.CLOSE -> DropState.OPEN
//                    DropState.OPEN -> DropState.CLOSE
//                }
                dropState = DropState.OPEN
                drawDropState(frontLayout, toolbar, backLayout)
                notifyListeners(true)
            }
        }

        needToInitializing = false
    }

    private fun drawDropState(
        frontLayout: ViewGroup,
        toolbar: Toolbar,
        backContainer: ViewGroup,
        withAnimation: Boolean = true
    ) {
        when (dropState) {
            DropState.CLOSE -> {
                drawClosedState(frontLayout, backContainer, toolbar, withAnimation)
//                toolbar.setNavigationIcon(closedIconId)
            }
            DropState.OPEN -> {
                drawOpenedState(frontLayout, backContainer, toolbar, withAnimation)
//                toolbar.setNavigationIcon(openedIconRes)
            }
        }
    }

    private fun drawClosedState(
        frontLayout: ViewGroup,
        backLayout: ViewGroup,
        toolbar: Toolbar,
        withAnimation: Boolean = true
    ) {
        val position = topPosition.toFloat()
        val duration = if (withAnimation) DEFAULT_DURATION else WITHOUT_DURATION

        frontLayout
            .animate()
            .setInterpolator(AccelerateDecelerateInterpolator())
//            .withDim()
            .y(position)
            .setDuration(duration)
            .start()

        utils.fadeIn(toolbar, duration)
        for (child in backLayout.children) {
            if (child !is Toolbar) {
                utils.fadeOut(child, duration)
            }
        }

        if (frontLayout is FrontLayout) {
            frontLayout.backgroundDimAlpha = 0.0f
        }
    }

    private fun drawOpenedState(
        frontLayout: ViewGroup,
        backLayout: ViewGroup,
        toolbar: Toolbar,
        withAnimation: Boolean = true
    ) {
        val position = bottomPosition.toFloat()
        val duration = if (withAnimation) DEFAULT_DURATION else WITHOUT_DURATION

        frontLayout
            .animate()
            .setInterpolator(AccelerateDecelerateInterpolator())
//            .withDim()
            .y(position)
            .setDuration(duration)
            .start()

        utils.fadeOut(toolbar, duration)
        for (child in backLayout.children) {
            if (child !is Toolbar) {
                utils.fadeIn(child, duration)
            }
        }
    }

    private fun ViewPropertyAnimator.withDim(): ViewPropertyAnimator {
        if (frontLayout is FrontLayout) {
            val view = frontLayout as FrontLayout
            val top = topPosition
            val bottom = bottomPosition
            setUpdateListener {
                val currentPosition = view.y
                val alpha = 0.6f * ((currentPosition - top) / (bottom - top))
                view.backgroundDimAlpha = alpha
            }
        }

        return this
    }

//    private fun getBottomPosition(): Float = bottomPosition -

    private fun notifyListeners(fromUser: Boolean) {
        dropListeners.forEach { listener ->
            listener.onDrop(dropState, fromUser)
        }
    }
}