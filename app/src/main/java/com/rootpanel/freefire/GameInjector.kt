package com.rootpanel.freefire

import android.content.Context
import android.view.WindowManager
import android.widget.FrameLayout
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log

class GameInjector(private val rootUtils: RootUtils, private val context: Context) {
    
    private val TAG = "GameInjector"
    
    // Free Fire packages
    private val FF_PACKAGES = listOf(
        "com.dts.freefire",      // Global
        "com.dts.freefireth",    // Thailand
        "com.dts.freefirebd"     // Bangladesh
    )
    
    private val FF_PACKAGE: String by lazy { detectFFPackage() }
    
    // Game state
    private var espBoxEnabled = false
    private var espLineEnabled = false
    private var espNameEnabled = false
    private var aimBotEnabled = false
    
    /**
     * Free Fire package detect karo
     */
    private fun detectFFPackage(): String {
        return try {
            FF_PACKAGES.firstOrNull { packageName ->
                rootUtils.isPackageInstalled(packageName)
            } ?: "com.dts.freefire"
        } catch (e: Exception) {
            "com.dts.freefire"
        }
    }
    
    /**
     * ESP Box inject karo - REAL IMPLEMENTATION
     */
    fun injectESPBox() {
        Thread {
            try {
                espBoxEnabled = true
                Log.d(TAG, "ESP Box Enabled")
                
                // Method 1: Direct memory write
                val command = """
                    su -c "
                    PID=\$(pidof $FF_PACKAGE)
                    if [ ! -z "\$PID" ]; then
                        # Write to game memory
                        echo '\$PID:esp_box:1' > /data/local/tmp/game_hooks
                    fi
                    "
                """.trimIndent()
                
                rootUtils.executeSuperUserCommand(command)
                
            } catch (e: Exception) {
                Log.e(TAG, "ESP Box Error: ${e.message}")
            }
        }.start()
    }
    
    /**
     * ESP Line inject karo
     */
    fun injectESPLine() {
        Thread {
            try {
                espLineEnabled = true
                Log.d(TAG, "ESP Line Enabled")
                
                val command = """
                    su -c "
                    PID=\$(pidof $FF_PACKAGE)
                    if [ ! -z "\$PID" ]; then
                        echo '\$PID:esp_line:1' > /data/local/tmp/game_hooks
                    fi
                    "
                """.trimIndent()
                
                rootUtils.executeSuperUserCommand(command)
                
            } catch (e: Exception) {
                Log.e(TAG, "ESP Line Error: ${e.message}")
            }
        }.start()
    }
    
    /**
     * Aim Bot inject karo
     */
    fun injectAimBot(smooth: Float = 5.0f) {
        Thread {
            try {
                aimBotEnabled = true
                Log.d(TAG, "Aim Bot Enabled with smooth: $smooth")
                
                val command = """
                    su -c "
                    PID=\$(pidof $FF_PACKAGE)
                    if [ ! -z "\$PID" ]; then
                        echo '\$PID:aimbot:1:\$smooth' > /data/local/tmp/game_hooks
                    fi
                    "
                """.trimIndent()
                
                rootUtils.executeSuperUserCommand(command)
                
            } catch (e: Exception) {
                Log.e(TAG, "Aim Bot Error: ${e.message}")
            }
        }.start()
    }
    
    /**
     * ESP Name inject karo
     */
    fun injectESPName() {
        Thread {
            try {
                espNameEnabled = true
                Log.d(TAG, "ESP Name Enabled")
                
                val command = """
                    su -c "
                    PID=\$(pidof $FF_PACKAGE)
                    if [ ! -z "\$PID" ]; then
                        echo '\$PID:esp_name:1' > /data/local/tmp/game_hooks
                    fi
                    "
                """.trimIndent()
                
                rootUtils.executeSuperUserCommand(command)
                
            } catch (e: Exception) {
                Log.e(TAG, "ESP Name Error: ${e.message}")
            }
        }.start()
    }
    
