package com.example.medicalapp.ui.view.inputs

import android.content.Context
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.text.InputType
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.children
import com.example.medicalapp.*
import com.example.medicalapp.CheckBox
import com.example.medicalapp.RadioGroup
import com.example.medicalapp.ui.view.CustomLinearLayout
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.checkbox.view.*
import kotlinx.android.synthetic.main.checkbox.view.optionalInputLayout
import kotlinx.android.synthetic.main.radio_group.view.*

class InputLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CustomLinearLayout(context, attrs, defStyleAttr) {

    override val dividerPaddingHorizontal: Int = 24.dpToPx()
    override val dividerPaddingVertical: Int = 8.dpToPx()

    var data: List<Input>? = null
        set(value) {
            field = value
            updateView()
        }

    private fun updateView() {
        removeAllViews()

        val inputs = data ?: return

        for (input in inputs) {
            val resId = when (input) {
                is TextInput -> R.layout.text_field
                is DateInput -> R.layout.text_field
                is OptionsInput -> R.layout.dropdown_menu
                is IntegerInput -> R.layout.text_field
                is CheckBox -> R.layout.checkbox
                is RadioGroup -> R.layout.radio_group
                is CheckboxGroup -> R.layout.checkbox_group
            }
            with(inflate(context, resId, null)) {
                addView(this)
                bindView(this, input)
            }
        }
    }

    private fun bindView(view: View, input: Input) {
        when (input) {
            is TextInput -> with(view as TextInputLayout) {
                hint = input.name
                editText?.afterTextChanged { input.input = it }
            }
            is DateInput -> with(view as TextInputLayout) {
                hint = input.name
                editText?.apply {
                    inputType = InputType.TYPE_CLASS_DATETIME
                    afterTextChanged { input.input = it }
                }
            }
            is OptionsInput -> with(view as TextInputLayout) {
                hint = input.name
                val items = input.options
                val adapter = ArrayAdapter(context, R.layout.list_gender, items)
                (editText as? AutoCompleteTextView)?.apply {
                    setAdapter(adapter)
                    setOnItemClickListener { _, _, i, _ ->
                        input.selected = i
                    }
                }
            }
            is IntegerInput -> with(view as TextInputLayout) {
                hint = input.name
                suffixText = input.suffix
                editText?.apply {
                    inputType = InputType.TYPE_CLASS_NUMBER
                    afterTextChanged {
                        input.input = it.toIntOrNull()
                    }
                }
            }
            is CheckBox -> with(view as ViewGroup) {
                checkbox.text = input.name
                checkbox.setOnCheckedChangeListener { _, isChecked ->
                    input.isChecked = isChecked
                    if (isChecked && input.thenAttributes != null) {
                        optionalInputLayout.visibility = View.VISIBLE
                    } else {
                        optionalInputLayout.visibility = View.GONE
                    }
                }
                if (input.thenAttributes != null) {
                    optionalInputLayout.data = input.thenAttributes
                }
            }
            is RadioGroup -> with(view as ViewGroup) {
                hint.text = input.name
                options.apply {
                    setOnCheckedChangeListener { _, i -> input.checked = i }
                    for ((j, option) in input.options.withIndex()) {
                        (inflate(context, R.layout.radio_option, null) as RadioButton).run {
                            id = j
                            text = option
                            addView(this)
                            layoutParams.width = LayoutParams.MATCH_PARENT
                        }
                    }
                }

                if (input.thenAttributes != null) {
                    optionalInputLayout.visibility = View.VISIBLE
                    optionalInputLayout.data = input.thenAttributes
                }
            }
            is CheckboxGroup -> with(view as ViewGroup) {
                hint.text = input.name
                options.apply {
                    for ((j, option) in input.options.withIndex()) {
                        (inflate(context, R.layout.checkbox, null)).run {
                            id = j
                            checkbox.text = option
                            checkbox.setOnCheckedChangeListener { _, isChecked ->
                                if (isChecked) {
                                    input.checked.add(j)
                                } else {
                                    input.checked.remove(j)
                                }
                            }
                            addView(this)
                        }
                    }
                }
            }
        }
    }

//    override fun hasDividerBeforeChild(childIndex: Int): Boolean {
//        val inputs = data ?: return false
//
//        if (super.hasDividerBeforeChild(childIndex)) {
//            return when (inputs[childIndex]) {
//                is TextInput -> when (inputs[childIndex - 1]) {
//                    is DateInput, is TextInput, is IntegerInput, is OptionsInput -> false
//                    else -> true
//                }
//                is DateInput -> when (inputs[childIndex - 1]) {
//                    is DateInput, is TextInput, is IntegerInput, is OptionsInput -> false
//                    else -> true
//                }
//                is IntegerInput -> when (inputs[childIndex - 1]) {
//                    is DateInput, is TextInput, is IntegerInput, is OptionsInput -> false
//                    else -> true
//                }
//                is OptionsInput -> when (inputs[childIndex - 1]) {
//                    is DateInput, is TextInput, is IntegerInput, is OptionsInput -> false
//                    else -> true
//                }
//                else -> true
//            }
//        }
//
//        return false
//    }

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
        padding(
            left = insets.systemWindowInsetLeft,
            top = insets.systemWindowInsetTop,
            right = insets.systemWindowInsetRight,
            bottom = insets.systemWindowInsetBottom
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
}