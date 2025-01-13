package com.example.dmd_project.search

import android.location.Location
import com.example.dmd_project.network.GdgApiService
import com.example.dmd_project.network.GdgChapter
import com.example.dmd_project.network.GdgResponse
import com.example.dmd_project.network.LatLong
import kotlinx.coroutines.*

// Repository class for managing GDG chapter data from the API
class GdgChapterRepository(private val gdgApiService: GdgApiService) {

    // Cached network request for chapters
    private val request = gdgApiService.getChapters()

    // Holds an in-progress or completed sorting job (Deferred represents an asynchronous result)
    private var inProgressSort: Deferred<SortedData>? = null

    // Flag indicating whether the data has been fully initialized
    var isFullyInitialized = false
        private set  // Setter is private to prevent external modification

    /**
     * Retrieves a list of chapters filtered by region.
     * If no filter is provided, returns the complete list of chapters.
     */
    suspend fun getChaptersForFilter(filter: String?): List<GdgChapter> {
        val data = sortedData()  // Get the sorted data (either cached or newly sorted)
        return filter?.let {
            data.chaptersByRegion[it].orEmpty()  // Return chapters for the specified region or an empty list
        } ?: data.chapters  // If no filter is provided, return all chapters
    }

    /**
     * Retrieves the available region filters.
     */
    suspend fun getFilters(): List<String> = sortedData().filters

    /**
     * Gets sorted data or starts a new sorting job if none is currently running.
     */
    private suspend fun sortedData(): SortedData = withContext(Dispatchers.Main) {
        inProgressSort?.await()  // If a sorting job is in progress, wait for its result
            ?: doSortData()      // Otherwise, start a new sorting job
    }

    /**
     * Starts a new sorting job and caches its result.
     * Optionally, sorts the data based on the provided location.
     */
    private suspend fun doSortData(location: Location? = null): SortedData = coroutineScope {
        val deferred = async(Dispatchers.Default) {
            SortedData.from(request.await(), location)  // Perform sorting in a background thread
        }
        inProgressSort = deferred  // Cache the in-progress sorting job
        deferred.await()  // Wait for the result and return it
    }

    /**
     * Handles location updates by canceling any in-progress sorting and starting a new sort.
     * Marks the data as fully initialized once sorting is complete.
     */
    suspend fun onLocationChanged(location: Location): Unit = withContext(Dispatchers.Main) {
        isFullyInitialized = true  // Mark the data as fully initialized
        inProgressSort?.cancel()   // Cancel any ongoing sorting job
        doSortData(location)       // Start a new sorting job with the updated location
    }

    /**
     * Data class to hold the sorted chapter data.
     */
    private data class SortedData(
        val chapters: List<GdgChapter>,                     // List of all chapters
        val filters: List<String>,                          // List of available region filters
        val chaptersByRegion: Map<String, List<GdgChapter>> // Chapters grouped by region
    ) {
        companion object {
            /**
             * Creates [SortedData] by sorting the response based on the provided location.
             */
            suspend fun from(response: GdgResponse, location: Location?): SortedData =
                withContext(Dispatchers.Default) {
                    // Sort chapters by distance from the provided location
                    val chapters = response.chapters.sortByDistanceFrom(location)
                    // Extract distinct region filters from the sorted chapters
                    val filters = chapters.map { it.region }.distinct()
                    // Group chapters by region
                    val chaptersByRegion = chapters.groupBy { it.region }
                    // Return a new SortedData instance
                    SortedData(chapters, filters, chaptersByRegion)
                }

            /**
             * Sorts a list of chapters by their distance from the provided location.
             * If no location is provided, returns the list as-is.
             */
            private fun List<GdgChapter>.sortByDistanceFrom(location: Location?): List<GdgChapter> {
                return location?.let {
                    sortedBy { distanceBetween(it.geo, location) }  // Sort by distance to the location
                } ?: this  // If location is null, return the original list
            }

            /**
             * Calculates the distance (in meters) between a [LatLong] and a [Location].
             */
            private fun distanceBetween(start: LatLong, location: Location): Float {
                val results = FloatArray(1)  // Array to store the distance result
                Location.distanceBetween(
                    start.lat, start.long,    // Start latitude and longitude
                    location.latitude, location.longitude,  // Destination latitude and longitude
                    results                    // Output array for the distance result
                )
                return results[0]  // Return the calculated distance
            }
        }
    }
}

