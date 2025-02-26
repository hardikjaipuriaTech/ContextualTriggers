package com.strath.ct

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

/**
 * MainActivity starts up the application. If previously stopped in boot and the user
 * has pressed to start the application then it is used to restart the application.
 */
class MainActivity : AppCompatActivity() {
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("CT MainActivity", "Calling Intent ContextDataManager Service")
        val contextDataManagerIntent = Intent(this, ContextDataManager::class.java)
        startService(contextDataManagerIntent)
        finish()
    }
}