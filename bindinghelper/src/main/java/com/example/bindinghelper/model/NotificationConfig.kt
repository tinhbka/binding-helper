package com.example.bindinghelper.model

data class NotificationConfig(
    val title: String,
    val message: String,
    val enableNotification: Boolean = true,
    val notificationId: Int,
    val delayInSecond: Int = 0,
    val triggerInterval: Int = 0
)
