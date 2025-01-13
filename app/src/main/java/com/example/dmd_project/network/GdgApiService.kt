package com.example.dmd_project.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

// Base URL for the GDG API
private const val BASE_URL = "https://android-kotlin-fun-mars-server.appspot.com/"

// Retrofit interface defining the API endpoints
interface GdgApiService {

    @GET("gdg-directory.json")  // HTTP GET request to fetch the GDG chapters
    fun getChapters():
    // Returns a Deferred object, which is a coroutine-based Job that yields a result (GdgResponse)
            Deferred<GdgResponse>
}

// Create a Moshi instance with support for Kotlin classes
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())  // Adds a JSON adapter for Kotlin data classes
    .build()

// Create a Retrofit instance with Moshi for JSON parsing and a Coroutine Call Adapter
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))  // Converts JSON response to Kotlin objects
    .addCallAdapterFactory(CoroutineCallAdapterFactory())      // Enables support for coroutines
    .baseUrl(BASE_URL)                                         // Sets the base URL for the API
    .build()

// Singleton object providing access to the Retrofit service
object GdgApi {
    // Lazily initialized Retrofit service instance
    val retrofitService: GdgApiService by lazy {
        retrofit.create(GdgApiService::class.java)
    }
}
