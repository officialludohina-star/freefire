package com.rootpanel.freefire

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    
    private val rootUtils = RootUtils()
    private lateinit var gameInjector: GameInjector
    private var isRootGranted = false
    private var modMenuVisible = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
            
            gameInjector = GameInjector(rootUtils, this)
            
            // Request overlay permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    startActivity(intent)
                }
            }
            
            // Root permission request karo
            requestRootPermission()
            
            // Start Floating Overlay Service
            val overlayIntent = Intent(this, FloatingOverlayService::class.java)
            startService(overlayIntent)
            
            // UI Elements
            val iconX = findViewById<ImageButton>(R.id.iconX)
            val modMenuContainer = findViewById<LinearLayout>(R.id.modMenuContainer)
            val freeFireCard = findViewById<FrameLayout>(R.id.freeFireCard)
            val launchFFBtn = findViewById<Button>(R.id.launchFFBtn)
            
            // X icon - toggle mod menu
            iconX.setOnClickListener {
                if (isRootGranted) {
                    modMenuVisible = !modMenuVisible
                    modMenuContainer.visibility = if (modMenuVisible) View.VISIBLE else View.GONE
                } else {
                    Toast.makeText(this, "❌ Root Permission Required!", Toast.LENGTH_SHORT).show()
                }
            }
            
            // Free Fire Card Click
            freeFireCard.setOnClickListener {
                if (isRootGranted) {
                    openModMenu()
                } else {
                    Toast.makeText(this, "❌ Root Permission Required!", Toast.LENGTH_SHORT).show()
                }
            }
            
            // Launch Button
            launchFFBtn.setOnClickListener {
                if (isRootGranted) {
                    openModMenu()
                } else {
                    Toast.makeText(this, "❌ Root Permission Required!", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "❌ Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    /**
     * Root permission request karo
     */
    private fun requestRootPermission() {
        Thread {
            val granted = rootUtils.testRootAccess()
            
            runOnUiThread {
                if (granted) {
                    isRootGranted = true
                    enableAllButtons()
                    showRootDialog("✓ Root Granted", "All features ready to inject!", true)
                } else {
                    isRootGranted = false
                    disableAllButtons()
                    showRootDialog("❌ Root Denied", "Root access required!", false)
                }
            }
        }.start()
    }
    
    /**
     * Sab buttons enable karo
     */
    private fun enableAllButtons() {
        try {
            findViewById<CheckBox>(R.id.aimAssistCheckbox).isEnabled = true
            findViewById<CheckBox>(R.id.silentAimCheckbox).isEnabled = true
            findViewById<CheckBox>(R.id.autoAimCheckbox).isEnabled = true
            findViewById<CheckBox>(R.id.espBoxCheckbox).isEnabled = true
            findViewById<CheckBox>(R.id.espLineCheckbox).isEnabled = true
            findViewById<CheckBox>(R.id.espNameCheckbox).isEnabled = true
            findViewById<CheckBox>(R.id.espHealthCheckbox).isEnabled = true
            findViewById<CheckBox>(R.id.espDistanceCheckbox).isEnabled = true
            findViewById<CheckBox>(R.id.headshotOnlyCheckbox).isEnabled = true
            findViewById<SeekBar>(R.id.smoothSlider).isEnabled = true
            findViewById<SeekBar>(R.id.fovSlider).isEnabled = true
            
            addCheckBoxListeners()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Sab buttons disable karo
     */
    private fun disableAllButtons() {
        try {
            findViewById<CheckBox>(R.id.aimAssistCheckbox).isEnabled = false
            findViewById<CheckBox>(R.id.silentAimCheckbox).isEnabled = false
            findViewById<CheckBox>(R.id.autoAimCheckbox).isEnabled = false
            findViewById<CheckBox>(R.id.espBoxCheckbox).isEnabled = false
            findViewById<CheckBox>(R.id.espLineCheckbox).isEnabled = false
            findViewById<CheckBox>(R.id.espNameCheckbox).isEnabled = false
            findViewById<CheckBox>(R.id.espHealthCheckbox).isEnabled = false
            findViewById<CheckBox>(R.id.espDistanceCheckbox).isEnabled = false
            findViewById<CheckBox>(R.id.headshotOnlyCheckbox).isEnabled = false
            findViewById<SeekBar>(R.id.smoothSlider).isEnabled = false
            findViewById<SeekBar>(R.id.fovSlider).isEnabled = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Checkbox listeners add karo
     */
    private fun addCheckBoxListeners() {
        // Aim Assist
        findViewById<CheckBox>(R.id.aimAssistCheckbox).setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                gameInjector.injectAimBot()
                Toast.makeText(this, "✅ Aim Bot Injected!", Toast.LENGTH_SHORT).show()
            } else {
                gameInjector.killAimBot()
                Toast.makeText(this, "❌ Aim Bot Disabled", Toast.LENGTH_SHORT).show()
            }
        }
        
        // Silent Aim - not implemented, disabled
        findViewById<CheckBox>(R.id.silentAimCheckbox).isEnabled = false
        
        // Auto Aim
        findViewById<CheckBox>(R.id.autoAimCheckbox).setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                gameInjector.injectAimBot(5.0f)
                Toast.makeText(this, "✅ Auto Aim Injected!", Toast.LENGTH_SHORT).show()
            } else {
                gameInjector.killAimBot()
                Toast.makeText(this, "❌ Auto Aim Disabled", Toast.LENGTH_SHORT).show()
            }
        }
        
        // ESP Box
        findViewById<CheckBox>(R.id.espBoxCheckbox).setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                gameInjector.injectESPBox()
                Toast.makeText(this, "✅ ESP Box Injected!", Toast.LENGTH_SHORT).show()
            } else {
                gameInjector.killESPBox()
                Toast.makeText(this, "❌ ESP Box Disabled", Toast.LENGTH_SHORT).show()
            }
        }
        
        // ESP Line
        findViewById<CheckBox>(R.id.espLineCheckbox).setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                gameInjector.injectESPLine()
                Toast.makeText(this, "✅ ESP Line Injected!", Toast.LENGTH_SHORT).show()
            } else {
                gameInjector.killESPLine()
                Toast.makeText(this, "❌ ESP Line Disabled", Toast.LENGTH_SHORT).show()
            }
        }
        
        // ESP Name (FIXED - Ab sahi method call)
        findViewById<CheckBox>(R.id.espNameCheckbox).setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                gameInjector.injectESPName()  // ✅ CORRECT
                Toast.makeText(this, "✅ ESP Name Injected!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "❌ ESP Name Disabled", Toast.LENGTH_SHORT).show()
            }
        }
        
        // ESP Health (FIXED - Ab sahi method call)
        findViewById<CheckBox>(R.id.espHealthCheckbox).setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                gameInjector.injectESPHealth()  // ✅ CORRECT
                Toast.makeText(this, "✅ ESP Health Injected!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "❌ ESP Health Disabled", Toast.LENGTH_SHORT).show()
            }
        }
        
        // ESP Distance
        findViewById<CheckBox>(R.id.espDistanceCheckbox).setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                gameInjector.setESPDistance(500)
                Toast.makeText(this, "✅ ESP Distance Injected!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "❌ ESP Distance Disabled", Toast.LENGTH_SHORT).show()
            }
        }
        
        // Headshot Only Mode
        findViewById<CheckBox>(R.id.headshotOnlyCheckbox).setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                gameInjector.injectHeadshotOnly()
                Toast.makeText(this, "🎯 Headshot Only Mode Activated!", Toast.LENGTH_SHORT).show()
            } else {
                gameInjector.disableHeadshotOnly()
                Toast.makeText(this, "❌ Headshot Mode Disabled", Toast.LENGTH_SHORT).show()
            }
        }
        
        // Smooth Slider
        findViewById<SeekBar>(R.id.smoothSlider).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val smoothValue = (progress / 10.0f)
                gameInjector.injectAimBot(smoothValue)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        // FOV Slider
        findViewById<SeekBar>(R.id.fovSlider).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                gameInjector.setFOV(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
    
    /**
     * Root dialog show karo
     */
    private fun showRootDialog(title: String, message: String, isGranted: Boolean) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .setCancelable(false)
            .show()
    }
    
    /**
     * Mod menu open karo
     */
    private fun openModMenu() {
        val modMenuContainer = findViewById<LinearLayout>(R.id.modMenuContainer)
        val launcherSection = findViewById<FrameLayout>(R.id.freeFireCard)
        
        launcherSection.visibility = View.GONE
        modMenuContainer.visibility = View.VISIBLE
        modMenuVisible = true
        
        Toast.makeText(this, "🎯 Mod Menu Loaded...", Toast.LENGTH_SHORT).show()
    }
}
