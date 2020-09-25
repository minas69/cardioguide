package com.example.medicalapp.ui.form

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.example.medicalapp.data.LoginRepository

import com.example.medicalapp.R
import com.example.medicalapp.data.MainRepository
import com.example.medicalapp.ui.login.LoginResult
import kotlinx.coroutines.*
import java.lang.Exception

class FormViewModel(
    private val loginRepository: LoginRepository,
    private val mainRepository: MainRepository
) : ViewModel() {

    private val job = Job()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + job)

    private val _isDataValid = MutableLiveData<Boolean>()
    val isDataValid: LiveData<Boolean> = _isDataValid

    fun logout() {
       loginRepository.logout()
    }

    fun dataChanged(vararg data: String) {
        var isValid = true
        for (i in data) {
            isValid = isValid && i.isNotEmpty()
        }
        _isDataValid.value = isValid
    }

    override fun onCleared() {
        job.cancel()
    }
}