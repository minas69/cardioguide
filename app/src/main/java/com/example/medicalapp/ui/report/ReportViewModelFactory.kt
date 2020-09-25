package com.example.medicalapp.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medicalapp.data.LoginDataSource
import com.example.medicalapp.data.LoginRepository
import com.example.medicalapp.data.MainRepository
import com.example.medicalapp.data.model.Data

class ReportViewModelFactory(val data: Data) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportViewModel::class.java)) {
            return ReportViewModel(
                data = data,
                repository = MainRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}