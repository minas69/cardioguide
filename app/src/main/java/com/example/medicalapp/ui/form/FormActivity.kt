package com.example.medicalapp.ui.form

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.medicalapp.*
import com.example.medicalapp.ui.login.LoginActivity
import com.example.medicalapp.ui.report.ReportActivity
import com.example.medicalapp.ui.form.backdrop.BackdropBehavior
import com.example.medicalapp.ui.form.backdrop.StepsAdapter
import com.example.medicalapp.ui.review.ReviewActivity
import kotlinx.android.synthetic.main.activity_form.*
import kotlinx.android.synthetic.main.activity_form.frontLayout
import kotlinx.android.synthetic.main.back_layer.*
import kotlinx.android.synthetic.main.step_item.view.*


class FormActivity : AppCompatActivity() {

    companion object {
        private const val FRAGMENT_CONTAINER = R.id.content
        private const val FRAGMENT_TAG_PREFIX = "content_"
        private const val BOTTOM_SHEET_TAG = "bottom_sheet"
        private const val SELECTED_STEP_ARG = "selected_step"

        private const val INPUTS_ARG = "inputs"
        private const val PHOTOS_ARG = "photos"
    }

    private lateinit var viewModel: FormViewModel

    private lateinit var backdropBehavior: BackdropBehavior
    private lateinit var stepsAdapter: StepsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setDecorBehindSystemWindows()

        setContentView(R.layout.activity_form)
        setSupportActionBar(toolbar)
        applyInsets()

        val factory = FormViewModelFactory(application, this, savedInstanceState)
        viewModel = ViewModelProviders.of(this, factory).get(FormViewModel::class.java)

//        var selected = 0
//        savedInstanceState?.let {
//            selected = it.getInt(SELECTED_STEP_ARG)
//            viewModel.selectStep(selected)
//        }

        savedInstanceState?.let {
            viewModel.inputs = (it.getSerializable(INPUTS_ARG) as HashMap<String, Any>?) ?: return@let
            val photos = (it.getSerializable(PHOTOS_ARG) as HashMap<String, List<Pair<String, String?>>>?) ?: return@let
            viewModel.setPhotos(photos)
        }

        val selected = viewModel.selectedStep.value ?: 0
        stepsAdapter = StepsAdapter(this, viewModel.data, selected) { index ->
            backdropBehavior.close()
            viewModel.selectStep(index)
        }
        with(steps) {
            adapter = stepsAdapter
            addItemDecoration(stepsAdapter.JoinItemDecoration())
        }

        backdropBehavior = frontLayout.findBehavior()
        backdropBehavior.attachBackLayout(R.id.backLayout)

        viewModel.selectedStep.observe(this) { index ->
            if (index == -1) {
                return@observe resetFragmentManager()
            }
            stepsAdapter.selected = index
            val total = viewModel.data.size
            supportActionBar?.title = "Шаг ${index + 1} из $total"

            val step = viewModel.data[index];
            if (step.containsRequired) {
                val text = step.name
                val spannable = SpannableString("$text*")
                spannable.setSpan(ForegroundColorSpan(Color.RED), text.length, text.length + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                frontLayout.subheader.setText(spannable, TextView.BufferType.SPANNABLE)
            } else {
                frontLayout.subheader.text = step.name
            }

            showPage(index)
        }

        viewModel.errorMessage.observe(this) {
            toast(it)
        }

//        viewModel.result.observe(this) {
//            startActivity(ReportActivity.getIntent(this, it))
//        }

        rollUp.setOnClickListener {
            backdropBehavior.close()
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putSerializable(INPUTS_ARG, viewModel.inputs)
        outState.putSerializable(PHOTOS_ARG, viewModel.photos.value)
        outState.putInt(SELECTED_STEP_ARG, viewModel.selectedStep.value ?: 0)
    }

    @Suppress("DEPRECATION")
    private fun applyInsets() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            backdrop.setOnApplyWindowInsetsListener { _, insets ->
//                view.padding(top = insets.systemWindowInsetTop)
                val top = insets.systemWindowInsetTop + 8.dpToPx()
                steps.padding(top = top)
                rollUp.margin(top = top)
                backdropBehavior.setTopInset(insets.systemWindowInsetTop)
                backdropBehavior.setBottomInset(insets.systemWindowInsetBottom)
                insets.copy(top = 0)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            ReportActivity.REPORT_REQUEST -> {
                if (resultCode == Activity.RESULT_OK) {
//                    clearInput()
                }
            }
        }
    }

    private fun clearInput() {
        viewModel.clear()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_form, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.more -> {
                showBottomSheet()
            }
        }
        return true
    }

    private fun showBottomSheet() {
        val sheet = BottomSheet()
        sheet.itemClickListener = { view ->
            when (view.id) {
                R.id.complete -> {
                    if (viewModel.canComplete()) {
                        startActivityForResult(
                            ReportActivity.getIntent(this, viewModel.inputs),
                            ReportActivity.REPORT_REQUEST
                        )
                    }
                }
                R.id.clear -> {
                    clearInput()
                }
                R.id.exit -> {
                    logout()
                }
            }
        }
        sheet.show(supportFragmentManager, BOTTOM_SHEET_TAG)
    }

    private fun logout() {
        viewModel.logout()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun resetFragmentManager() {
        val transaction = supportFragmentManager.beginTransaction()

        repeat(viewModel.data.count()) { i ->
            val fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG_PREFIX + i)
            if (fragment != null) {
                transaction.remove(fragment)
            }
        }
        transaction.commitNow()

        viewModel.selectStep(0)
    }

    private fun showPage(index: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.animator.fade_in, R.animator.fade_out)

        val currentFragment = supportFragmentManager.primaryNavigationFragment
        var fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG_PREFIX + index)
        if (currentFragment != null && (fragment == null || currentFragment != fragment)) {
            transaction.detach(currentFragment)
        }
        if (fragment == null) {
            fragment = ContentFragment.newInstance(viewModel.selectedStep.value ?: 0)
            transaction.add(FRAGMENT_CONTAINER, fragment, FRAGMENT_TAG_PREFIX + index)
        } else {
            transaction.attach(fragment)
        }

        transaction.setPrimaryNavigationFragment(fragment)
        transaction.commitNow()
    }
}