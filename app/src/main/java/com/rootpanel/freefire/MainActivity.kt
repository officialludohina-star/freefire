package com.rootpanel.freefire

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    
    private val TAG = "FreeFire_Debug"
    
    private lateinit var rootUtils: RootUtils
    private lateinit var gameInjector: GameInjector
    private var isRootGranted = false
    private var modMenuVisible = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            Log.d(TAG, "=== MainActivity.onCreate() STARTED ===")
            
            super.onCreate(savedInstanceState)
            
            Log.d(TAG, "Setting content view...")
            setContentView(R.layout.activity_main)
            
            Log.d(TAG, "Initializing RootUtils...")
            rootUtils = RootUtils()
            
            Log.d(TAG, "Initializing GameInjector...")
            gameInjector = GameInjector(rootUtils)
            
            // Request root permission in background
            Log.d(TAG, "Starting root permission request...")
            requestRootPermission()
            
            // Find UI Elements
            Log.d(TAG, "Finding UI elements...")
            val iconX = findViewById<ImageButton>(R.id.iconX)
            val modMenuContainer = findViewById<LinearLayout>(R.id.modMenuContainer)
            val freeFireCard = findViewById<LinearLayout>(R.id.freeFireCard)
            val launchFFBtn = findViewById<Button>(R.id.launchFFBtn)
            
            Log.d(TAG, "Setting up click listeners...")
            
            // X icon - toggle mod menu
            iconX.setOnClickListener {
                Log.d(TAG, "Close button clicked")
                if (isRootGranted) {
                    modMenuVisible = !modMenuVisible
                    modMenuContainer.visibility = if (modMenuVisible) View.VISIBLE else View.GONE
                    Log.d(TAG, "Mod menu visibility toggled: $modMenuVisible")
                } else {
                    Toast.makeText(this, "❌ Root Permission Required!", Toast.LENGTH_SHORT).show()
                }
            }
            
            // Free Fire Card Click
            freeFireCard.setOnClickListener {
                Log.d(TAG, "Free Fire card clicked")
                if (isRootGranted) {
                    openModMenu()
                } else {
                    Toast.makeText(this, "❌ Root Permission Required!", Toast.LENGTH_SHORT).show()
                }
            }
            
            // Launch Button
            launchFFBtn.setOnClickListener {
                Log.d(TAG, "Launch button clicked")
                if (isRootGranted) {
                    openModMenu()
                } else {
                    Toast.makeText(this, "❌ Root Permission Required!", Toast.LENGTH_SHORT).show()
                }
            }
            
            Log.d(TAG, "=== MainActivity.onCreate() COMPLETED ===")
            
        } catch (e: Exception) {
            Log.e(TAG, "=== CRITICAL ERROR in onCreate ===", e)
            e.printStackTrace()
            Toast.makeText(this, "❌ Startup Error: ${e.message}", Toast.LENGTH_LONG).show()
            
            // Try to at least show something
            try {
                disableAllButtons()
            } catch (ex: Exception) {
                Log.e(TAG, "Failed to disable buttons: ${ex.message}")
            }
        }
    }
    
    /**
     * Root permission request karo (runs in background thread)
     * FIXED: Added proper error handling and timeout
     */
    private fun requestRootPermission() {
        Thread(Runnable {
            try {
                Log.d(TAG, "Root test thread started...")
                val granted = rootUtils.testRootAccess()
                Log.d(TAG, "Root test result: $granted")
                
                runOnUiThread {
                    try {
                        Log.d(TAG, "Updating UI with root result...")
                        if (granted) {
                            isRootGranted = true
                            enableAllButtons()
                            showRootDialog("✓ Root Granted", "All features ready to inject!", true)
                            Log.d(TAG, "Root granted - UI updated")
                        } else {
                            isRootGranted = false
                            disableAllButtons()
                            showRootDialog("❌ Root Denied", "Root access required!", false)
                            Log.d(TAG, "Root denied - buttons disabled")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error updating UI after root check: ${e.message}", e)
                        Toast.makeText(this@MainActivity, "UI Update Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Root permission check FAILED: ${e.message}", e)
                runOnUiThread {
                    try {
                        isRootGranted = false
                        disableAllButtons()
                        Toast.makeText(this@MainActivity, "Root Check Error: ${e.message}", Toast.LENGTH_LONG).show()
                        showRootDialog("❌ Error", "Failed to check root: ${e.message}", false)
                    } catch (ex: Exception) {
                        Log.e(TAG, "Failed to show error dialog: ${ex.message}")
                    }
                }
            }
        }).start()
    }
    
    /**
     * Sab buttons enable karo
     * FIXED: Added comprehensive error handling
     */
    private fun enableAllButtons() {
        try {
            Log.d(TAG, "Enabling all buttons...")
            val checkboxIds = listOf(
                R.id.aimAssistCheckbox,
                R.id.silentAimCheckbox,
                R.id.autoAimCheckbox,
                R.id.espBoxCheckbox,
                R.id.espLineCheckbox,
                R.id.espNameCheckbox,
                R.id.espHealthCheckbox,
                R.id.espDistanceCheckbox,
                R.id.headshotOnlyCheckbox
            )
            
            for (id in checkboxIds) {
                try {
                    findViewById<CheckBox>(id).isEnabled = true
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to enable checkbox $id: ${e.message}")
                }
            }
            
            try {
                findViewById<SeekBar>(R.id.smoothSlider).isEnabled = true
                findViewById<SeekBar>(R.id.fovSlider).isEnabled = true
            } catch (e: Exception) {
                Log.w(TAG, "Failed to enable seekbars: ${e.message}")
            }
            
            addCheckBoxListeners()
            Log.d(TAG, "All buttons enabled")
        } catch (e: Exception) {
            Log.e(TAG, "Error enabling buttons: ${e.message}", e)
        }
    }
    
    /**
     * Sab buttons disable karo
     * FIXED: Added comprehensive error handling
     */
    private fun disableAllButtons() {
        try {
            Log.d(TAG, "Disabling all buttons...")
            val checkboxIds = listOf(
                R.id.aimAssistCheckbox,
                R.id.silentAimCheckbox,
                R.id.autoAimCheckbox,
                R.id.espBoxCheckbox,
                R.id.espLineCheckbox,
                R.id.espNameCheckbox,
                R.id.espHealthCheckbox,
                R.id.espDistanceCheckbox,
                R.id.headshotOnlyCheckbox
            )
            
            for (id in checkboxIds) {
                try {
                    findViewById<CheckBox>(id).isEnabled = false
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to disable checkbox $id: ${e.message}")
                }
            }
            
            try {
                findViewById<SeekBar>(R.id.smoothSlider).isEnabled = false
                findViewById<SeekBar>(R.id.fovSlider).isEnabled = false
            } catch (e: Exception) {
                Log.w(TAG, "Failed to disable seekbars: ${e.message}")
            }
            
            Log.d(TAG, "All buttons disabled")
        } catch (e: Exception) {
            Log.e(TAG, "Error disabling buttons: ${e.message}", e)
        }
    }
    
    /**
     * Checkbox listeners add karo
     * FIXED: Added error handling for each checkbox
     */
    private fun addCheckBoxListeners() {
        try {
            Log.d(TAG, "Adding checkbox listeners...")
            
            // Aim Assist
            try {
                findViewById<CheckBox>(R.id.aimAssistCheckbox).setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        gameInjector.injectAimBot()
                        Toast.makeText(this, "✅ Aim Bot Injected!", Toast.LENGTH_SHORT).show()
                    } else {
                        gameInjector.killAimBot()
                        Toast.makeText(this, "❌ Aim Bot Disabled", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error setting up Aim Assist listener: ${e.message}")
            }
            
            // Silent Aim
            try {
                findViewById<CheckBox>(R.id.silentAimCheckbox).setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        gameInjector.injectSilentAim()
                        Toast.makeText(this, "✅ Silent Aim Injected!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "❌ Silent Aim Disabled", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error setting up Silent Aim listener: ${e.message}")
            }
            
            // Auto Aim
            try {
                findViewById<CheckBox>(R.id.autoAimCheckbox).setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        gameInjector.injectAimBot(5.0f)
                        Toast.makeText(this, "✅ Auto Aim Injected!", Toast.LENGTH_SHORT).show()
                    } else {
                        gameInjector.killAimBot()
                        Toast.makeText(this, "❌ Auto Aim Disabled", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error setting up Auto Aim listener: ${e.message}")
            }
            
            // ESP Checkboxes
            try {
                findViewById<CheckBox>(R.id.espBoxCheckbox).setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        gameInjector.injectESPBox()
                        Toast.makeText(this, "✅ ESP Box Injected!", Toast.LENGTH_SHORT).show()
                    } else {
                        gameInjector.killESPBox()
                        Toast.makeText(this, "❌ ESP Box Disabled", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error setting up ESP Box listener: ${e.message}")
            }
            
            try {
                findViewById<CheckBox>(R.id.espLineCheckbox).setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        gameInjector.injectESPLine()
                        Toast.makeText(this, "✅ ESP Line Injected!", Toast.LENGTH_SHORT).show()
                    } else {
                        gameInjector.killESPLine()
                        Toast.makeText(this, "❌ ESP Line Disabled", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error setting up ESP Line listener: ${e.message}")
            }
            
            try {
                findViewById<CheckBox>(R.id.espNameCheckbox).setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        gameInjector.injectESPName()
                        Toast.makeText(this, "✅ ESP Name Injected!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "❌ ESP Name Disabled", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error setting up ESP Name listener: ${e.message}")
            }
            
            try {
                findViewById<CheckBox>(R.id.espHealthCheckbox).setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        gameInjector.injectESPHealth()
                        Toast.makeText(this, "✅ ESP Health Injected!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "❌ ESP Health Disabled", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error setting up ESP Health listener: ${e.message}")
            }
            
            try {
                findViewById<CheckBox>(R.id.espDistanceCheckbox).setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        gameInjector.setESPDistance(500)
                        Toast.makeText(this, "✅ ESP Distance Injected!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "❌ ESP Distance Disabled", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error setting up ESP Distance listener: ${e.message}")
            }
            
            try {
                findViewById<CheckBox>(R.id.headshotOnlyCheckbox).setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        gameInjector.injectHeadshotOnly()
                        Toast.makeText(this, "🎯 Headshot Only Mode Activated!", Toast.LENGTH_SHORT).show()
                    } else {
                        gameInjector.disableHeadshotOnly()
                        Toast.makeText(this, "❌ Headshot Mode Disabled", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error setting up Headshot listener: ${e.message}")
            }
            
            // Sliders
            try {
                findViewById<SeekBar>(R.id.smoothSlider).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        if (fromUser) {
                            val smoothValue = (progress / 10.0f)
                            gameInjector.injectAimBot(smoothValue)
                        }
                    }
                    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                })
            } catch (e: Exception) {
                Log.e(TAG, "Error setting up Smooth Slider: ${e.message}")
            }
            
            try {
                findViewById<SeekBar>(R.id.fovSlider).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        if (fromUser) {
                            gameInjector.setFOV(progress)
                        }
                    }
                    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                })
            } catch (e: Exception) {
                Log.e(TAG, "Error setting up FOV Slider: ${e.message}")
            }
            
            Log.d(TAG, "Checkbox listeners added successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error adding checkbox listeners: ${e.message}", e)
        }
    }
    
    /**
     * Root dialog show karo
     */
    private fun showRootDialog(title: String, message: String, isGranted: Boolean) {
        try {
            AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .setCancelable(false)
                .show()
        } catch (e: Exception) {
            Log.e(TAG, "Error showing dialog: ${e.message}")
        }
    }
    
    /**
     * Mod menu open karo
     */
    private fun openModMenu() {
        try {
            Log.d(TAG, "Opening mod menu...")
            val modMenuContainer = findViewById<LinearLayout>(R.id.modMenuContainer)
            val launcherSection = findViewById<LinearLayout>(R.id.freeFireCard)
            
            launcherSection.visibility = View.GONE
            modMenuContainer.visibility = View.VISIBLE
            modMenuVisible = true
            
            Toast.makeText(this, "🎯 Mod Menu Loaded...", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Mod menu opened")
        } catch (e: Exception) {
            Log.e(TAG, "Error opening mod menu: ${e.message}", e)
        }
    }
}
