package com.example.dmd_project.search

import android.location.Location
import androidx.lifecycle.*
import com.example.dmd_project.network.GdgApi
import com.example.dmd_project.network.GdgChapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

class GdgListViewModel : ViewModel() {

    private val repository = GdgChapterRepository(GdgApi.retrofitService)
    private var filter = FilterHolder()
    private var currentJob: Job? = null

    private val _gdgList = MutableLiveData<List<GdgChapter>>()
    val gdgList: LiveData<List<GdgChapter>> get() = _gdgList

    private val _regionList = MutableLiveData<List<String>>()
    val regionList: LiveData<List<String>> get() = _regionList

    private val _showNeedLocation = MutableLiveData<Boolean>()
    val showNeedLocation: LiveData<Boolean> get() = _showNeedLocation

    // Add MutableLiveData for the search query
    val searchQuery = MutableLiveData<String>()

    // Add MediatorLiveData for filtered results
    val filteredGdgList = MediatorLiveData<List<GdgChapter>>().apply {
        addSource(_gdgList) { filterData() }
        addSource(searchQuery) { filterData() }
    }

    init {
        updateQuery()
        monitorInitialization()
    }

    private fun updateQuery() {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            try {
                _gdgList.value = repository.getChaptersForFilter(filter.currentValue)
                repository.getFilters().let { filters ->
                    if (filters != _regionList.value) {
                        _regionList.value = filters
                    }
                }
            } catch (e: IOException) {
                _gdgList.value = emptyList()
            }
        }
    }

    private fun monitorInitialization() {
        viewModelScope.launch {
            kotlinx.coroutines.delay(5_000)
            _showNeedLocation.value = !repository.isFullyInitialized
        }
    }

    fun onLocationUpdated(location: Location) {
        viewModelScope.launch {
            repository.onLocationChanged(location)
            updateQuery()
        }
    }

    fun onFilterChanged(filter: String, isChecked: Boolean) {
        if (this.filter.update(filter, isChecked)) {
            updateQuery()
        }
    }

    // Add a method to filter data based on the search query and region filter
    private fun filterData() {
        val query = searchQuery.value.orEmpty().lowercase()
        val originalList = _gdgList.value.orEmpty()

        filteredGdgList.value = if (query.isBlank()) {
            originalList
        } else {
            originalList.filter { chapter ->
                chapter.name.lowercase().contains(query) || chapter.region.lowercase().contains(query)
            }
        }
    }

    private class FilterHolder {
        var currentValue: String? = null
            private set

        fun update(filter: String, isChecked: Boolean): Boolean {
            return if (isChecked) {
                currentValue = filter
                true
            } else if (currentValue == filter) {
                currentValue = null
                true
            } else false
        }
    }
}

