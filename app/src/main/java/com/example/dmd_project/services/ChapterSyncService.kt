package com.example.dmd_project.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.dmd_project.R
import com.example.dmd_project.network.GdgApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChapterSyncService : Service() {

    private val CHANNEL_ID = "ChapterSyncChannel"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1, buildNotification("Checking for updates..."))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Start syncing data in a coroutine
        CoroutineScope(Dispatchers.IO).launch {
            checkForUpdates()
            stopSelf() // Stop service once work is complete
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    @RequiresApi(Build.VERSION_CODES.M)
    private suspend fun checkForUpdates() {
        try {
            val chapters = GdgApi.retrofitService.getChapters().await()
            if (chapters.chapters.isNotEmpty()) {
                // Notify user about new chapters
                val notification = buildNotification("New GDG chapters are available!")
                val manager = getSystemService(NotificationManager::class.java)
                manager.notify(2, notification)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun buildNotification(contentText: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("GDG Chapter Sync")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_sync)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Chapter Sync Service",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}
