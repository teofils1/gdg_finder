package com.example.dmd_project.network

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

// Data class representing a GDG chapter
@Parcelize  // Automatically implements Parcelable, allowing this class to be passed between Android components
data class GdgChapter(
    @Json(name = "chapter_name") val name: String,   // Maps the JSON key "chapter_name" to the property "name"
    @Json(name = "cityarea") val city: String,       // Maps the JSON key "cityarea" to the property "city"
    val country: String,                             // No custom JSON mapping needed since key matches property name
    val region: String,                              // No custom JSON mapping needed since key matches property name
    val website: String,                             // Stores the website URL for the chapter
    val geo: LatLong                                 // Nested LatLong object representing geographic coordinates
) : Parcelable  // Implements Parcelable interface for efficient object serialization in Android

// Data class representing geographic coordinates (latitude and longitude)
@Parcelize
data class LatLong(
    val lat: Double,                                 // Latitude value
    @Json(name = "lng") val long: Double             // Maps the JSON key "lng" to the property "long"
) : Parcelable

// Data class representing the API response containing a list of GDG chapters and filters
@Parcelize
data class GdgResponse(
    @Json(name = "filters_") val filters: Filter,    // Maps the JSON key "filters_" to the property "filters"
    @Json(name = "data") val chapters: List<GdgChapter>  // Maps the JSON key "data" to the property "chapters"
) : Parcelable

// Data class representing filter options for the GDG chapters (e.g., regions)
@Parcelize
data class Filter(
    @Json(name = "region") val regions: List<String> // Maps the JSON key "region" to the property "regions"
) : Parcelable

