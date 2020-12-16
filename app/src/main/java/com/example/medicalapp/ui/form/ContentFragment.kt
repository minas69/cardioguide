package com.example.medicalapp.ui.form

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.medicalapp.Block
import com.example.medicalapp.R
import com.example.medicalapp.copy
import com.example.medicalapp.padding
import kotlinx.android.synthetic.main.activity_form.*
import kotlinx.android.synthetic.main.content_form.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.lang.Exception

class ContentFragment : Fragment() {

    private val viewModel: FormViewModel by viewModels { FormViewModelFactory(requireActivity().application) }

    override fun onStart() {
        super.onStart()

//        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_form, container, false).apply {
            applyInsets(this)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val id = it.getInt(ID_ARG)
            blocks.data = viewModel.data
        }
    }

    private fun applyInsets(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            view.setOnApplyWindowInsetsListener { _, insets ->
                view.padding(
                    bottom = insets.systemWindowInsetBottom,
                    left = insets.systemWindowInsetLeft,
                    right = insets.systemWindowInsetRight
                )
                view.invalidate()
                insets.copy(bottom = 0)
            }
        }
    }

    companion object {

        private const val ID_ARG = "id_arg"

        fun newInstance(id: Int) = ContentFragment().apply {
            arguments = Bundle().apply {
                putInt(ID_ARG, id)
            }
        }

    }

}