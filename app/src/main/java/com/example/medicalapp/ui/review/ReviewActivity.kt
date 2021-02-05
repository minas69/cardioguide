package com.example.medicalapp.ui.review

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.medicalapp.*
import kotlinx.android.synthetic.main.activity_report.coordinator
import kotlinx.android.synthetic.main.activity_report.toolbar
import kotlinx.android.synthetic.main.content_review.*

class ReviewActivity : AppCompatActivity() {

    private val viewModel: ReviewViewModel by viewModels {
        ReviewViewModelFactory(intent.getStringExtra(RECORD_ID)!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setDecorBehindSystemWindows()

        setContentView(R.layout.activity_review)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        applyInsets()

        val onCheckedChangedListener = { _: Int ->
            viewModel.onInputChanged(aware.checked == 0, helpful.checked == 0, rate.checked)
        }

        aware.label = "Знали ли вы о рекомендациях, которые вам дало наше приложение?"
        aware.items = listOf("Да", "Нет")
        aware.flowLayout.visibility = View.GONE
        aware.setOnCheckedChangedListener(onCheckedChangedListener)

        helpful.label = "Были ли для вас полезны данные рекомендации?"
        helpful.items = listOf("Да", "Нет")
        helpful.flowLayout.visibility = View.GONE
        helpful.setOnCheckedChangedListener(onCheckedChangedListener)

        rate.label = "Пожалуйста, оцените приложение от 1 до 5"
        rate.items = listOf("1", "2", "3", "4", "5")
        rate.flowLayout.visibility = View.GONE
        rate.setOnCheckedChangedListener(onCheckedChangedListener)

        viewModel.isDataValid.observe(this) { isValid ->
            send.isEnabled = isValid
        }
        viewModel.error.observe(this) {
            toast(it)
        }
        viewModel.success.observe(this) { success ->
            if (success) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }

        send.setOnClickListener {
            viewModel.sendReview()
        }

    }

    private fun applyInsets() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT_WATCH) {
            coordinator.doOnApplyWindowInsets { view, insets, _ ->
                view.padding(top = insets.systemWindowInsetTop)
            }
            content.doOnApplyWindowInsets { view, insets, initialPadding ->
                view.padding(
                    left = initialPadding.left + insets.systemWindowInsetLeft,
                    right = initialPadding.right + insets.systemWindowInsetRight,
                    bottom = initialPadding.bottom + insets.systemWindowInsetBottom
                )
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {

        private const val RECORD_ID = "record_id"
        const val REVIEW_REQUEST = 699

        fun getIntent(context: Context, recordId: String) =
            Intent(context, ReviewActivity::class.java).apply {
                putExtra(RECORD_ID, recordId)
            }
    }
}