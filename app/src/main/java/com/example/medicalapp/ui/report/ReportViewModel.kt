package com.example.medicalapp.ui.report

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.example.medicalapp.R
import com.example.medicalapp.data.MainRepository
import com.example.medicalapp.data.Repository
import com.example.medicalapp.data.Result
import com.example.medicalapp.data.model.Data
import kotlinx.coroutines.*
import java.lang.Exception

class ReportViewModel(val data: Map<String, Any>, private val repository: MainRepository) : ViewModel() {

    private val job = Job()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + job)

    private val _result = MutableLiveData<ReportResult>()
    val result: LiveData<ReportResult> = _result

    init {
        viewModelScope.launch {
            _result.value = ReportResult.loading()
            when (val response = Repository.post(data)) {
                is Result.Success -> {
                    _result.value = ReportResult.success(response.data)
                }
                is Result.Error -> {
                    _result.value = ReportResult.error(response.exception.message ?: "Error")
                }
            }
        }
    }

    override fun onCleared() {
        job.cancel()
    }
}