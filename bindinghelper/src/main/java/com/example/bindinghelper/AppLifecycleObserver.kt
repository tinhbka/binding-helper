package com.example.bindinghelper

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

internal class AppLifecycleObserver(private val context: Context) : DefaultLifecycleObserver {
    private var appHideCountRecent = 0
    private var isFirstTimeRecent = true
    private var appHideCount30m = 0
    private var isFirstTime30m = true
    private var appHideCount5m = 0
    private var isFirstTime5m = true

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        appHideCountRecent++
        appHideCount30m++
        appHideCount5m++

        NotificationHelper.recentContent?.let { content ->
            if (isFirstTimeRecent || appHideCountRecent == content.triggerInterval + 1) {
                NotificationWorker.scheduleUniqueWork(
                    context = context,
                    uniqueWorkName = "recent",
                    eventName = AnalyticLogger.exitApp,
                    duration = content.delayInSecond,
                    unit = TimeUnit.SECONDS,
                    notificationContent = content,
                )
                appHideCountRecent = 0
                isFirstTimeRecent = false
            }
        }

        NotificationHelper.after30MinContent?.let { content ->
            if (isFirstTime30m || appHideCount30m == content.triggerInterval + 1) {
                NotificationWorker.scheduleUniqueWork(
                    context = context,
                    uniqueWorkName = "after_30_min",
                    eventName = AnalyticLogger.exitApp30m,
                    duration = 30 * 60 + content.delayInSecond,
                    unit = TimeUnit.SECONDS,
                    notificationContent = content,
                )
                appHideCount30m = 0
                isFirstTime30m = false
            }
        }

        NotificationHelper.after5MinContent?.let { content ->
            if (isFirstTime5m || appHideCount5m == content.triggerInterval + 1) {
                NotificationWorker.schedulePeriodicWork(
                    context = context,
                    delayDuration = 5 * 60 + content.delayInSecond,
                    repeatInterval = 5 * 60,
                    unit = TimeUnit.SECONDS,
                    eventName = AnalyticLogger.repeat5m,
                    tag = "after_5_min",
                    notificationContent = content
                )
                appHideCount5m = 0
                isFirstTime5m = false
            }
        }

        listOf(1, 3, 7, 15, 30).forEach { day ->
            scheduleKillAppNotification(day)
        }
    }

    private fun scheduleKillAppNotification(day: Int) {
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
        WorkManager.getInstance(context).cancelUniqueWork("recent")
        WorkManager.getInstance(context).cancelAllWorkByTag("after_5_min")
        WorkManager.getInstance(context).cancelUniqueWork("after_30_min")
        WorkManager.getInstance(context).cancelAllWorkByTag("kill_app")
    }
}