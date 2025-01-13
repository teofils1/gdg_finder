package com.example.dmd_project.requests

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.dmd_project.database.GdgChapterEntity
import com.example.dmd_project.database.GdgDatabase

class RequestsViewModel(application: Application) : AndroidViewModel(application) {

    private val gdgChapterDao = GdgDatabase.getInstance(application).gdgChapterDao

    // LiveData to observe the list of requests
    val requests: LiveData<List<GdgChapterEntity>> = gdgChapterDao.getAllChaptersLive()
}