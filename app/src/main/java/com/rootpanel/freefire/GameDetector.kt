package com.rootpanel.freefire

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log

class GameDetector(private val context: Context) {
    
    private val TAG = "GameDetector"
    
    private val FFPackages = listOf(
        "com.dts.freefireth",
        "com.dts.freefire",
        "com.dts.freefirebd"
    )
    
    fun detectFreeFirePackage(): String? {
        Log.d(TAG, "🔍 Searching for Free Fire packages...")
        
        for (packageName in FFPackages) {
            try {
                val info = context.packageManager.getPackageInfo(packageName, 0)
                Log.d(TAG, "✅ FOUND: $packageName (v${info.versionName})")
                return packageName
            } catch (e: Exception) {
                Log.d(TAG, "❌ Not found: $packageName")
            }
        }
        
        Log.e(TAG, "❌ No Free Fire package found on device!")
        return null
    }
}
