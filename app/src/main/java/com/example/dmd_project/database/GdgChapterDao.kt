package com.example.dmd_project.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GdgChapterDao {
    @Insert
    suspend fun insert(chapter: GdgChapterEntity)

    @Query("SELECT * FROM gdg_chapter_table")
    fun getAllChaptersLive(): LiveData<List<GdgChapterEntity>>
}