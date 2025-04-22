package com.example.bindinghelper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ProcessLifecycleOwner

object BindingNotificationManager {
    internal lateinit var mainActivityClass: Class<*>
    private val permissionHandler = Handler(Looper.getMainLooper())
    private var checkPermissionRunnable: Runnable? = null

    fun init(context: Context, mainActivity: Class<*>, icon: Int) {
        val observer = AppLifecycleObserver(context)
        this.mainActivityClass = mainActivity
        NotificationHelper.smallIcon = icon
        ProcessLifecycleOwner.get().lifecycle.addObserver(observer)
    }

    fun setEnableNotifications(enable: Boolean) {
        NotificationHelper.enableNotifications = enable
    }

    fun buildBackgroundNotification(
        title: String,
        message: String,
    ) {
        NotificationHelper.mainContent = NotificationContent(
            notificationId = 100,
            title = title,
            message = message,
        )
    }

    fun setDelayTime(
        delayInSecond: Long = 0
    ) {
        NotificationHelper.delayInSecond = delayInSecond
    }

    fun setTemporaryContent(
        title: String,
        message: String,
    ) {
        NotificationHelper.temporaryContent = NotificationContent(
            notificationId = 101,
            title = title,
            message = message,
        )
    }

    fun clearTemporaryContent() {
        NotificationHelper.temporaryContent = null
    }

    fun onRestart(
        context: Context,
        intent: Intent,
        onNotificationClicked: ((eventKey: String) -> Unit)? = null
    ) {
        val eventName = intent.getStringExtra("eventName")
        if (!eventName.isNullOrEmpty()) {
            AnalyticLogger.logNotifyEvent(context, "open_$eventName")
            onNotificationClicked?.invoke(eventName)
        }
    }

    fun openNotificationSettings(
        activity: Activity,
    ) {
        val checkNotificationPermissionRunnable: Runnable = object : Runnable {
            override fun run() {
                val notificationManagerCompat = NotificationManagerCompat.from(activity)
                val isNotificationEnabled = notificationManagerCompat.areNotificationsEnabled()
                if (isNotificationEnabled) {
                    permissionHandler.removeCallbacks(this)
                    checkPermissionRunnable = null
                    val intent1 = Intent(activity, activity::class.java)
                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    activity.startActivity(intent1)
                } else {
                    permissionHandler.postDelayed(this, 500)
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, activity.packageName)
            activity.startActivity(intent)

            // Bắt đầu kiểm tra quyền thông báo
            permissionHandler.postDelayed(checkNotificationPermissionRunnable, 50)
        } else {
            openAppSettings(activity)
        }
    }

    private fun openAppSettings(activity: Activity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)

        activity.let {
            intent.data = Uri.fromParts("package", it.packageName, null)
            it.startActivity(intent)
        }
    }


    fun setupEventsName(
        exitApp: String? = null,
        exitApp30m: String? = null,
        repeat5m: String? = null,
        exitAppInDay: String? = null,
    ) {
        AnalyticLogger.exitApp = exitApp
        AnalyticLogger.exitApp30m = exitApp30m
        AnalyticLogger.repeat5m = repeat5m
        AnalyticLogger.exitAppInDay = exitAppInDay
    }
}