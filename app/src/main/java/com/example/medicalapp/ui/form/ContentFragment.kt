package com.example.medicalapp.ui.form

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.medicalapp.*
import kotlinx.android.synthetic.main.content_form2.*
import kotlinx.android.synthetic.main.content_form2.view.*

class ContentFragment : Fragment() {

    private lateinit var viewModel: FormViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_form2, container, false).apply {
            applyInsets(this)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = FormViewModelFactory(requireActivity().application, this, savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity(), factory).get(FormViewModel::class.java)

        val selected = viewModel.selectedStep.value ?: 0
        val data = viewModel.data[selected]

        val fragmentManager = requireActivity().supportFragmentManager
        flowLayout.adapter = ControlAdapter(fragmentManager, data.attributes) { id, value ->
            viewModel.onInputChanged(id, value)
        }

//        val isLast = viewModel.data.size - 1 == selected
//        if (isLast) {
//            nextBtn.visibility = View.GONE
//        }
//        nextBtn.setOnClickListener {
//            viewModel.nextStep()
//        }
    }

    @Suppress("DEPRECATION")
    private fun applyInsets(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            view.flowLayout.setOnApplyWindowInsetsListener { _, insets ->
                view.padding(
                    bottom = insets.systemWindowInsetBottom + 8.dpToPx(),
                    left = insets.systemWindowInsetLeft,
                    right = insets.systemWindowInsetRight
                )
                insets.copy(bottom = 0)
            }
        }
    }

    companion object {

        private const val ID_ARG = "id_arg"
        private const val RECYCLER_VIEW_STATE_ARG = "recycler_view_state"

        fun newInstance(id: Int) = ContentFragment().apply {
            arguments = bundleOf(ID_ARG to id)
        }

    }

}