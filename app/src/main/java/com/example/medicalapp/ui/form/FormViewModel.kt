package com.example.medicalapp.ui.form

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import com.example.medicalapp.Block
import com.example.medicalapp.data.LoginRepository

import com.example.medicalapp.R
import com.example.medicalapp.data.MainRepository
import com.example.medicalapp.ui.login.LoginResult
import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.lang.Exception

class FormViewModel(
    application: Application,
    private val loginRepository: LoginRepository,
    private val mainRepository: MainRepository
) : AndroidViewModel(application) {

    private val job = Job()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + job)

    private val _isDataValid = MutableLiveData<Boolean>()
    val isDataValid: LiveData<Boolean> = _isDataValid

    private val _selectedStep = MutableLiveData(0)
    val selectedStep: LiveData<Int> = _selectedStep

    val data = application.assets.open("dummy.json").bufferedReader().use {
        Json.decodeFromString<Block>(it.readText())
    }

    fun logout() {
       loginRepository.logout()
    }

    fun selectStep(stepIndex: Int) {
        _selectedStep.value = stepIndex
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