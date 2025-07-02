package com.example.bindinghelper

import android.content.Context
import com.example.bindinghelper.model.NotificationConfig
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel


class NotificationCallHandler(
    private val context: Context,
    messenger: BinaryMessenger,
    private val extraMethods: Map<String, (MethodCall, MethodChannel.Result) -> Unit>? = null
) :
    MethodChannel.MethodCallHandler {
    val channel = MethodChannel(messenger, "com.notification.helper/api")

    init {
        channel.setMethodCallHandler(this)
    }


    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "enableNotification" -> {
                val isEnable = call.argument<Boolean>("isEnable")
                BindingNotificationManager.setEnableNotifications(isEnable ?: false)
                result.success(null)
            }

            "openNotificationSettings" -> {
                BindingNotificationManager.openNotificationSettings(context)
                result.success(null)
            }

            "setRecentContent" -> {
                val title = call.argument<String>("title")
                val message = call.argument<String>("message")
                val enable = call.argument<Boolean>("enable_notification") ?: true
                val delayInSecond = call.argument<Int>("delay_in_second") ?: 0
                val triggerInterval = call.argument<Int>("trigger_interval") ?: 0
                if (title != null && message != null) {
                    NotificationHelper.recentContent = NotificationConfig(
                        title = title,
                        message = message,
                        notificationId = 100,
                        enableNotification = enable,
                        delayInSecond = delayInSecond,
                        triggerInterval = triggerInterval
                    )
                }
            }

            "setAfter5mContent" -> {
                val title = call.argument<String>("title")
                val message = call.argument<String>("message")
                val enable = call.argument<Boolean>("enable_notification") ?: true
                val delayInSecond = call.argument<Int>("delay_in_second") ?: 0
                val triggerInterval = call.argument<Int>("trigger_interval") ?: 0
                if (title != null && message != null) {
                    NotificationHelper.after5MinContent = NotificationConfig(
                        title = title,
                        message = message,
                        notificationId = 100,
                        enableNotification = enable,
                        delayInSecond = delayInSecond,
                        triggerInterval = triggerInterval
                    )
                }
            }

            "setAfter30mContent" -> {
                val title = call.argument<String>("title")
                val message = call.argument<String>("message")
                val enable = call.argument<Boolean>("enable_notification") ?: true
                val delayInSecond = call.argument<Int>("delay_in_second") ?: 0
                val triggerInterval = call.argument<Int>("trigger_interval") ?: 0
                if (title != null && message != null) {
                    NotificationHelper.after30MinContent = NotificationConfig(
                        title = title,
                        message = message,
                        notificationId = 100,
                        enableNotification = enable,
                        delayInSecond = delayInSecond,
                        triggerInterval = triggerInterval
                    )
                }
            }

            "setupEventsName" -> {
                val exitApp = call.argument<String>("exitApp")
                val repeat5m = call.argument<String>("repeat5m")
                val exitAppInDay = call.argument<String>("exitAppInDay")
                val exitApp30m = call.argument<String>("exitApp30m")
                BindingNotificationManager.setupEventsName(
                    exitApp = exitApp,
                    repeat5m = repeat5m,
                    exitAppInDay = exitAppInDay,
                    exitApp30m = exitApp30m,
                )
            }

            else -> {
                extraMethods?.get(call.method)?.invoke(call, result) ?: result.notImplemented()
            }

        }
    }

}