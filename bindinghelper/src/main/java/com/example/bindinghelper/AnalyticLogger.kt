package com.example.bindinghelper

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresPermission
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics

object AnalyticLogger {
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