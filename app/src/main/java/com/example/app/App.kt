package com.example.app
import android.app.Application
import android.util.Log
import com.example.bindinghelper.BindingNotificationManager

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d("App", "Application started")
        BindingNotificationManager.init(this)
    }
}
