package com.example.dmd_project.add

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dmd_project.database.GdgChapterEntity
import com.example.dmd_project.database.GdgDatabase
import kotlinx.coroutines.launch

// ViewModel class for managing the UI-related data for AddGdgFragment
class AddGdgViewModel(application: Application) : AndroidViewModel(application) {

    // Database DAO reference
    private val gdgChapterDao = GdgDatabase.getInstance(application).gdgChapterDao

    // Private MutableLiveData to control the Snackbar visibility event
    private val _showSnackbarEvent = MutableLiveData<Boolean>()

    // Public LiveData for external observers (like the fragment) to observe the Snackbar event
    val showSnackBarEvent: LiveData<Boolean>
        get() = _showSnackbarEvent  // Exposes an immutable LiveData to ensure encapsulation

    // Function to reset the Snackbar event after it has been shown
    fun doneShowingSnackbar() {
        _showSnackbarEvent.value = false  // Sets the value to false, indicating the event is complete
    }

    // Function to trigger the Snackbar event when an application is submitted
    fun onSubmitApplication(name: String, email: String, city: String, country: String, region: String, why: String) {
        viewModelScope.launch {
            // Create a new GDG chapter entity
            val chapter = GdgChapterEntity(
                name = name,
                email = email,
                city = city,
                country = country,
                region = region,
                why = why
            )

            // Insert the chapter into the database
            gdgChapterDao.insert(chapter)

            // Trigger the Snackbar event to show success message
            _showSnackbarEvent.value = true
        }
    }
}