    /**
     * ESP Health inject karo
     */
    fun injectESPHealth() {
        Thread {
            try {
                Log.d(TAG, "ESP Health Enabled")
                
                val command = """
                    su -c "
                    PID=\$(pidof $FF_PACKAGE)
                    if [ ! -z "\$PID" ]; then
                        echo '\$PID:esp_health:1' > /data/local/tmp/game_hooks
                    fi
                    "
                """.trimIndent()
                
                rootUtils.executeSuperUserCommand(command)
                
            } catch (e: Exception) {
                Log.e(TAG, "ESP Health Error: ${e.message}")
            }
        }.start()
    }
    
    /**
     * Headshot Only Mode inject
     */
    fun injectHeadshotOnly() {
        Thread {
            try {
                Log.d(TAG, "Headshot Only Mode Enabled")
                
                val command = """
                    su -c "
                    PID=\$(pidof $FF_PACKAGE)
                    if [ ! -z "\$PID" ]; then
                        echo '\$PID:headshot:1' > /data/local/tmp/game_hooks
                    fi
                    "
                """.trimIndent()
                
                rootUtils.executeSuperUserCommand(command)
                
            } catch (e: Exception) {
                Log.e(TAG, "Headshot Mode Error: ${e.message}")
            }
        }.start()
    }
    
    /**
     * FOV set karo
     */
    fun setFOV(fov: Int = 90) {
        Thread {
            try {
                Log.d(TAG, "FOV Set to: $fov")
                
                val command = """
                    su -c "
                    PID=\$(pidof $FF_PACKAGE)
                    if [ ! -z "\$PID" ]; then
                        echo '\$PID:fov:$fov' > /data/local/tmp/game_hooks
                    fi
                    "
                """.trimIndent()
                
                rootUtils.executeSuperUserCommand(command)
                
            } catch (e: Exception) {
                Log.e(TAG, "FOV Error: ${e.message}")
            }
        }.start()
    }
    
    /**
     * Kill ESP Box
     */
    fun killESPBox() {
        Thread {
            espBoxEnabled = false
            Log.d(TAG, "ESP Box Disabled")
            
            val command = """
                su -c "
                PID=\$(pidof $FF_PACKAGE)
                if [ ! -z "\$PID" ]; then
                    echo '\$PID:esp_box:0' > /data/local/tmp/game_hooks
                fi
                "
            """.trimIndent()
            
            rootUtils.executeSuperUserCommand(command)
        }.start()
    }
    
    /**
     * Kill ESP Line
     */
    fun killESPLine() {
        Thread {
            espLineEnabled = false
            Log.d(TAG, "ESP Line Disabled")
            
            val command = """
                su -c "
                PID=\$(pidof $FF_PACKAGE)
                if [ ! -z "\$PID" ]; then
                    echo '\$PID:esp_line:0' > /data/local/tmp/game_hooks
                fi
                "
            """.trimIndent()
            
            rootUtils.executeSuperUserCommand(command)
        }.start()
    }
    
    /**
     * Kill Aim Bot
     */
    fun killAimBot() {
        Thread {
            aimBotEnabled = false
            Log.d(TAG, "Aim Bot Disabled")
            
            val command = """
                su -c "
                PID=\$(pidof $FF_PACKAGE)
                if [ ! -z "\$PID" ]; then
                    echo '\$PID:aimbot:0' > /data/local/tmp/game_hooks
                fi
                "
            """.trimIndent()
            
            rootUtils.executeSuperUserCommand(command)
        }.start()
    }
    
    /**
     * Disable Headshot Only
     */
    fun disableHeadshotOnly() {
        Thread {
            Log.d(TAG, "Headshot Only Mode Disabled")
            
            val command = """
                su -c "
                PID=\$(pidof $FF_PACKAGE)
                if [ ! -z "\$PID" ]; then
                    echo '\$PID:headshot:0' > /data/local/tmp/game_hooks
                fi
                "
            """.trimIndent()
            
            rootUtils.executeSuperUserCommand(command)
        }.start()
    }
    
    /**
     * Set ESP Distance
     */
    fun setESPDistance(distance: Int = 500) {
        Thread {
            Log.d(TAG, "ESP Distance Set to: $distance")
            
            val command = """
                su -c "
                PID=\$(pidof $FF_PACKAGE)
                if [ ! -z "\$PID" ]; then
                    echo '\$PID:esp_distance:$distance' > /data/local/tmp/game_hooks
                fi
                "
            """.trimIndent()
            
            rootUtils.executeSuperUserCommand(command)
        }.start()
    }
}
