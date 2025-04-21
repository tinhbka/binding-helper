package com.example.myapplication

import android.app.Application
import android.util.Log
import com.example.app.R
import com.example.bindinghelper.BindingNotificationManager

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d("App", "Application started")
        BindingNotificationManager.init(
            this,
            MainActivity::class.java,
            R.drawable.ic_launcher_foreground,
        )
        BindingNotificationManager.setEnableNotifications(true)
        BindingNotificationManager.buildBackgroundNotification(
            title = "Hidden App",
            message = "App is running in background",
            delayInSecond = 5
        )
    }
}
