package com.example.medicalapp.ui.form

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.core.view.children
import androidx.fragment.app.FragmentActivity
import com.example.medicalapp.*
import com.example.medicalapp.R
import com.example.views.*
import com.example.views.photoselector.PhotoSelector
import com.example.medicalapp.padding
import java.io.File
import java.io.IOException

class ControlAdapter(
    private val activity: FragmentActivity,
    val data: List<Input>,
    private val onInputChanged: (String, Any?) -> Unit
) : FlowLayout.Adapter {

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 228
    }

    private val ids = mutableListOf<String>()

    private var viewGroup: ViewGroup? = null

    var onAddPhotoButtonClickListener: ((String) -> Unit)? = null
    var onRemovePhotoButtonClickListener: ((String, Pair<String, String?>) -> Unit)? = null

    fun addPhoto(id: String, items: List<Pair<String, String?>>) {
        repeat(ids.count()) { i ->
            if (ids[i] == id) {
                val view = viewGroup?.getChildAt(i) as PhotoSelector
                view.adapter.updateItems(items)
                return
            }
        }
    }

    fun clearInputs() {
        viewGroup?.children?.forEach { child ->
            when (child) {
                is TextField -> {
                    child.clear()
                }
                is DatePicker -> {
                    child.clear()
                }
                is DropdownField -> {
                    child.clearSelection()
                }
                is NestingCheckBox -> {
                    child.isChecked = false
                }
                is RadioSection -> {
                    child.clearSelection()
                }
                is CheckBoxSection -> {
                    child.clearSelection()
                }
                is PhotoSelector -> {
                    child.clear()
                }
            }
        }
    }

    override fun onCreateView(parent: ViewGroup, position: Int): View {
        if (viewGroup == null) {
            viewGroup = parent
        }

        val item = data[position]
        val context = parent.context
        val horizontalMargin = context.resources.getDimension(R.dimen.activity_horizontal_margin).toInt()
        return when(item) {
            is TextInput -> item.createView(context, horizontalMargin, item.id, onInputChanged).apply { ids.add(item.id) }
            is DateInput -> item.createView(context, horizontalMargin, activity, item.id, onInputChanged).apply { ids.add(item.id) }
            is OptionsInput -> item.createView(context, horizontalMargin, item.id, onInputChanged).apply { ids.add(item.id) }
            is IntegerInput -> item.createView(context, horizontalMargin, item.id, onInputChanged).apply { ids.add(item.id) }
            is FloatInput -> item.createView(context, horizontalMargin, item.id, onInputChanged).apply { ids.add(item.id) }
            is CheckBoxInput -> item.createView(context, horizontalMargin, activity, item.id, onInputChanged).apply { ids.add(item.id) }
            is RadioGroupInput -> item.createView(context, horizontalMargin, activity, item.id, onInputChanged).apply { ids.add(item.id) }
            is CheckboxGroupInput -> item.createView(context, horizontalMargin, item.id, onInputChanged).apply { ids.add(item.id) }
            is PhotosInput -> item.createView(context, horizontalMargin, activity, item.id, onInputChanged).apply {
                val view = this as PhotoSelector
                view.setOnAddPhotoButtonClickListener { onAddPhotoButtonClickListener?.invoke(item.id) }
                view.setOnRemovePhotoButtonClickListener { onRemovePhotoButtonClickListener?.invoke(item.id, it) }
                ids.add(item.id)
            }
        }
    }

    override fun getChildCount() = data.count()
}

fun TextInput.createView(context: Context,
                         horizontalMargin: Int,
                         id: String,
                         onInputChanged: (String, String?) -> Unit
): View {
    val view = TextField(context)
    view.padding(left = horizontalMargin, right = horizontalMargin)
    view.hint = name
    view.inputType = InputType.TYPE_CLASS_TEXT
    view.isRequired = required
    view.helperText = helperText
    view.afterTextChanged { text ->
        val value = if (text.isNotEmpty()) text.toString() else null
        onInputChanged(id, value)
    }
    return view
}

