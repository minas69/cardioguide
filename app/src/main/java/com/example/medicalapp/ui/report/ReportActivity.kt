package com.example.medicalapp.ui.report

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import com.example.medicalapp.*
import com.example.medicalapp.data.Status
import com.example.medicalapp.data.model.ResultResponse
import com.example.medicalapp.ui.form.FormActivity
import com.example.medicalapp.ui.review.ReviewActivity
import kotlinx.android.synthetic.main.activity_report.*
import kotlinx.android.synthetic.main.content_error.*
import kotlinx.android.synthetic.main.content_report.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat

class ReportActivity : AppCompatActivity() {

    private val viewModel: ReportViewModel by viewModels {
        ReportViewModelFactory(intent.getSerializableExtra(DATA)!! as Map<String, Any>)
    }

    private var rateMenuItem: MenuItem? = null
    private lateinit var reviewIntent: Intent
    private var rated = false

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

        savedInstanceState?.let {
            rated = it.getBoolean(RATED_ARG)
        }

        content.visibility = View.GONE
        errorContainer.visibility = View.GONE

        viewModel.result.observe(this) { result ->
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
        }
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

    private fun showReport(report: ResultResponse?) {
        report?.let { data ->
            content.visibility = View.VISIBLE
            errorContainer.visibility = View.GONE
            loading.visibility = View.GONE

            rateMenuItem?.isVisible = !rated
            reviewIntent = ReviewActivity.getIntent(this, report.id)

            reportId.text = report.id
            createdBy.text = report.createdBy.email

            val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
            createdAt.text = formatter.format(report.createdAt)

            val coefficients: List<Coefficient>
                    = application.assets.open("result.json").bufferedReader().use {
                Json.decodeFromString(it.readText())
            }

            hdr.text = "%.2f".format(data.SCORE * 100) + " %"
            var option = when {
                data.SCORE < 0.01 -> {
                    coefficients[0].options[0]
                }
                data.SCORE < 0.04 -> {
                    coefficients[0].options[1]
                }
                data.SCORE < 0.09 -> {
                    coefficients[0].options[2]
                }
                else -> {
                    coefficients[0].options[3]
                }
            }
            hdrDescription.text = option.description
            hdrComments.text = option.comments

            gfr.text = "%.2f".format(data.CKD_EPI)
            option = when {
                data.CKD_EPI > 90 -> {
                    coefficients[1].options[0]
                }
                data.CKD_EPI > 60 -> {
                    coefficients[1].options[1]
                }
                data.CKD_EPI > 30 -> {
                    coefficients[1].options[2]
                }
                data.CKD_EPI > 15 -> {
                    coefficients[1].options[3]
                }
                else -> {
                    coefficients[1].options[4]
                }
            }
            gfrDescription.text = option.description
            gfrComments.text = option.comments

            bmi.text = "%.2f".format(data.BMI)
            option = when {
                data.BMI < 16 -> {
                    coefficients[2].options[0]
                }
                data.BMI < 18.4 -> {
                    coefficients[2].options[1]
                }
                data.BMI < 24.9 -> {
                    coefficients[2].options[2]
                }
                data.BMI < 29.9 -> {
                    coefficients[2].options[3]
                }
                data.BMI < 34.9 -> {
                    coefficients[2].options[4]
                }
                data.BMI < 39.9 -> {
                    coefficients[2].options[5]
                }
                else -> {
                    coefficients[2].options[6]
                }
            }
            bmiDescription.text = option.description
            bmiComments.text = option.comments
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_report, menu)
        rateMenuItem = menu.findItem(R.id.rateMenuItem).apply {
            isVisible = viewModel.result.value?.status == Status.SUCCESS && !rated
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.rateMenuItem -> {
                startActivityForResult(reviewIntent, ReviewActivity.REVIEW_REQUEST)
            }
            android.R.id.home -> {
                finish()
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            ReviewActivity.REVIEW_REQUEST -> {
                if (resultCode == Activity.RESULT_OK) {
                    rated = true
                    rateMenuItem?.isVisible = false
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean(RATED_ARG, rated)
    }

    companion object {

        private const val DATA = "data"
        private const val RATED_ARG = "rated"
        const val REPORT_REQUEST = 799

        fun getIntent(context: Context, data: HashMap<String, Any>) =
            Intent(context, ReportActivity::class.java).apply {
                putExtra(DATA, data)
            }
    }
}