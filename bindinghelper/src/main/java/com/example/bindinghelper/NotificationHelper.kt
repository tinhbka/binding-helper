package com.example.bindinghelper

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationHelper {

    private const val CHANNEL_ID = "default_channel"
    private const val CHANNEL_NAME = "Default Channel"

    internal var backgroundNotificationContent: NotificationContent? = null

    var smallIcon: Int? = null

    var enableNotifications = false

    private fun isEnableNotification(context: Context): Boolean {
        if (!enableNotifications) {
            return false
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }


    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun pushNotification(
        context: Context,
        notificationId: Int,
        title: String? = null,
        message: String? = null,
        pendingIntent: PendingIntent,
        collapsedView: RemoteViews? = null,
        expandedView: RemoteViews? = null,
    ) {
        if (!isEnableNotification(context)) {
            return
        }
        // Tạo kênh thông báo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID).setContentTitle(title)
            .setContentText(message).setPriority(NotificationCompat.PRIORITY_HIGH) // High priority
            .setCustomContentView(collapsedView).setCustomBigContentView(expandedView)
            .setAutoCancel(true) // Auto cancel on click
            .setContentIntent(pendingIntent) // Intent on click
        if (smallIcon != null) {
            notification.setSmallIcon(smallIcon!!)
        }

        // Hiển thị thông báo
        NotificationManagerCompat.from(context).notify(notificationId, notification.build())
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun notifyOnAppExit(
        context: Context,
        eventName: String? = null,
    ) {
        if (!isEnableNotification(context) || backgroundNotificationContent == null) {
            return
        }

        // Hủy thông báo cũ
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.cancel(backgroundNotificationContent!!.notificationId)

        // Tạo intent để mở activity khi nhấn vào thông báo
        val intent = Intent(context, BindingNotificationManager.mainActivity).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            if (eventName != null) {

                putExtra("eventName", eventName)
            }
        }

        val requestCode = System.currentTimeMillis().toInt()
        val pendingIntent = PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        if (eventName != null) {
            // log sự kiện bắn thông báo lên Firebase
            AnalyticLogger.logNotifyEvent(
                context, "show_$eventName"
            )
        }
        // Tạo thông báo
        pushNotification(
            context,
            notificationId = backgroundNotificationContent!!.notificationId,
            title = backgroundNotificationContent!!.title,
            message = backgroundNotificationContent!!.message,
            pendingIntent = pendingIntent,
        )
    }
}