package com.example.medicalapp.ui.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medicalapp.data.LoginDataSource
import com.example.medicalapp.data.LoginRepository
import com.example.medicalapp.data.MainRepository

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class FormViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FormViewModel::class.java)) {
            return FormViewModel(
                loginRepository = LoginRepository,
                mainRepository = MainRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}