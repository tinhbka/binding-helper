package com.example.bindinghelper

import android.content.Context
import android.os.Build
import android.os.Bundle
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics

object AnalyticLogger {
    var exitApp: String? = "recent_app"
    var exitApp30m: String? = "recent_app_30m"
    var repeat5m: String? = "repeat_5m"
    var exitAppInDay: String? = "exit_app"

    private fun logEvent(
        context: Context,
        eventName: String,
        params: Map<String, String>? = null
    ) {

        FirebaseApp.initializeApp(context)
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val bundle = Bundle()
        if (params != null) {
            for ((key, value) in params) {
                bundle.putString(key, value)
            }
        }
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    fun logNotifyEvent(
        context: Context,
        eventName: String,
        params: Map<String, String>? = null
    ) {
        val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            context.packageManager
                .getPackageInfo(context.packageName, 0)
                .longVersionCode
        } else {
            context.packageManager
                .getPackageInfo(context.packageName, 0)
                .versionCode
        }
        logEvent(context, eventName + "_version_" + versionCode, params)
    }
}