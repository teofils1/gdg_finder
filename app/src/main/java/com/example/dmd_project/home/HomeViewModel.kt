package com.example.dmd_project.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// ViewModel class for managing UI-related data and events in HomeFragment
class HomeViewModel : ViewModel() {

    // Private MutableLiveData to control navigation to the search screen
    private val _navigateToSearch = MutableLiveData(false)

    // Public LiveData for external observers to observe the navigation event
    val navigateToSearch: LiveData<Boolean>
        get() = _navigateToSearch  // Exposes an immutable LiveData for encapsulation

    // Function to be called when the Floating Action Button (FAB) is clicked
    fun onFabClicked() {
        _navigateToSearch.value = true  // Triggers the navigation event by setting the value to true
    }

    // Function to reset the navigation event after navigation is complete
    fun onNavigatedToSearch() {
        _navigateToSearch.value = false  // Resets the value to false, indicating that the event has been handled
    }
}

