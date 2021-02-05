package com.example.medicalapp.ui.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.example.medicalapp.data.Repository
import com.example.medicalapp.data.Result
import kotlinx.coroutines.*

class ReviewViewModel(val recordId: String) : ViewModel() {

    private val job = Job()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + job)

    private val _success = MutableLiveData(false)
    val success: LiveData<Boolean> = _success

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _isDataValid = MutableLiveData(false)
    val isDataValid: LiveData<Boolean> = _isDataValid

    private var aware: Boolean? = null
    private var helpful: Boolean? = null
    private var rate: Int? = null

    fun onInputChanged(aware: Boolean?, helpful: Boolean?, rate: Int?) {
        this.aware = aware
        this.helpful = helpful
        this.rate = rate

        _isDataValid.value = this.aware != null && this.helpful != null && this.rate != null
    }

    fun sendReview() = viewModelScope.launch {
        when (val response = Repository.sendReview(recordId, aware!!, helpful!!, rate!! + 1)) {
            is Result.Success -> {
                _success.value = true
            }
            is Result.Error -> {
                _error.value = response.exception.message
            }
        }
    }

    override fun onCleared() {
        job.cancel()
    }
}