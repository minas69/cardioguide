package com.example.medicalapp.ui.report

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.example.medicalapp.R
import com.example.medicalapp.data.MainRepository
import com.example.medicalapp.data.model.Data
import kotlinx.coroutines.*
import java.lang.Exception

class ReportViewModel(val data: Data, private val repository: MainRepository) : ViewModel() {

    private val job = Job()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + job)

    private val _result = MutableLiveData<ReportResult>()
    val result: LiveData<ReportResult> = _result

    init {
        viewModelScope.launch {
            _result.value = ReportResult.loading()
            try {
//                val result = repository.sendData(data)
//                _result.value = ReportResult.success(result)
            } catch (e: Exception) {
                Log.e("ReportViewModel", "Error", e)
                _result.value = ReportResult.error(e.message!!)
            }
        }
    }

    override fun onCleared() {
        job.cancel()
    }
}