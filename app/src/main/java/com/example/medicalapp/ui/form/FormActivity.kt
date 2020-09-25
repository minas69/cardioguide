package com.example.medicalapp.ui.form

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.medicalapp.*
import com.example.medicalapp.data.model.Data
import com.example.medicalapp.ui.login.LoginActivity
import com.example.medicalapp.ui.report.ReportActivity
import kotlinx.android.synthetic.main.activity_form.*
import kotlinx.android.synthetic.main.content_form.*

class FormActivity : AppCompatActivity() {

    private val viewModel: FormViewModel by viewModels { FormViewModelFactory() }

    private val textFields: Array<EditText> by lazy {
        arrayOf(et_surname, et_age, et_weight, tv_gender, et_pressure, et_cholesterol, et_ldl)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentBehindNavigationBar()

        setContentView(R.layout.activity_form)
        setSupportActionBar(toolbar)
        applyInsets()

        send.hide()
        val items = listOf("Мужской", "Женский")
        val adapter = ArrayAdapter(this, R.layout.list_gender, items)
        (gender.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        viewModel.isDataValid.observe(this@FormActivity) { isDataValid ->
//            send.isEnabled = isDataValid
            if (isDataValid) {
                send.show()
            } else {
                send.hide()
            }
        }

        send.setOnClickListener {
            val data = Data(
                et_surname.text.toString(),
                et_age.text.toString().toInt(),
                et_weight.text.toString().toInt(),
                tv_gender.text.toString(),
                et_pressure.text.toString().toInt(),
                et_cholesterol.text.toString().toInt(),
                et_ldl.text.toString().toInt(),
                smoking.isChecked,
                low_risk_country.isChecked
            )

            startActivityForResult(
                ReportActivity.getIntent(this, data),
                ReportActivity.REPORT_REQUEST
            )
        }

        val textChangedListener = object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                viewModel.dataChanged(
                    et_surname.text.toString(),
                    et_age.text.toString(),
                    et_weight.text.toString(),
                    tv_gender.text.toString(),
                    et_pressure.text.toString(),
                    et_cholesterol.text.toString(),
                    et_ldl.text.toString()
                )
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        }
        for (textField in textFields) {
            textField.addTextChangedListener(textChangedListener)
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
            send.doOnApplyWindowInsets { view, insets, _ ->
                val initialMargin = resources.getDimension(R.dimen.fab_margin).toInt()
                view.margin(bottom = initialMargin + insets.systemWindowInsetBottom)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            ReportActivity.REPORT_REQUEST -> {
                if (resultCode == Activity.RESULT_OK) {
                    clearInput()
                }
            }
        }
    }

    private fun clearInput() {
        et_surname.text?.clear()
        et_age.text?.clear()
        et_weight.text?.clear()
        tv_gender.text?.clear()
        et_pressure.text?.clear()
        et_cholesterol.text?.clear()
        et_ldl.text?.clear()
        smoking.isChecked = false
        low_risk_country.isChecked = false

        surname.clearFocus()
        age.clearFocus()
        weight.clearFocus()
        gender.clearFocus()
        pressure.clearFocus()
        cholesterol.clearFocus()
        ldl.clearFocus()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_form, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.exit -> {
                logout()
            }
        }
        return true
    }

    private fun logout() {
        viewModel.logout()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}