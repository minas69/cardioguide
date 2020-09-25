package com.example.medicalapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.medicalapp.R
import com.example.medicalapp.data.Status
import com.example.medicalapp.toast
import com.example.medicalapp.ui.form.FormActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels { LoginViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        viewModel.isDataValid.observe(this@LoginActivity, Observer { isDataValid ->
            login.isEnabled = isDataValid
        })

        viewModel.loginResult.observe(this@LoginActivity, Observer { result ->
            when (result?.status) {
                Status.LOADING -> {
                    loading.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    loading.visibility = View.GONE
                    updateUiWithUser()
                }
                Status.ERROR -> {
                    loading.visibility = View.GONE
                    showLoginFailed(result.error)
                }
            }
        })

        et_email.afterTextChanged {
            viewModel.loginDataChanged(
                et_email.text.toString(),
                et_password.text.toString()
            )
        }

        et_password.apply {
            afterTextChanged {
                viewModel.loginDataChanged(
                    et_email.text.toString(),
                    et_password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        viewModel.login(
                            et_email.text.toString(),
                            et_password.text.toString()
                        )
                }
                false
            }
        }

        login.setOnClickListener {
            viewModel.login(et_email.text.toString(), et_password.text.toString())
        }
    }

    private fun updateUiWithUser() {
        startActivity(Intent(this, FormActivity::class.java))
        finish()
    }

    private fun showLoginFailed(e: Error?) {
        e ?: return

        email.error = null
        password.error = null

        when (e) {
            Error.EMAIL -> email.error = getString(e.msg)
            Error.PASSWORD -> password.error = getString(e.msg)
            else -> toast(getString(e.msg))
        }
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}