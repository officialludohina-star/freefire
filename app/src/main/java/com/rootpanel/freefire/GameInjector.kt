package com.rootpanel.freefire

import android.content.Context
import android.util.Log

class GameInjector(private val rootUtils: RootUtils, private val context: Context) {
    
    private val TAG = "GameInjector"
    
    private val FF_PACKAGES = listOf(
        "com.dts.freefire",
        "com.dts.freefireth",
        "com.dts.freefirebd"
    )
    
    private val FF_PACKAGE: String by lazy { detectFFPackage() }
    
    private var espBoxEnabled = false
    private var espLineEnabled = false
    private var espNameEnabled = false
    private var aimBotEnabled = false
    
    private fun detectFFPackage(): String {
        return try {
            FF_PACKAGES.firstOrNull { packageName ->
                rootUtils.isPackageInstalled(packageName)
            } ?: "com.dts.freefireth"
        } catch (e: Exception) {
            "com.dts.freefireth"
        }
    }
    
    fun injectESPBox() {
        Thread {
            try {
                espBoxEnabled = true
                Log.d(TAG, "✅ ESP Box Injected")
                
                val dollarSign = "$"
                val command = "su -c \"PID=${dollarSign}(pidof $FF_PACKAGE); if [ ! -z ${dollarSign}PID ]; then echo '${dollarSign}PID:esp_box:1' > /data/local/tmp/game_hooks; fi\""
                rootUtils.executeSuperUserCommand(command)
                
            } catch (e: Exception) {
                Log.e(TAG, "ESP Box Error: ${e.message}")
            }
        }.start()
    }
    
    fun injectESPLine() {
        Thread {
            try {
                espLineEnabled = true
                Log.d(TAG, "✅ ESP Line Injected")
                
                val dollarSign = "$"
                val command = "su -c \"PID=${dollarSign}(pidof $FF_PACKAGE); if [ ! -z ${dollarSign}PID ]; then echo '${dollarSign}PID:esp_line:1' > /data/local/tmp/game_hooks; fi\""
                rootUtils.executeSuperUserCommand(command)
                
            } catch (e: Exception) {
                Log.e(TAG, "ESP Line Error: ${e.message}")
            }
        }.start()
    }
    
    fun injectAimBot(smooth: Float = 5.0f) {
        Thread {
            try {
                aimBotEnabled = true
                Log.d(TAG, "✅ Aim Bot Injected (Smooth: $smooth)")
                
                val dollarSign = "$"
                val command = "su -c \"PID=${dollarSign}(pidof $FF_PACKAGE); if [ ! -z ${dollarSign}PID ]; then echo '${dollarSign}PID:aimbot:1:$smooth' > /data/local/tmp/game_hooks; fi\""
                rootUtils.executeSuperUserCommand(command)
                
            } catch (e: Exception) {
                Log.e(TAG, "Aim Bot Error: ${e.message}")
            }
        }.start()
    }
    
    fun injectSilentAim() {
        Thread {
            try {
                Log.d(TAG, "✅ Silent Aim Injected")
                
                val dollarSign = "$"
                val command = "su -c \"PID=${dollarSign}(pidof $FF_PACKAGE); if [ ! -z ${dollarSign}PID ]; then echo '${dollarSign}PID:silent_aim:1' > /data/local/tmp/game_hooks; fi\""
                rootUtils.executeSuperUserCommand(command)
                
            } catch (e: Exception) {
                Log.e(TAG, "Silent Aim Error: ${e.message}")
            }
        }.start()
    }
    
    fun injectESPName() {
        Thread {
            try {
                espNameEnabled = true
                Log.d(TAG, "✅ ESP Name Injected")
                
                val dollarSign = "$"
                val command = "su -c \"PID=${dollarSign}(pidof $FF_PACKAGE); if [ ! -z ${dollarSign}PID ]; then echo '${dollarSign}PID:esp_name:1' > /data/local/tmp/game_hooks; fi\""
                rootUtils.executeSuperUserCommand(command)
                
            } catch (e: Exception) {
                Log.e(TAG, "ESP Name Error: ${e.message}")
            }
        }.start()
    }
    
