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
            uniqueWorkName = "recent",
            eventName = AnalyticLogger.exitApp,
            duration = NotificationHelper.delayInSecond,
            unit = TimeUnit.SECONDS,
            notificationContent = NotificationHelper.recentContent,
        )
        NotificationWorker.scheduleUniqueWork(
            context = context,
            uniqueWorkName = "after_30_min",
            eventName = AnalyticLogger.exitApp30m,
            notificationContent =  NotificationHelper.after30MinContent,
        )
        NotificationWorker.schedulePeriodicWork(
            context = context,
            delayDuration = 5,
            repeatInterval = 5,
            unit = TimeUnit.MINUTES,
            eventName = AnalyticLogger.repeat5m,
            tag = "after_5_min",
            notificationContent = NotificationHelper.after5MinContent
        )
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        WorkManager.getInstance(context).cancelUniqueWork("recent")
        WorkManager.getInstance(context).cancelAllWorkByTag("after_5_min")
        WorkManager.getInstance(context).cancelUniqueWork("after_30_min")
    }
}