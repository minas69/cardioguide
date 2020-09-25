package com.example.medicalapp.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import androidx.annotation.StringRes
import com.example.medicalapp.data.LoginRepository

import com.example.medicalapp.R
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.*
import java.lang.Exception

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val job = Job()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + job)

    private val _isDataValid = MutableLiveData<Boolean>()
    val isDataValid: LiveData<Boolean> = _isDataValid

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = LoginResult.loading()
            try {
                loginRepository.login(username, password)
                _loginResult.value = LoginResult.success()
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Login Failed", e)

                val loginError = when (e) {
                    is FirebaseAuthInvalidUserException -> Error.EMAIL
                    is FirebaseAuthInvalidCredentialsException -> Error.PASSWORD
                    is FirebaseTooManyRequestsException -> Error.TOO_MANY_ATTEMPTS
                    else -> Error.OTHER
                }
                _loginResult.value = LoginResult.error(loginError)
            }
        }
    }

    fun loginDataChanged(email: String, password: String) {
        _isDataValid.value = isEmailValid(email) && isPasswordValid(password)
    }

    private fun isEmailValid(username: String)
            = Patterns.EMAIL_ADDRESS.matcher(username).matches()

    private fun isPasswordValid(password: String) = password.length > 5

    override fun onCleared() {
        job.cancel()
    }
}
