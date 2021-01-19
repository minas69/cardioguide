package com.example.medicalapp.ui.form

import android.content.Context
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.example.medicalapp.*
import com.example.views.*
import kotlinx.android.synthetic.main.content_form2.*

class ControlAdapter(
    val fragmentManager: FragmentManager,
    val data: List<Input>,
    val onInputChanged: (String, Any?) -> Unit
) : FlowLayout.Adapter {

    override fun onCreateView(parent: ViewGroup, position: Int): View {
        val item = data[position]
        val context = parent.context
        return when(item) {
            is TextInput -> item.createView(context, item.id, onInputChanged)
            is DateInput -> item.createView(context, fragmentManager, item.id, onInputChanged)
            is OptionsInput -> item.createView(context, item.id, onInputChanged)
            is IntegerInput -> item.createView(context, item.id, onInputChanged)
            is FloatInput -> item.createView(context, item.id, onInputChanged)
            is CheckBoxInput -> item.createView(context, fragmentManager, item.id, onInputChanged)
            is RadioGroupInput -> item.createView(context, fragmentManager, item.id, onInputChanged)
            is CheckboxGroupInput -> item.createView(context, item.id, onInputChanged)
        }
    }

    override fun getChildCount() = data.count()
}

fun TextInput.createView(context: Context,
                         id: String,
                         onInputChanged: (String, String?) -> Unit
): View {
    val view = TextField(context)
    view.hint = name
    view.inputType = InputType.TYPE_CLASS_TEXT
    view.afterTextChanged { text ->
        val value = if (text.isNotEmpty()) text.toString() else null
        onInputChanged(id, value)
    }
    return view
}

fun DateInput.createView(context: Context,
                         fragmentManager: FragmentManager,
                         id: String,
                         onInputChanged: (String, Long?) -> Unit
): View {
    val view = DatePicker(context)
    view.hint = name
    view.inputType = InputType.TYPE_CLASS_DATETIME
    view.setOnDateChangedListener(fragmentManager) {
        onInputChanged(id, it)
    }
    return view
}

fun OptionsInput.createView(context: Context,
                            id: String,
                            onInputChanged: (String, Int) -> Unit
): View {
    val view = DropdownField(context)
    view.hint = name
    view.items = options
    view.setOnItemClickListener {
        onInputChanged(id, it)
    }
    return view
}

fun IntegerInput.createView(context: Context,
                            id: String,
                            onInputChanged: (String, Int?) -> Unit
): View {
    val view = TextField(context)
    view.hint = name
    view.suffix = suffix
    view.inputType = InputType.TYPE_CLASS_NUMBER
    view.afterTextChanged { text ->
        val str = text.toString()
        onInputChanged(id, if (str.isNotEmpty()) str.toInt() else null)
    }
    return view
}

fun FloatInput.createView(context: Context, id: String, onInputChanged: (String, Float?) -> Unit): View {
    val view = TextField(context)
    view.hint = name
    view.suffix = suffix
    view.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
    view.afterTextChanged { text ->
        val str = text.toString()
        onInputChanged(id, if (str.isNotEmpty()) str.toFloat() else null)
    }
    return view
}

fun CheckBoxInput.createView(context: Context, fragmentManager: FragmentManager, id: String, onInputChanged: (String, Any?) -> Unit): View {
    val view = NestingCheckBox(context)
    view.label = name
    view.setOnCheckedChangedListener {
        onInputChanged(id, it)
    }
    thenAttributes?.let { attributes ->
        view.flowLayout.adapter = ControlAdapter(fragmentManager, attributes) { id, value ->
            onInputChanged(id, value)
        }
    }
    return view
}

fun RadioGroupInput.createView(context: Context, fragmentManager: FragmentManager, id: String, onInputChanged: (String, Any?) -> Unit): View {
    val view = RadioSection(context)
    view.label = name
    view.items = options
    view.setOnCheckedChangedListener {
        onInputChanged(id, it)
    }
    thenAttributes?.let { attributes ->
        view.flowLayout.adapter = ControlAdapter(fragmentManager, attributes) { id, value ->
            onInputChanged(id, value)
        }
    }
    return view
}

fun CheckboxGroupInput.createView(context: Context, id: String, onInputChanged: (String, Any?) -> Unit): View {
    val view = CheckBoxSection(context)
    view.label = name
    view.items = options
    view.setOnCheckedChangeListener {
        onInputChanged(id, it)
    }
    return view
}
