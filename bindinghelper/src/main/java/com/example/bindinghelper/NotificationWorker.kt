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
import java.util.concurrent.TimeUnit

class NotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters,
) :
    Worker(context, workerParams) {
    companion object {
        const val PERIODIC_WORK_NAME = "MyPeriodicWorker"

        fun schedulePeriodicWork(
            context: Context,
            delayDuration: Long = 5,
            repeatInterval: Long = 5,
            unit: TimeUnit = TimeUnit.MINUTES,
            tag: String? = null,
            eventName: String? = null,
            useDefaultContent: Boolean = true,
        ) {
            val data = workDataOf(
                "eventName" to eventName,
                "useDefaultContent" to useDefaultContent,
            )
            val worker =
                PeriodicWorkRequestBuilder<NotificationWorker>(repeatInterval, unit)
                    .setInitialDelay(
                        delayDuration,
                        unit
                    )  // Đợi 5 phút trước khi chạy lần  đầu tiên (tùy chọn)
                    .setInputData(data)
            if (tag != null) {
                worker.addTag(tag)
            }

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                PERIODIC_WORK_NAME,
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
            useDefaultContent: Boolean = true,
        ) {

            val data = workDataOf(
                "eventName" to eventName,
                "useDefaultContent" to useDefaultContent,
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
        val useDefaultContent = inputData.getBoolean(
            "useDefaultContent",
            defaultValue = true
        )
        NotificationHelper.notifyOnAppExit(
            context = applicationContext,
            eventName = eventName,
            useDefaultContent = useDefaultContent,

        )

        return Result.success()
    }
}