package com.example.medicalapp.ui.form

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import com.example.medicalapp.R
import com.example.medicalapp.dpToPx
import com.example.medicalapp.padding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet.*

class BottomSheet : BottomSheetDialogFragment() {

    lateinit var itemClickListener : (view: View) -> Unit

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return (inflater.inflate(R.layout.bottom_sheet, container) as ViewGroup).apply {
            children.forEach { view ->
                view.setOnClickListener {
                    itemClickListener(it)
                    dialog?.dismiss()
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dialog?.window?.apply {
                findViewById<View>(com.google.android.material.R.id.container).setOnApplyWindowInsetsListener { _, insets ->
                    content.padding(bottom = insets.systemWindowInsetBottom + 8.dpToPx())

                    insets.consumeSystemWindowInsets()
                }
                val decorView = decorView
                decorView.systemUiVisibility = decorView.systemUiVisibility or
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
        }
    }
}
