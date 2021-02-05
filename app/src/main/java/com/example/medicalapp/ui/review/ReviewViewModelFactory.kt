package com.example.medicalapp.ui.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medicalapp.data.LoginDataSource
import com.example.medicalapp.data.LoginRepository
import com.example.medicalapp.data.MainRepository
import com.example.medicalapp.data.model.Data

class ReviewViewModelFactory(val recordId: String) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReviewViewModel::class.java)) {
            return ReviewViewModel(recordId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}