package com.example.dmd_project.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    // Backing property for navigation events
    private val _navigateToSearch = MutableLiveData(false)
    val navigateToSearch: LiveData<Boolean> get() = _navigateToSearch

    /**
     * Trigger navigation to the search screen when the FAB is clicked.
     */
    fun onFabClicked() {
        _navigateToSearch.value = true
    }

    /**
     * Reset navigation state after completing the navigation.
     */
    fun onNavigatedToSearch() {
        _navigateToSearch.value = false
    }
}
