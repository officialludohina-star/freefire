package com.rootpanel.freefire

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    
    private val rootUtils = RootUtils()
    private lateinit var gameInjector: GameInjector
    private lateinit var gameDetector: GameDetector
    private var isRootGranted = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
            
            gameInjector = GameInjector(rootUtils, this)
            gameDetector = GameDetector(this)
            
            requestRootPermission()
            checkOverlayPermission()
            
            val launchFFBtn = findViewById<Button>(R.id.launchFFBtn)
            
            launchFFBtn.setOnClickListener {
                if (!canDrawOverlays()) {
                    Toast.makeText(this, "Grant overlay permission first!", Toast.LENGTH_LONG).show()
                    requestOverlayPermission()
                    return@setOnClickListener
                }
                
                if (isRootGranted) {
                    launchGameWithOverlay()
                } else {
                    Toast.makeText(this, "Root Permission Required!", Toast.LENGTH_SHORT).show()
                }
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun checkOverlayPermission() {
        if (!canDrawOverlays()) {
            Toast.makeText(
                this,
                "⚠️ Tap SETTINGS to enable overlay permission",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
    private fun canDrawOverlays(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(this)
        }
        return true
    }
    
    private fun requestOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivity(intent)
    }
    
    private fun requestRootPermission() {
        Thread {
            val granted = rootUtils.testRootAccess()
            
            runOnUiThread {
                if (granted) {
                    isRootGranted = true
                    Toast.makeText(this, "✅ Root Access Granted!", Toast.LENGTH_SHORT).show()
                    Log.d("MainActivity", "Root granted")
                } else {
                    isRootGranted = false
                    Toast.makeText(this, "❌ Root Access Required!", Toast.LENGTH_SHORT).show()
                    Log.e("MainActivity", "Root denied")
                }
            }
        }.start()
    }
    
    private fun launchGameWithOverlay() {
        Thread {
            try {
                Log.d("GameLaunch", "Detecting Free Fire...")
                
                val ffPackage = gameDetector.detectFreeFirePackage()
                
                if (ffPackage == null) {
                    runOnUiThread {
                        Toast.makeText(
                            this@MainActivity,
                            "Free Fire Not Installed!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    return@Thread
                }
                
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "Found Free Fire!\n🚀 Launching...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                
                launchFFGame(ffPackage)
                
            } catch (e: Exception) {
                Log.e("GameLaunch", "Error: ${e.message}", e)
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }.start()
    }
    
    private fun launchFFGame(packageName: String) {
        try {
            Log.d("GameLaunch", "Launching: $packageName")
            
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            
            if (launchIntent == null) {
                runOnUiThread {
                    Toast.makeText(this, "Cannot launch game!", Toast.LENGTH_LONG).show()
                }
                return
            }
            
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
            
            startActivity(launchIntent)
            Log.d("GameLaunch", "Game launched")
            
            Thread.sleep(1500)
            
            runOnUiThread {
                Log.d("GameLaunch", "Starting overlay...")
                
                val overlayIntent = Intent(this@MainActivity, FloatingOverlayService::class.java)
                overlayIntent.putExtra("game_package", packageName)
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(overlayIntent)
                } else {
                    startService(overlayIntent)
                }
                
                Log.d("GameLaunch", "Overlay started")
                Toast.makeText(
                    this@MainActivity,
                    "Game Running!\nTap HINA for Mod Menu",
                    Toast.LENGTH_LONG
                ).show()
            }
            
        } catch (e: Exception) {
            Log.e("GameLaunch", "Launch error: ${e.message}", e)
            runOnUiThread {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, FloatingOverlayService::class.java))
    }
}