fun DateInput.createView(context: Context,
                         horizontalMargin: Int,
                         activity: FragmentActivity,
                         id: String,
                         onInputChanged: (String, Long?) -> Unit
): View {
    val view = DatePicker(context)
    view.padding(left = horizontalMargin, right = horizontalMargin)
    view.hint = name
    view.isRequired = required
    view.inputType = InputType.TYPE_CLASS_DATETIME
    view.setOnDateChangedListener(activity.supportFragmentManager, min, max) {
        onInputChanged(id, it)
    }
    return view
}

fun OptionsInput.createView(context: Context,
                            horizontalMargin: Int,
                            id: String,
                            onInputChanged: (String, Int) -> Unit
): View {
    val view = DropdownField(context)
    view.padding(left = horizontalMargin, right = horizontalMargin)
    view.hint = name
    view.isRequired = required
    view.items = options
    view.setOnItemClickListener {
        onInputChanged(id, it)
    }
    return view
}

fun IntegerInput.createView(context: Context,
                            horizontalMargin: Int,
                            id: String,
                            onInputChanged: (String, Int?) -> Unit
): View {
    val view = TextField(context)
    view.padding(left = horizontalMargin, right = horizontalMargin)
    view.hint = name
    view.isRequired = required
    view.suffix = suffix
    view.helperText = helperText
    view.inputType = InputType.TYPE_CLASS_NUMBER
    view.afterTextChanged { text ->
        val str = text.toString()
        onInputChanged(id, if (str.isNotEmpty()) str.toInt() else null)
    }
    return view
}

fun FloatInput.createView(context: Context,
                          horizontalMargin: Int,
                          id: String,
                          onInputChanged: (String, Float?) -> Unit): View {
    val view = TextField(context)
    view.padding(left = horizontalMargin, right = horizontalMargin)
    view.hint = name
    view.isRequired = required
    view.suffix = suffix
    view.helperText = helperText
    view.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
    view.afterTextChanged { text ->
        val str = text.toString()
        onInputChanged(id, if (str.isNotEmpty()) str.toFloat() else null)
    }
    return view
}

fun CheckBoxInput.createView(context: Context,
                             horizontalMargin: Int,
                             activity: FragmentActivity,
                             id: String, onInputChanged: (String, Any?) -> Unit): View {
    val view = NestingCheckBox(context)
    view.padding(left = horizontalMargin, right = horizontalMargin)
    view.label = name
    view.setOnCheckedChangedListener {
        onInputChanged(id, it)
    }
    thenAttributes?.let { attributes ->
        view.flowLayout.adapter = ControlAdapter(activity, attributes) { id, value ->
            onInputChanged(id, value)
        }
    }
    return view
}

fun RadioGroupInput.createView(context: Context,
                               horizontalMargin: Int,
                               activity: FragmentActivity,
                               id: String, onInputChanged: (String, Any?) -> Unit): View {
    val view = RadioSection(context)
    view.padding(left = horizontalMargin, right = horizontalMargin)
    view.label = name
    view.isRequired = required
    view.items = options
    view.setOnCheckedChangedListener {
        onInputChanged(id, it)
    }
    thenAttributes?.let { attributes ->
        view.flowLayout.adapter = ControlAdapter(activity, attributes) { id, value ->
            onInputChanged(id, value)
        }
    }
    return view
}

fun CheckboxGroupInput.createView(context: Context,
                                  horizontalMargin: Int,
                                  id: String,
                                  onInputChanged: (String, Any?) -> Unit): View {
    val view = CheckBoxSection(context)
    view.padding(left = horizontalMargin, right = horizontalMargin)
    view.label = name
    view.items = options
    view.setOnCheckedChangeListener {
        onInputChanged(id, it)
    }
    return view
}

fun PhotosInput.createView(context: Context,
                           horizontalMargin: Int,
                           activity: FragmentActivity,
                           id: String,
                           onInputChanged: (String, Any?) -> Unit): View {
    val view = PhotoSelector(context)
    view.label = name
    view.isRequired = required
    view.setMaxPhotos(max)
    view.padding(left = horizontalMargin, right = horizontalMargin)
    return view
}

