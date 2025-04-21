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
        NotificationWorker.scheduleUniqueWork(
            context = context,
            uniqueWorkName = "pause_app",
            eventName = AnalyticLogger.exitApp,
            duration = NotificationHelper.delayInSecond,
            unit = TimeUnit.SECONDS,
        )
        NotificationWorker.scheduleUniqueWork(
            context = context,
            uniqueWorkName = "recent_app_30m",
            eventName = AnalyticLogger.exitApp30m,
        )
        NotificationWorker.schedulePeriodicWork(
            context = context,
            duration = 5,
            unit = TimeUnit.MINUTES,
            eventName = AnalyticLogger.repeat5m,
            tag = "repeat_5m"
        )
        scheduleKillAppNotification(1)
        scheduleKillAppNotification(3)
        scheduleKillAppNotification(7)
        scheduleKillAppNotification(15)
        scheduleKillAppNotification(30)
    }

    private fun scheduleKillAppNotification(day: Long) {
        val eventName = if (AnalyticLogger.exitAppInDay != null) {
            "${AnalyticLogger.exitAppInDay}_${day}_day"
        } else {
            null
        };
        NotificationWorker.scheduleUniqueWork(
            context = context,
            uniqueWorkName = "kill_app_${day}_day",
            eventName = eventName,
            duration = day,
            unit = TimeUnit.DAYS,
            tag = "kill_app"
        )
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        WorkManager.getInstance(context).cancelUniqueWork("recent_app_30m")
        WorkManager.getInstance(context).cancelUniqueWork("pause_app")
        WorkManager.getInstance(context).cancelAllWorkByTag("repeat_5m")
        WorkManager.getInstance(context).cancelAllWorkByTag("kill_app")
    }
}