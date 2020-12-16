package com.example.medicalapp.ui.form

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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

        private val stepsList = listOf(
            "Общие данные",
            "Жалобы за последний месяц",
            "Опросник по здоровью EQ-5D-3L",
            "Анамнез",
            "Наличие болезней",
            "Жалобы за последний месяц",
            "Принимаемые препараты",
            "Общий анализ крови",
            "Биохимический анализ крови",
            "ЭКГ",
            "УЗИ сердца (ЭХОКГ)",
            "Холтеровское мониторирование (ХМЭКГ)"
        )
    }

    private val viewModel: FormViewModel by viewModels { FormViewModelFactory(application) }

    private lateinit var backdropBehavior: BackdropBehavior

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setDecorBehindSystemWindows()

        setContentView(R.layout.activity_form)
        setSupportActionBar(toolbar)
        applyInsets()

        val adapter = StepsAdapter(this, stepsList) { index ->
            backdropBehavior.close()
            viewModel.selectStep(index)
        }
        with(steps) {
            this.adapter = adapter
            addItemDecoration(adapter.JoinItemDecoration())
        }

        frontLayout.setSubheaderTitle(FormActivity.stepsList[0])

        backdropBehavior = frontLayout.findBehavior()
        backdropBehavior.attachBackLayout(R.id.backLayout)

        viewModel.selectedStep.observe(this) { index ->
            val total = stepsList.size
            supportActionBar?.title = "Шаг ${index + 1} из $total"
            frontLayout.setSubheaderTitle(stepsList[index])
            showPage(index)
        }

        rollUp.setOnClickListener {
            backdropBehavior.close()
        }
    }

    private fun applyInsets() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            backdrop.setOnApplyWindowInsetsListener { view, insets ->
                view.padding(top = insets.systemWindowInsetTop)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_form, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.exit -> {
//                logout()
            }
        }
        return true
    }

    private fun logout() {
        viewModel.logout()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun showPage(index: Int) {
        val transaction = supportFragmentManager.beginTransaction()

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