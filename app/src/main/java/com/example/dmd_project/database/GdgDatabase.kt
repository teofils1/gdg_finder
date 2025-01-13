package com.example.dmd_project.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [GdgChapterEntity::class], version = 1, exportSchema = false)
abstract class GdgDatabase : RoomDatabase() {
    abstract val gdgChapterDao: GdgChapterDao

    companion object {
        @Volatile
        private var INSTANCE: GdgDatabase? = null

        fun getInstance(context: Context): GdgDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        GdgDatabase::class.java,
                        "gdg_database"
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
