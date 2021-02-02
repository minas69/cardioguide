package com.example.medicalapp.ui.form

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.medicalapp.*
import com.example.medicalapp.ui.report.ReportActivity
import kotlinx.android.synthetic.main.content_form.*
import kotlinx.android.synthetic.main.content_form.view.*
import java.io.File
import java.io.IOException
import java.util.*

class ContentFragment : Fragment() {

    private lateinit var viewModel: FormViewModel

    private var photoInputId: String? = null
    private var photoFileName: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_form, container, false).apply {
            applyInsets(this)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = FormViewModelFactory(requireActivity().application, this, savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity(), factory).get(FormViewModel::class.java)

        val selected = viewModel.selectedStep.value ?: 0
        val data = viewModel.data[selected]

        val adapter = ControlAdapter(requireActivity(), data.attributes) { id, value ->
            viewModel.onInputChanged(id, value)
        }
        adapter.onAddPhotoButtonClickListener = { id ->
            photoInputId = id
            dispatchTakePictureIntent(id + Date().time)
        }
        adapter.onRemovePhotoButtonClickListener = { id, photo ->
            viewModel.removePhoto(id, photo)
        }
        flowLayout.adapter = adapter

        viewModel.photos.observe(viewLifecycleOwner) { photos ->
            photos.forEach {
                adapter.addPhoto(it.key, it.value)
            }
        }

//        val isLast = viewModel.data.size - 1 == selected
//        if (isLast) {
//            nextBtn.visibility = View.GONE
//        }
//        nextBtn.setOnClickListener {
//            viewModel.nextStep()
//        }
    }

    private fun dispatchTakePictureIntent(filename: String) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile(filename, requireContext())
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }

                photoFile?.also {
                    photoFileName = it.absolutePath

                    val photoUri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.example.serviceland.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    startActivityForResult(takePictureIntent,
                        ControlAdapter.REQUEST_IMAGE_CAPTURE
                    )
                }

            }
        }
    }

    private fun createImageFile(filename: String, context: Context): File {
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            filename,
            ".jpg",
            storageDir
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val filename = photoFileName ?: return
        val id = photoInputId ?: return

        when(requestCode) {
            ControlAdapter.REQUEST_IMAGE_CAPTURE -> {
                if (resultCode == RESULT_OK) {
                    viewModel.addPhoto(id, filename)
                    photoFileName = null
                }
            }
        }
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