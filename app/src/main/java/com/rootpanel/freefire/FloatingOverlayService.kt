package com.rootpanel.freefire

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.view.WindowManager
import android.widget.FrameLayout

class FloatingOverlayService : Service() {
    
    private val TAG = "FloatingOverlayService"
    private lateinit var gameInjector: GameInjector
    private lateinit var rootUtils: RootUtils
    private var overlayView: FrameLayout? = null
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "FloatingOverlayService Created")
        
        try {
            rootUtils = RootUtils(this)
            gameInjector = GameInjector(rootUtils, this)
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing: ${e.message}")
        }
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started")
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
        
        if (overlayView != null) {
            try {
                val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
                windowManager.removeView(overlayView)
            } catch (e: Exception) {
                Log.e(TAG, "Error removing overlay: ${e.message}")
            }
        }
    }
}