    fun injectESPHealth() {
        Thread {
            try {
                Log.d(TAG, "✅ ESP Health Injected")
                
                val dollarSign = "$"
                val command = "su -c \"PID=${dollarSign}(pidof $FF_PACKAGE); if [ ! -z ${dollarSign}PID ]; then echo '${dollarSign}PID:esp_health:1' > /data/local/tmp/game_hooks; fi\""
                rootUtils.executeSuperUserCommand(command)
                
            } catch (e: Exception) {
                Log.e(TAG, "ESP Health Error: ${e.message}")
            }
        }.start()
    }
    
    fun injectHeadshotOnly() {
        Thread {
            try {
                Log.d(TAG, "✅ Headshot Only Mode Injected")
                
                val dollarSign = "$"
                val command = "su -c \"PID=${dollarSign}(pidof $FF_PACKAGE); if [ ! -z ${dollarSign}PID ]; then echo '${dollarSign}PID:headshot:1' > /data/local/tmp/game_hooks; fi\""
                rootUtils.executeSuperUserCommand(command)
                
            } catch (e: Exception) {
                Log.e(TAG, "Headshot Mode Error: ${e.message}")
            }
        }.start()
    }
    
    fun setFOV(fov: Int = 90) {
        Thread {
            try {
                Log.d(TAG, "✅ FOV Set to: $fov°")
                
                val dollarSign = "$"
                val command = "su -c \"PID=${dollarSign}(pidof $FF_PACKAGE); if [ ! -z ${dollarSign}PID ]; then echo '${dollarSign}PID:fov:$fov' > /data/local/tmp/game_hooks; fi\""
                rootUtils.executeSuperUserCommand(command)
                
            } catch (e: Exception) {
                Log.e(TAG, "FOV Error: ${e.message}")
            }
        }.start()
    }
    
    fun killESPBox() {
        Thread {
            espBoxEnabled = false
            Log.d(TAG, "❌ ESP Box Disabled")
            
            val dollarSign = "$"
            val command = "su -c \"PID=${dollarSign}(pidof $FF_PACKAGE); if [ ! -z ${dollarSign}PID ]; then echo '${dollarSign}PID:esp_box:0' > /data/local/tmp/game_hooks; fi\""
            rootUtils.executeSuperUserCommand(command)
        }.start()
    }
    
    fun killESPLine() {
        Thread {
            espLineEnabled = false
            Log.d(TAG, "❌ ESP Line Disabled")
            
            val dollarSign = "$"
            val command = "su -c \"PID=${dollarSign}(pidof $FF_PACKAGE); if [ ! -z ${dollarSign}PID ]; then echo '${dollarSign}PID:esp_line:0' > /data/local/tmp/game_hooks; fi\""
            rootUtils.executeSuperUserCommand(command)
        }.start()
    }
    
    fun killAimBot() {
        Thread {
            aimBotEnabled = false
            Log.d(TAG, "❌ Aim Bot Disabled")
            
            val dollarSign = "$"
            val command = "su -c \"PID=${dollarSign}(pidof $FF_PACKAGE); if [ ! -z ${dollarSign}PID ]; then echo '${dollarSign}PID:aimbot:0' > /data/local/tmp/game_hooks; fi\""
            rootUtils.executeSuperUserCommand(command)
        }.start()
    }
    
    fun disableHeadshotOnly() {
        Thread {
            Log.d(TAG, "❌ Headshot Only Mode Disabled")
            
            val dollarSign = "$"
            val command = "su -c \"PID=${dollarSign}(pidof $FF_PACKAGE); if [ ! -z ${dollarSign}PID ]; then echo '${dollarSign}PID:headshot:0' > /data/local/tmp/game_hooks; fi\""
            rootUtils.executeSuperUserCommand(command)
        }.start()
    }
    
    fun setESPDistance(distance: Int = 500) {
        Thread {
            Log.d(TAG, "✅ ESP Distance Set to: $distance")
            
            val dollarSign = "$"
            val command = "su -c \"PID=${dollarSign}(pidof $FF_PACKAGE); if [ ! -z ${dollarSign}PID ]; then echo '${dollarSign}PID:esp_distance:$distance' > /data/local/tmp/game_hooks; fi\""
            rootUtils.executeSuperUserCommand(command)
        }.start()
    }
}
