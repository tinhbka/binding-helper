package com.example.bindinghelper

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ProcessLifecycleOwner

object BindingNotificationManager {
    internal lateinit var mainActivity: Class<*>

    fun init(context: Context, mainActivity: Class<*>, icon: Int) {
        val observer = AppLifecycleObserver(context)
        this.mainActivity = mainActivity
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
        NotificationHelper.backgroundNotificationContent = NotificationContent(
            notificationId = 100,
            title = title,
            message = message,
        )
    }

    fun onMainActivityCreated(
        activity: Activity,
        onNotificationClicked: ((eventKey: String) -> Unit)? = null
    ) {
        val eventName = activity.intent.getStringExtra("eventName")
        if (eventName != null) {
            AnalyticLogger.logNotifyEvent(activity, "open_$eventName")
            onNotificationClicked?.invoke(eventName)
        }
    }

    fun onMainActivityNewIntent(
        activity: Activity,
        onNotificationClicked: ((eventKey: String) -> Unit)? = null
    ) {
        val eventName = activity.intent.getStringExtra("eventName")
        if (eventName != null) {
            AnalyticLogger.logNotifyEvent(activity, "open_$eventName")
            onNotificationClicked?.invoke(eventName)
        }
    }
}