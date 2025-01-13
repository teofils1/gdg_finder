package com.example.dmd_project.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gdg_chapter_table")
data class GdgChapterEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val city: String,
    val country: String,
    val region: String,
    val why: String
)