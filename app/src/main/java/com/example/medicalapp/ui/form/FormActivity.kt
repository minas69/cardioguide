package com.example.medicalapp.ui.form

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.medicalapp.*
import com.example.medicalapp.ui.login.LoginActivity
import com.example.medicalapp.ui.report.ReportActivity
import com.example.medicalapp.ui.view.backdrop.BackdropBehavior
import com.example.medicalapp.ui.view.backdrop.StepsAdapter
import kotlinx.android.synthetic.main.activity_form.*
import kotlinx.android.synthetic.main.activity_form.frontLayout
import kotlinx.android.synthetic.main.back_layer.*


class FormActivity : AppCompatActivity() {

    companion object {
        private const val FRAGMENT_CONTAINER = R.id.content
        private const val FRAGMENT_TAG_PREFIX = "content_"
        private const val BOTTOM_SHEET_TAG = "bottom_sheet"
        private const val SELECTED_STEP_ARG = "selected_step"
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
            val iinputs = (it.getSerializable("fuck") as HashMap<String, Any>?) ?: return@let
            viewModel.setInputs(iinputs)
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
            stepsAdapter.selected = index
            val total = viewModel.data.size
            supportActionBar?.title = "Шаг ${index + 1} из $total"
            frontLayout.subheader.text = viewModel.data[index].name
            showPage(index)
        }

        viewModel.errorMessage.observe(this) {
            toast(it)
        }

        viewModel.result.observe(this) {
            startActivity(ReportActivity.getIntent(this, it))
        }

        rollUp.setOnClickListener {
            backdropBehavior.close()
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putSerializable("fuck", viewModel._inputs)
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

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        when (requestCode) {
//            ReportActivity.REPORT_REQUEST -> {
//                if (resultCode == Activity.RESULT_OK) {
////                    clearInput()
//                }
//            }
//        }
//    }

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
                    viewModel.complete()
                }
                R.id.exit -> {
                    toast("Exited")
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