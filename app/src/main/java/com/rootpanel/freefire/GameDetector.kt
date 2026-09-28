package com.rootpanel.freefire

import android.app.ActivityManager
import android.content.Context
import android.util.Log

class GameDetector(private val context: Context) {
    
    private val TAG = "GameDetector"
    
    // Free Fire packages
    private val FF_PACKAGES = listOf(
        "com.dts.freefire",      // Global
        "com.dts.freefireth",    // Thailand
        "com.dts.freefirebd"     // Bangladesh
    )
    
    /**
     * Check karo Free Fire game running hai ya nahi
     */
    fun isGameRunning(): Boolean {
        return try {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val runningApps = activityManager.runningAppProcesses
            
            if (runningApps != null) {
                for (appProcess in runningApps) {
                    for (ffPackage in FF_PACKAGES) {
                        if (appProcess.processName == ffPackage) {
                            Log.d(TAG, "✅ Free Fire Running: $ffPackage")
                            return true
                        }
                    }
                }
            }
            
            Log.d(TAG, "❌ Free Fire Not Running")
            false
        } catch (e: Exception) {
            Log.e(TAG, "Error checking game: ${e.message}", e)
            false
        }
    }
    
    /**
     * Detect karo kaun sa Free Fire installed hai
     */
    fun detectInstalledFF(): String {
        return try {
            val pm = context.packageManager
            
            for (ffPackage in FF_PACKAGES) {
                try {
                    pm.getPackageInfo(ffPackage, 0)
                    Log.d(TAG, "✅ Detected: $ffPackage")
                    return ffPackage
                } catch (e: Exception) {
                    // Package not found, continue
                }
            }
            
            Log.d(TAG, "⚠️ No FF variant found, using default")
            "com.dts.freefireth" // Default
        } catch (e: Exception) {
            Log.e(TAG, "Error detecting FF: ${e.message}", e)
            "com.dts.freefireth"
        }
    }
}
