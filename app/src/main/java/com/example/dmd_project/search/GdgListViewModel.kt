package com.example.dmd_project.search

import android.location.Location
import androidx.lifecycle.*
import com.example.dmd_project.network.GdgApi
import com.example.dmd_project.network.GdgChapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

// ViewModel class for managing GDG chapter data and UI-related state for GdgListFragment
class GdgListViewModel : ViewModel() {

    // Repository instance for fetching GDG chapter data from the API
    private val repository = GdgChapterRepository(GdgApi.retrofitService)

    // Filter holder to track the currently selected region filter
    private var filter = FilterHolder()

    // Job to manage the current coroutine for data fetching
    private var currentJob: Job? = null

    // LiveData holding the complete list of GDG chapters
    private val _gdgList = MutableLiveData<List<GdgChapter>>()
    val gdgList: LiveData<List<GdgChapter>> get() = _gdgList

    // LiveData holding the list of available region filters
    private val _regionList = MutableLiveData<List<String>>()
    val regionList: LiveData<List<String>> get() = _regionList

    // LiveData to control whether the "need location" snackbar should be shown
    private val _showNeedLocation = MutableLiveData<Boolean>()
    val showNeedLocation: LiveData<Boolean> get() = _showNeedLocation

    // LiveData for the search query input by the user
    val searchQuery = MutableLiveData<String>()

    // MediatorLiveData for the filtered list of GDG chapters
    val filteredGdgList = MediatorLiveData<List<GdgChapter>>().apply {
        // Add sources to observe changes in the GDG list and search query
        addSource(_gdgList) { filterData() }
        addSource(searchQuery) { filterData() }
    }

    init {
        updateQuery()             // Fetch initial data
        monitorInitialization()   // Check if the location is initialized
    }

    /**
     * Fetches the list of chapters and region filters based on the current filter.
     */
    private fun updateQuery() {
        // Cancel any ongoing data fetch job before starting a new one
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            try {
                // Fetch chapters for the current region filter
                _gdgList.value = repository.getChaptersForFilter(filter.currentValue)
                // Fetch and update available region filters if they have changed
                repository.getFilters().let { filters ->
                    if (filters != _regionList.value) {
                        _regionList.value = filters
                    }
                }
            } catch (e: IOException) {
                // If an error occurs, set the list to empty
                _gdgList.value = emptyList()
            }
        }
    }

    /**
     * Monitors whether the repository has been fully initialized within 5 seconds.
     * If not, triggers a snackbar to indicate that location permission may be needed.
     */
    private fun monitorInitialization() {
        viewModelScope.launch {
            kotlinx.coroutines.delay(5_000)  // Wait for 5 seconds
            _showNeedLocation.value = !repository.isFullyInitialized  // Show snackbar if not initialized
        }
    }

    /**
     * Updates the data when the user's location changes.
     */
    fun onLocationUpdated(location: Location) {
        viewModelScope.launch {
            repository.onLocationChanged(location)  // Notify the repository of the location change
            updateQuery()  // Fetch new data based on the updated location
        }
    }

    /**
     * Updates the region filter and fetches new data if the filter changes.
     */
    fun onFilterChanged(filter: String, isChecked: Boolean) {
        if (this.filter.update(filter, isChecked)) {
            updateQuery()  // Fetch new data if the filter was updated
        }
    }

    /**
     * Filters the GDG chapter list based on the search query and region filter.
     */
    private fun filterData() {
        val query = searchQuery.value.orEmpty().lowercase()  // Get the search query in lowercase
        val originalList = _gdgList.value.orEmpty()  // Get the original list of chapters

        filteredGdgList.value = if (query.isBlank()) {
            originalList  // If the query is empty, return the original list
        } else {
            // Filter the list based on whether the chapter name or region contains the query
            originalList.filter { chapter ->
                chapter.name.lowercase().contains(query) || chapter.region.lowercase().contains(query)
            }
        }
    }

    /**
     * Helper class to manage the region filter.
     */
    private class FilterHolder {
        var currentValue: String? = null  // Holds the current filter value
            private set

        /**
         * Updates the filter value and returns whether the filter was changed.
         */
        fun update(filter: String, isChecked: Boolean): Boolean {
            return if (isChecked) {
                currentValue = filter  // Set the new filter if checked
                true
            } else if (currentValue == filter) {
                currentValue = null  // Reset the filter if unchecked
                true
            } else false  // No change if the filter was not updated
        }
    }
}


