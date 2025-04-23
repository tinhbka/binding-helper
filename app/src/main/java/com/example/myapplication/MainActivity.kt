package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.app.R
import com.example.bindinghelper.BindingNotificationManager

class MainActivity : AppCompatActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {

        }
    }


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
        BindingNotificationManager.onRestart(this, intent)

        findViewById<Button>(R.id.setText).setOnClickListener {
            BindingNotificationManager.setTemporaryContent(
                title = "AAAAAAAA",
                message = "vvvvvvv",
            )
        }
        findViewById<Button>(R.id.reset).setOnClickListener {
            BindingNotificationManager.clearTemporaryContent()
        }
    }
}