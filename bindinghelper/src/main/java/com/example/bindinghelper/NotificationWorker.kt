package com.example.bindinghelper

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.bindinghelper.model.NotificationConfig
import java.util.concurrent.TimeUnit

class NotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters,
) :
    Worker(context, workerParams) {
    companion object {

        fun schedulePeriodicWork(
            context: Context,
            delayDuration: Long = 5,
            repeatInterval: Long = 5,
            unit: TimeUnit = TimeUnit.MINUTES,
            tag: String? = null,
            eventName: String? = null,
            notificationContent: NotificationConfig? = null,
        ) {
            val data = workDataOf(
                "eventName" to eventName,
                "id" to notificationContent?.notificationId,
                "title" to notificationContent?.title,
                "message" to notificationContent?.message,
            )
            val worker =
                PeriodicWorkRequestBuilder<NotificationWorker>(repeatInterval, unit)
                    .setInitialDelay(
                        delayDuration,
                        unit
                    )
                    .setInputData(data)
            if (tag != null) {
                worker.addTag(tag)
            }

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "MyPeriodicWorker",
                ExistingPeriodicWorkPolicy.UPDATE,  // Nếu đã có worker này thì cập nhật
                worker.build()
            )
        }

        fun scheduleUniqueWork(
            context: Context,
            uniqueWorkName: String,
            duration: Long = 30,
            unit: TimeUnit = TimeUnit.MINUTES,
            eventName: String? = null,
            tag: String? = null,
            notificationContent: NotificationConfig? = null,
        ) {

            val data = workDataOf(
                "eventName" to eventName,
                "id" to notificationContent?.notificationId,
                "title" to notificationContent?.title,
                "message" to notificationContent?.message,
            )
            val worker =
                OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInitialDelay(
                        duration,
                        unit
                    )
                    .setInputData(data)
            if (tag != null) {
                worker.addTag(tag)
            }

            WorkManager.getInstance(context).enqueueUniqueWork(
                uniqueWorkName,
                ExistingWorkPolicy.REPLACE,  // Nếu đã có worker này thì thay thế
                worker.build()
            )
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun doWork(): Result {
        val eventName = inputData.getString("eventName")
        val id = inputData.getInt("id", 100)
        val title = inputData.getString("title")
        val message = inputData.getString("message")
        if(title.isNullOrEmpty() || message.isNullOrEmpty()) {
            return Result.failure()
        }
        NotificationHelper.notifyOnAppExit(
            context = applicationContext,
            eventName = eventName,
            notificationContent = NotificationConfig(
                notificationId = id,
                title = title,
                message = message,
            ),
        )

        return Result.success()
    }
}