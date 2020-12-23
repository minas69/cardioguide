package com.example.medicalapp.ui.form

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.medicalapp.*
import kotlinx.android.synthetic.main.activity_form.*
import kotlinx.android.synthetic.main.content_form.*

class ContentFragment : Fragment() {

    private val viewModel: FormViewModel by viewModels(
        ownerProducer = { requireActivity() },
        factoryProducer = { FormViewModelFactory(requireActivity().application) }
    )

    override fun onResume() {
        super.onResume()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_form, container, false).apply {
            applyInsets(this)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selected = viewModel.selectedStep.value ?: 0
        val data = viewModel.data[selected]
        inputs.data = data.attributes

        val isLast = viewModel.data.size - 1 == selected
        if (isLast) {
            nextBtn.visibility = View.GONE
        }
        nextBtn.setOnClickListener {
            viewModel.nextStep()
        }
    }

    @Suppress("DEPRECATION")
    private fun applyInsets(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            view.setOnApplyWindowInsetsListener { _, insets ->
                view.padding(
                    bottom = insets.systemWindowInsetBottom + 8.dpToPx(),
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
            arguments = bundleOf(ID_ARG to id)
        }

    }

}