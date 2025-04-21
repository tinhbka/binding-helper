package com.example.bindinghelper

data class NotificationContent(
    val title: String,
    val message: String,
    val notificationId: Int,
    val delayInSecond: Long = 0,
)
