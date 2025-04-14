package com.example.bindinghelper

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

class RecentWorker(
    private val context: Context,
    workerParams: WorkerParameters,
) :
    Worker(context, workerParams) {
    companion object {
        fun schedulePeriodicWork(
            context: Context,
            eventName: String,
            duration: Long = 5,
            unit: TimeUnit = TimeUnit.MINUTES,
            tag: String? = null
        ) {
            val data = workDataOf(
                "eventName" to eventName,
            )
            val worker =
                PeriodicWorkRequestBuilder<RecentWorker>(5, TimeUnit.MINUTES)
                    .setInitialDelay(
                        duration,
                        unit
                    )  // Đợi 5 phút trước khi chạy lần đầu tiên (tùy chọn)
                    .setInputData(data)
            if (tag != null) {
                worker.addTag(tag)
            }
            val workRequest = worker.build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "MyPeriodicWorker",
                ExistingPeriodicWorkPolicy.UPDATE,  // Nếu đã có worker này thì cập nhật
                workRequest
            )
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun doWork(): Result {
        val eventName = inputData.getString("eventName")

        NotificationHelper.pushNotification(applicationContext, "Hello", "This is a test notification from recent!")

        return Result.success()
    }
}