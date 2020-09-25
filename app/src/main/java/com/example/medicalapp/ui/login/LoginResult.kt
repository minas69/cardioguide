package com.example.medicalapp.ui.login

import androidx.annotation.StringRes
import com.example.medicalapp.R
import com.example.medicalapp.data.Status
import com.example.medicalapp.data.Status.SUCCESS
import com.example.medicalapp.data.Status.ERROR
import com.example.medicalapp.data.Status.LOADING

data class LoginResult(val status: Status, val error: Error?) {
    companion object {
        fun success() = LoginResult(SUCCESS, null)

        fun error(e: Error) = LoginResult(ERROR, e)

        fun loading() = LoginResult(LOADING, null)
    }
}

enum class Error(@StringRes val msg: Int) {
    EMAIL(R.string.invalid_email),
    PASSWORD(R.string.invalid_password),
    TOO_MANY_ATTEMPTS(R.string.too_many_requests),
    OTHER(R.string.login_failed)
}
