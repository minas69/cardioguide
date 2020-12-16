package com.example.medicalapp.ui.report

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.medicalapp.R
import com.example.medicalapp.data.Status
import com.example.medicalapp.data.model.Data
import com.example.medicalapp.data.model.Report
import com.example.medicalapp.doOnApplyWindowInsets
import com.example.medicalapp.padding
import com.example.medicalapp.setDecorBehindSystemWindows
import kotlinx.android.synthetic.main.activity_report.*
import kotlinx.android.synthetic.main.content_error.*
import kotlinx.android.synthetic.main.content_report.*

class ReportActivity : AppCompatActivity() {

    private val viewModel: ReportViewModel by viewModels {
        ReportViewModelFactory(intent.getParcelableExtra(DATA)!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setDecorBehindSystemWindows()

        setContentView(R.layout.activity_report)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        applyInsets()

        content.visibility = View.GONE
        errorContainer.visibility = View.GONE

        viewModel.result.observe(this, Observer { result ->
            when (result.status) {
                Status.LOADING -> {
                    content.visibility = View.GONE
                    errorContainer.visibility = View.GONE
                    loading.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    setResult(Activity.RESULT_OK)
                    showReport(result.data)
                }
                Status.ERROR -> {
                    showError(result.message)
                }
            }
        })
    }

    private fun applyInsets() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT_WATCH) {
            coordinator.doOnApplyWindowInsets { view, insets, _ ->
                view.padding(top = insets.systemWindowInsetTop)
            }
            content.doOnApplyWindowInsets { view, insets, initialPadding ->
                view.padding(bottom = initialPadding.bottom + insets.systemWindowInsetBottom)
            }
        }
    }

    private fun showError(message: String?) {
        message?.let {
            content.visibility = View.GONE
            errorContainer.visibility = View.VISIBLE
            loading.visibility = View.GONE

            error.text = message
        }
    }

    private fun showReport(report: Report?) {
        report?.let {
            content.visibility = View.VISIBLE
            errorContainer.visibility = View.GONE
            loading.visibility = View.GONE

            risk.text = report.risk
            wideRisk.text = report.wideRisk
            optimalRisk.text = report.optimalRisk
            statin.text = report.statin
            recommendation.text = report.recommendation
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {

        private const val DATA = "data"
        const val REPORT_REQUEST = 799

        fun getIntent(context: Context, data: Data) =
            Intent(context, ReportActivity::class.java).apply {
                putExtra(DATA, data)
            }
    }
}