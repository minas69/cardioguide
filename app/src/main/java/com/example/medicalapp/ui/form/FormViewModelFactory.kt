package com.example.medicalapp.ui.form

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import com.example.medicalapp.data.LoginDataSource
import com.example.medicalapp.data.LoginRepository
import com.example.medicalapp.data.MainRepository

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class FormViewModelFactory(
    private val application: Application,
    owner: SavedStateRegistryOwner,
    bundle: Bundle?
) : AbstractSavedStateViewModelFactory(owner, bundle) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(FormViewModel::class.java)) {
            return FormViewModel(
                application = application,
                loginRepository = LoginRepository,
                mainRepository = MainRepository,
                handle
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}