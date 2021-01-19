package com.example.medicalapp.ui.form

import android.os.Parcel
import android.os.Parcelable
import android.text.InputType
import android.util.Log
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.util.containsKey
import androidx.core.util.set
import androidx.recyclerview.widget.RecyclerView
import com.example.medicalapp.*
import com.example.medicalapp.ui.view.inputs.InputLayout
import com.example.views.DropdownField
import com.example.views.TextField
import kotlinx.android.parcel.Parcelize

class InputAdapter2(val data: List<Input>, val states: SparseArray<State>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val entered: SparseArray<Any> = SparseArray()

    companion object {
        const val TEXT_INPUT_VIEW_TYPE = 0
        const val DATE_INPUT_VIEW_TYPE = 1
        const val OPTIONS_INPUT_VIEW_TYPE = 2
        const val INTEGER_INPUT_VIEW_TYPE = 3
    }

//    override fun getItemViewType(position: Int) = when (data[position]) {
//        is TextInput -> TEXT_INPUT_VIEW_TYPE
//        is DateInput -> DATE_INPUT_VIEW_TYPE
//        is OptionsInput -> OPTIONS_INPUT_VIEW_TYPE
//        is IntegerInput -> INTEGER_INPUT_VIEW_TYPE
//        is CheckBoxInput -> 4
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val holder = when (viewType) {
            TEXT_INPUT_VIEW_TYPE -> TextInputViewHolder(TextField(parent.context))
            DATE_INPUT_VIEW_TYPE -> DateInputViewHolder(TextField(parent.context))
            OPTIONS_INPUT_VIEW_TYPE -> OptionsInputViewHolder(DropdownField(parent.context))
            else -> IntegerInputViewHolder(TextField(parent.context))
        }

        holder.itemView.run {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val horizontalPadding = context.resources.getDimension(R.dimen.activity_horizontal_margin).toInt()
            padding(left = horizontalPadding, right = horizontalPadding)
        }

        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        if (states.containsKey(position)) {
//            holder.itemView.restoreHierarchyState(states[position].container)
//            return
//        }

        when (holder) {
            is TextInputViewHolder -> holder.bind(data[position] as TextInput)
            is DateInputViewHolder -> holder.bind(data[position] as DateInput)
            is OptionsInputViewHolder -> holder.bind(data[position] as OptionsInput)
            is IntegerInputViewHolder -> holder.bind(data[position] as IntegerInput)
        }
    }

    override fun getItemCount() = data.size

//    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
//        val container = SparseArray<Parcelable>()
//        holder.itemView.saveHierarchyState(container)
//        val state = State()
//        state.container = container
//        states[holder.adapterPosition] = state
//    }

    inner class TextInputViewHolder(view: TextField) : RecyclerView.ViewHolder(view) {

        init {
            view.afterTextChanged { text ->
                Log.d("test", "$adapterPosition -> $text")
            }
        }

        fun bind(input: TextInput) {
            (itemView as TextField).run {
                clear()
                inputType = InputType.TYPE_CLASS_TEXT
                hint = input.name
            }
        }

    }

    inner class DateInputViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(input: DateInput) {
            (itemView as TextField).run {
                clear()
                inputType = InputType.TYPE_CLASS_DATETIME
                hint = input.name
            }
        }

    }

    inner class OptionsInputViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(input: OptionsInput) {
            (itemView as DropdownField).run {
                clearSelection()
                hint = input.name
                items = input.options
            }
        }

    }

    inner class IntegerInputViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(input: IntegerInput) {
            (itemView as TextField).run {
                clear()
                inputType = InputType.TYPE_CLASS_NUMBER
                hint = input.name
                suffix = input.suffix
            }
        }

    }

    class State() : Parcelable {

        var container: SparseArray<Parcelable>? = null

        constructor(parcel: Parcel) : this() {
            container = parcel.readSparseArray(javaClass.classLoader)
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeSparseArray(container)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<State> {
            override fun createFromParcel(parcel: Parcel): State {
                return State(parcel)
            }

            override fun newArray(size: Int): Array<State?> {
                return arrayOfNulls(size)
            }
        }
    }
}