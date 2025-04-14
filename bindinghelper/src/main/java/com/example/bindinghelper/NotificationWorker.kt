package com.example.bindinghelper

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorker(appContext: Context, params: WorkerParameters) : Worker(appContext, params) {
    override fun doWork(): Result {
        NotificationHelper.pushNotification(
            applicationContext,
            "App Quit",
            "Bạn vừa thoát app, và đây là thông báo!"
        )
        return Result.success()
    }
}