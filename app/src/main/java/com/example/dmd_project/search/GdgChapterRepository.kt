package com.example.dmd_project.search

import android.location.Location
import com.example.dmd_project.network.GdgApiService
import com.example.dmd_project.network.GdgChapter
import com.example.dmd_project.network.GdgResponse
import com.example.dmd_project.network.LatLong
import kotlinx.coroutines.*

class GdgChapterRepository(private val gdgApiService: GdgApiService) {

    // Cached network request
    private val request = gdgApiService.getChapters()

    // In-progress or completed sorting job
    private var inProgressSort: Deferred<SortedData>? = null

    var isFullyInitialized = false
        private set

    /**
     * Retrieves a list of chapters filtered by region.
     */
    suspend fun getChaptersForFilter(filter: String?): List<GdgChapter> {
        val data = sortedData()
        return filter?.let { data.chaptersByRegion[it].orEmpty() } ?: data.chapters
    }

    /**
     * Retrieves available region filters.
     */
    suspend fun getFilters(): List<String> = sortedData().filters

    /**
     * Gets sorted data or starts a new sort if none is running.
     */
    private suspend fun sortedData(): SortedData = withContext(Dispatchers.Main) {
        inProgressSort?.await() ?: doSortData()
    }

    /**
     * Starts a new sorting job and caches its result.
     */
    private suspend fun doSortData(location: Location? = null): SortedData = coroutineScope {
        val deferred = async(Dispatchers.Default) { SortedData.from(request.await(), location) }
        inProgressSort = deferred
        deferred.await()
    }

    /**
     * Handles location updates by canceling in-progress sorting and starting a new one.
     */
    suspend fun onLocationChanged(location: Location): Unit = withContext(Dispatchers.Main) {
        isFullyInitialized = true
        inProgressSort?.cancel()
        doSortData(location)
    }

    /**
     * Holds sorted chapter data.
     */
    private data class SortedData(
        val chapters: List<GdgChapter>,
        val filters: List<String>,
        val chaptersByRegion: Map<String, List<GdgChapter>>
    ) {
        companion object {
            /**
             * Creates [SortedData] by sorting the response based on the provided location.
             */
            suspend fun from(response: GdgResponse, location: Location?): SortedData =
                withContext(Dispatchers.Default) {
                    val chapters = response.chapters.sortByDistanceFrom(location)
                    val filters = chapters.map { it.region }.distinct()
                    val chaptersByRegion = chapters.groupBy { it.region }
                    SortedData(chapters, filters, chaptersByRegion)
                }

            /**
             * Sorts a list of chapters by distance from the provided location.
             */
            private fun List<GdgChapter>.sortByDistanceFrom(location: Location?): List<GdgChapter> {
                return location?.let {
                    sortedBy { distanceBetween(it.geo, location) }
                } ?: this
            }

            /**
             * Calculates the distance (in meters) between a [LatLong] and a [Location].
             */
            private fun distanceBetween(start: LatLong, location: Location): Float {
                val results = FloatArray(1)
                Location.distanceBetween(
                    start.lat, start.long,
                    location.latitude, location.longitude,
                    results
                )
                return results[0]
            }
        }
    }
}
