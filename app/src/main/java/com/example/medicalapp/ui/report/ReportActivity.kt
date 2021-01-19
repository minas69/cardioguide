package com.example.medicalapp.ui.report

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.medicalapp.*
import com.example.medicalapp.data.Status
import com.example.medicalapp.data.model.Data
import com.example.medicalapp.data.model.Report
import com.example.medicalapp.data.model.ResultResponse
import kotlinx.android.synthetic.main.activity_report.*
import kotlinx.android.synthetic.main.content_error.*
import kotlinx.android.synthetic.main.content_report.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ReportActivity : AppCompatActivity() {

//    private val viewModel: ReportViewModel by viewModels {
//        ReportViewModelFactory(intent.getParcelableExtra(DATA)!!)
//    }

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

        content.visibility = View.VISIBLE
//        errorContainer.visibility = View.GONE

        val data = intent.getParcelableExtra<ResultResponse>(DATA)!!

        val coefficients: List<Coefficient>
            = application.assets.open("result.json").bufferedReader().use {
            Json.decodeFromString(it.readText())
        }

        hdr.text = (data.hdr * 100).toString() + " %"
        var option = when {
            data.hdr < 0.01 -> {
                coefficients[0].options[0]
            }
            data.hdr < 0.04 -> {
                coefficients[0].options[1]
            }
            data.hdr < 0.09 -> {
                coefficients[0].options[2]
            }
            else -> {
                coefficients[0].options[3]
            }
        }
        hdrDescription.text = option.description
        hdrComments.text = option.comments

        gfr.text = data.gfr.toString()
        option = when {
            data.gfr > 90 -> {
                coefficients[1].options[0]
            }
            data.gfr > 60 -> {
                coefficients[1].options[1]
            }
            data.gfr > 30 -> {
                coefficients[1].options[2]
            }
            data.gfr > 15 -> {
                coefficients[1].options[3]
            }
            else -> {
                coefficients[1].options[4]
            }
        }
        gfrDescription.text = option.description
        gfrComments.text = option.comments

        bmi.text = data.bmi.toString()
        option = when {
            data.bmi < 16 -> {
                coefficients[2].options[0]
            }
            data.bmi < 18.4 -> {
                coefficients[2].options[1]
            }
            data.bmi < 24.9 -> {
                coefficients[2].options[2]
            }
            data.bmi < 29.9 -> {
                coefficients[2].options[3]
            }
            data.bmi < 34.9 -> {
                coefficients[2].options[4]
            }
            data.bmi < 39.9 -> {
                coefficients[2].options[5]
            }
            else -> {
                coefficients[2].options[6]
            }
        }
        bmiDescription.text = option.description
        bmiComments.text = option.comments

//        viewModel.result.observe(this, Observer { result ->
//            when (result.status) {
//                Status.LOADING -> {
//                    content.visibility = View.GONE
//                    errorContainer.visibility = View.GONE
//                    loading.visibility = View.VISIBLE
//                }
//                Status.SUCCESS -> {
//                    setResult(Activity.RESULT_OK)
////                    showReport(result.data)
//                }
//                Status.ERROR -> {
//                    showError(result.message)
//                }
//            }
//        })
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

//    private fun showReport(report: Report?) {
//        report?.let {
//            content.visibility = View.VISIBLE
//            errorContainer.visibility = View.GONE
//            loading.visibility = View.GONE
//
//            risk.text = report.risk
//            wideRisk.text = report.wideRisk
//            optimalRisk.text = report.optimalRisk
//            statin.text = report.statin
//            recommendation.text = report.recommendation
//        }
//    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {

        private const val DATA = "data"
        const val REPORT_REQUEST = 799

        fun getIntent(context: Context, data: ResultResponse) =
            Intent(context, ReportActivity::class.java).apply {
                putExtra(DATA, data)
            }
    }
}