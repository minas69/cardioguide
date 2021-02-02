package com.example.medicalapp.ui.form

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.example.medicalapp.Step
import com.example.medicalapp.data.LoginRepository

import com.example.medicalapp.data.MainRepository
import com.example.medicalapp.data.Repository
import com.example.medicalapp.data.model.ResultResponse
import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import com.example.medicalapp.data.Result.Success
import com.example.medicalapp.data.Result.Error

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

    var inputs = HashMap<String, Any>()

    private val _photos: MutableLiveData<HashMap<String, List<Pair<String, String?>>>> = MutableLiveData()
    val photos: LiveData<HashMap<String, List<Pair<String, String?>>>> = _photos

    fun clear() {
        _photos.value = HashMap()
        inputs = HashMap()
        selectStep(-1)
    }

    fun addPhoto(id: String, uri: String) {
        val copy = HashMap<String, List<Pair<String, String?>>>()
        val initial = _photos.value
        if (initial != null) {
            copy.putAll(initial)
        }
        val photoList = copy[id]?.toMutableList() ?: mutableListOf()
        photoList.add(Pair(uri, null))
        copy[id] = photoList

        sendPhoto(id, uri)

        _photos.value = copy
    }

    fun removePhoto(id: String, photo: Pair<String, String?>) {
        val copy = HashMap<String, List<Pair<String, String?>>>()
        val initial = _photos.value
        if (initial != null) {
            copy.putAll(initial)
        }
        val photoList = copy[id]?.toMutableList() ?: mutableListOf()
        val photoToRemove = photoList.find { it.first == photo.first }
        if (photoToRemove != null) {
            photoList.remove(photoToRemove)
        }
        copy[id] = photoList
        _photos.value = copy

        if (inputs.containsKey(id)) {
            val remotePaths = (inputs[id] as List<String>)
            val newRemotePaths = mutableListOf<String>()
            remotePaths.forEach {
                if (it != photo.second) {
                    newRemotePaths.add(it)
                }
            }
            if (newRemotePaths.isEmpty()) {
                inputs.remove(id)
            } else {
                inputs[id] = newRemotePaths
            }
        }
        Log.d("test", inputs.toString())
    }

    fun setPhotos(new: HashMap<String, List<Pair<String, String?>>>) {
        _photos.value = new
    }

    private val _result = MutableLiveData<ResultResponse>()
    val result: LiveData<ResultResponse> = _result

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    val data: List<Step> = application.assets.open("inputs.json").bufferedReader().use {
            Json.decodeFromString(it.readText())
        }

    fun onInputChanged(id: String, value: Any?) {
        if (value != null) {
            inputs[id] = value
        } else {
            inputs.remove(id)
        }
        Log.d("test", inputs.toString())
    }

    private fun sendPhoto(id: String, localPath: String) = viewModelScope.launch {
        when (val response = Repository.sendPhoto(localPath)) {
            is Success -> setRemotePath(id, localPath, response.data)
            is Error -> _errorMessage.value = response.exception.message
        }
    }

    private fun setRemotePath(id: String, localPath: String, remotePath: String) {
        val photos = _photos.value?.get(id)
        if (photos != null) {
            val photo = photos.find { it.first == localPath }
            if (photo != null) {
                val newPhotos = photos.toMutableList()
                newPhotos.remove(photo)
                newPhotos.add(Pair(localPath, remotePath))

                val newValue = mutableListOf<String>()
                newPhotos.forEach {
                    if (it.second != null) {
                        newValue.add(it.second!!)
                    }
                }

                val copy = HashMap<String, List<Pair<String, String?>>>()
                val initial = _photos.value
                if (initial != null) {
                    copy.putAll(initial)
                }
                copy[id] = newPhotos
                _photos.value = copy
                onInputChanged(id, newValue)
            }
        }
    }

    fun canComplete(): Boolean {
        photos.value?.forEach {
            it.value.forEach { l ->
                if (l.second == null) {
                    _errorMessage.value = "Фото загружаются"
                    return false
                }
            }
        }
        data.forEach { step ->
            step.attributes.forEach { attribute ->
                if (attribute.isRequired() && !inputs.containsKey(attribute.getIdd())) {
                    _errorMessage.value = "Заполните все необходимые данные"
                    return false
                }
            }
        }

        return true
    }

    fun complete() {
//        var canComplete = true
//        data.forEach { step ->
//            step.attributes.forEach { attribute ->
//                if (attribute.required) {
//                    canComplete = canComplete && attribute.isFilled()
//                }
//            }
//        }
//        if (!canComplete) {
//            _errorMessage.value = "You must fill required fields"
//            return
//        }
//        val handler = CoroutineExceptionHandler { _, exception ->
//            Log.d("Network", "Caught $exception")
//        }

        viewModelScope.launch {
            when (val response = Repository.post(inputs)) {
                is Success -> {
                    _result.value = response.data
                }
                is Error -> {
                    _errorMessage.value = response.exception.message
                }
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
