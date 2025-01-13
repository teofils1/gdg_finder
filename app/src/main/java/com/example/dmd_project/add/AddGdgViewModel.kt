package com.example.dmd_project.add;

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddGdgViewModel : ViewModel() {

    // Backing property for snackbar event
    private val _showSnackbarEvent = MutableLiveData<Boolean>()
    val showSnackBarEvent: LiveData<Boolean> get() = _showSnackbarEvent

    /**
     * Clears the snackbar event state to prevent duplicate snackbar displays.
     */
    fun doneShowingSnackbar() {
        _showSnackbarEvent.value = false
    }

    /**
     * Triggers the snackbar event to notify observers.
     */
    fun onSubmitApplication() {
        _showSnackbarEvent.value = true
    }
}

