package com.example.medicalapp.ui.view

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.text.InputType
import android.util.AttributeSet
import android.view.WindowInsets
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import com.example.medicalapp.*
import com.google.android.material.textfield.TextInputLayout

class BlockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        orientation = VERTICAL
    }

    var data: Block? = null
        set(value) {
            field = value
            if (value != null) {
                updateView(value)
            }
        }

    private fun updateView(data: Block) {
        removeAllViews()

        for ((i, input) in data.attributes.withIndex()) {
            val view = when (input) {
                is TextInput -> {
                    (inflate(context, R.layout.text_field, null) as TextInputLayout).apply {
                        hint = input.name
//                        editText?.id = i shl 16
                    }
                }
                is DateInput -> {
                    (inflate(context, R.layout.text_field, null) as TextInputLayout).apply {
                        hint = input.name
                        editText?.apply {
//                            id = i shl 16
                            inputType = InputType.TYPE_CLASS_DATETIME
                        }
                    }
                }
                is OptionsInput -> {
                    (inflate(context, R.layout.dropdown_menu, null) as TextInputLayout).apply {
                        hint = input.name
                        val items = input.options
                        val adapter = ArrayAdapter(context, R.layout.list_gender, items)
                        (editText as? AutoCompleteTextView)?.apply {
//                            id = i shl 16
                            setAdapter(adapter)
                        }
                    }
                }
                is IntegerInput -> {
                    (inflate(context, R.layout.text_field, null) as TextInputLayout).apply {
                        hint = input.name
                        editText?.apply {
//                            id = i shl 16
                            inputType = InputType.TYPE_CLASS_NUMBER
                        }
                    }
                }
            }
//            addView(view.apply { id = i })
            addView(view)
            val horizontalMargin =
                context.resources.getDimension(R.dimen.activity_horizontal_margin).toInt()
            view.margin(
                top = 4.dpToPx(),
                left = horizontalMargin,
                right = horizontalMargin,
                bottom = 4.dpToPx()
            )

        }
    }

    override fun onSaveInstanceState(): Parcelable {
        return SavedState(super.onSaveInstanceState()).apply {
            data?.attributes?.let { attributes ->
                for ((i, attribute) in attributes.withIndex()) {
                    if (attribute !is OptionsInput) {
                        val view = getChildAt(i) as TextInputLayout
                        inputs.add(view.editText!!.text.toString())
                    }
                }
            }
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            val attributes = data?.attributes ?: return
            var i = 0
            for (attribute in attributes) {
                if (attribute !is OptionsInput) {
                    val view = getChildAt(i) as TextInputLayout
                    view.editText!!.setText(state.inputs[i])
                    i += 1
                }
            }
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        padding(
            left = insets.systemWindowInsetLeft,
            top = insets.systemWindowInsetTop,
            right = insets.systemWindowInsetRight,
            bottom = insets.systemWindowInsetBottom
        )

        return insets.consumeSystemWindowInsets()
    }

    class SavedState : BaseSavedState {

        val inputs = mutableListOf<String>()

        constructor(source: Parcel) : super(source) {
            inputs.clear()
            (0..16).forEach {
                inputs.add(source.readString() ?: "")
            }
        }

        constructor(superState: Parcelable?) : super(superState)

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)

            for (input in inputs) {
                out.writeString(input)
            }
        }

        companion object {
            @JvmStatic
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {

                override fun createFromParcel(source: Parcel): SavedState {
                    return SavedState(source)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }
}