package com.example.medicalapp.ui.form

import android.app.Application
import android.os.Parcelable
import android.os.ResultReceiver
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.example.medicalapp.Step
import com.example.medicalapp.data.LoginRepository

import com.example.medicalapp.data.MainRepository
import com.example.medicalapp.data.model.ResultResponse
import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.lang.Exception

class FormViewModel(
    application: Application,
    private val loginRepository: LoginRepository,
    private val mainRepository: MainRepository,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    companion object {
        private const val SELECTED_STEP = "selected_step"
    }

    private val job = Job()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + job)

//    private val _isDataValid = MutableLiveData<Boolean>()
//    val isDataValid: LiveData<Boolean> = _isDataValid

    val selectedStep: LiveData<Int> = savedStateHandle.getLiveData(SELECTED_STEP, 0)

    var _inputs = HashMap<String, Any>()

    private val _result = MutableLiveData<ResultResponse>()
    val result: LiveData<ResultResponse> = _result

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    val data: List<Step> = application.assets.open("dummy3.json").bufferedReader().use {
            Json.decodeFromString(it.readText())
        }

    fun setInputs(inputs: HashMap<String, Any>) {
        _inputs = inputs
    }

    fun onInputChanged(id: String, value: Any?) {
        if (value != null) {
            _inputs[id] = value
        } else {
            _inputs.remove(id)
        }
        Log.d("test", _inputs.toString())
    }

    fun complete() {
        var canComplete = true
//        data.forEach { step ->
//            step.attributes.forEach { attribute ->
//                if (attribute.required) {
//                    canComplete = canComplete && attribute.isFilled()
//                }
//            }
//        }
        if (!canComplete) {
            _errorMessage.value = "You must fill required fields"
            return
        }

        viewModelScope.launch {
            try {
//                _result.value = mainRepository.post(_inputs)
//                _result.value = ResultResponse(0.0123, 86.9805, 22.9557)
                mainRepository.post(_inputs)
            } catch (e: Exception) {
                _errorMessage.value = "Error occurred"
                Log.e("api", "Error occurred on api request", e)
            }
        }
    }

    fun logout() {
       loginRepository.logout()
    }

    fun selectStep(stepIndex: Int) {
        savedStateHandle.set(SELECTED_STEP, stepIndex)
    }

    fun nextStep(): Boolean {
        val selected = selectedStep.value ?: 0
        if (selected == data.size - 1) return false

        savedStateHandle.set(SELECTED_STEP, selected + 1)
        return true
    }

    override fun onCleared() {
        job.cancel()
    }
}

interface Value: Parcelable
