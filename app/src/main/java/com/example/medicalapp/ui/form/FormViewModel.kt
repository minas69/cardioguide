package com.example.medicalapp.ui.form

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.AndroidViewModel
import com.example.medicalapp.Step
import com.example.medicalapp.data.LoginRepository

import com.example.medicalapp.data.MainRepository
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

    private val _result = MutableLiveData<String>()
    val result: LiveData<String> = _result

    val data = application.assets.open("dummy.json").bufferedReader().use {
        Json.decodeFromString<List<Step>>(it.readText())
    }

    fun complete() {
        var canComplete = true
        data.forEach { step ->
            step.attributes.forEach { attribute ->
                if (attribute.required) {
                    canComplete = canComplete && attribute.isFilled()
                }
            }
        }
        if (!canComplete) {
            _result.value = "You must fill required fields"
            return
        }

        viewModelScope.launch {
            try {
                _result.value = mainRepository.post(data)
            } catch (e: Exception) {
                _result.value = "Error occurred"
                Log.e("FormViewModel", "Error occurred on api request", e)
            }
        }
    }

    fun logout() {
       loginRepository.logout()
    }

    fun selectStep(stepIndex: Int) {
        _selectedStep.value = stepIndex
    }

    fun nextStep(): Boolean {
        val selected = _selectedStep.value ?: 0
        if (selected == data.size - 1) return false

        _selectedStep.value = selected + 1
        return true
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