package com.example.bindinghelper

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

internal class AppLifecycleObserver(private val context: Context) : DefaultLifecycleObserver {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        NotificationHelper.notifyOnAppExit(
            context = context,
            eventName = "pause_app",
        )
        NotificationWorker.scheduleUniqueWork(
            context = context,
            uniqueWorkName = "pause_app_30m",
            eventName = "pause_app_30m",
        )
        NotificationWorker.schedulePeriodicWork(
            context = context,
            duration = 5,
            unit = TimeUnit.MINUTES,
            eventName = "repeat_5m",
            tag = "repeat_5m"
        )
        scheduleKillAppNotification(1)
        scheduleKillAppNotification(3)
        scheduleKillAppNotification(7)
        scheduleKillAppNotification(15)
        scheduleKillAppNotification(30)
    }

    private fun scheduleKillAppNotification(day: Long) {
        NotificationWorker.scheduleUniqueWork(
            context = context,
            uniqueWorkName = "kill_app_${day}_day",
            eventName = "kill_app_${day}_day",
            duration = day,
            unit = TimeUnit.DAYS
        )
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        WorkManager.getInstance(context).cancelUniqueWork("pause_app_30m")
        WorkManager.getInstance(context).cancelAllWorkByTag("repeat_5m")
    }
}