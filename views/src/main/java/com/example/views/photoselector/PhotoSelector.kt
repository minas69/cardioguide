package com.example.views.photoselector

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.views.R
import com.example.views.padding

class PhotoSelector @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val labelTextView: TextView
    private val recyclerView: RecyclerView
    val adapter: PhotosAdapter

    var label: CharSequence?
        set(value) {
            labelTextView.text = value
        }
        get() = labelTextView.text

    var isRequired: Boolean = false
        set(value) {
            field = value
            val labelText = label
            if (value && labelText != null) {
                val spannable = SpannableString("$labelText*")
                spannable.setSpan(ForegroundColorSpan(Color.RED), labelText.length, labelText.length + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                labelTextView.setText(spannable, TextView.BufferType.SPANNABLE)
            }
        }

    init {
        orientation = VERTICAL
        inflate(context, R.layout.layout_photo_selector, this)

        labelTextView = getChildAt(0) as TextView
        recyclerView = getChildAt(1) as RecyclerView
        adapter = PhotosAdapter()
        recyclerView.adapter = adapter
    }

    fun clear() {
        adapter.updateItems(emptyList())
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        labelTextView.padding(left = left, right = right)
        recyclerView.padding(left = left, right = right)
    }

    fun setMaxPhotos(value: Int) {
        adapter.maxPhotos = value
    }

    fun setOnAddPhotoButtonClickListener(listener: () -> Unit) {
        adapter.onAddButtonClickListener = listener
    }

    fun setOnRemovePhotoButtonClickListener(listener: (Pair<String, String?>) -> Unit) {
        adapter.onRemoveButtonClickListener = listener
    }

}
