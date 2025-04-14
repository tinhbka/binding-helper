package com.example.bindinghelper

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object BindingNotificationManager {

    fun init(context: Context) {
        val observer = AppLifecycleObserver(context.applicationContext)
        ProcessLifecycleOwner.get().lifecycle.addObserver(observer)
    }

    fun scheduleQuitNotification(context: Context) {
        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    fun enableQuitNotification(context: Context) {
        context.startService(Intent(context, NotificationService::class.java))
    }
}