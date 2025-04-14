package com.example.bindinghelper

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

internal class AppLifecycleObserver(private val context: Context) : DefaultLifecycleObserver {
    override fun onStop(owner: LifecycleOwner) {
        // App chuyển vào background
        BindingNotificationManager.scheduleQuitNotification(context)
    }
}