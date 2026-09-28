package com.rootpanel.freefire

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.*
import androidx.core.content.ContextCompat

class FloatingOverlayService : Service() {
    
    private val TAG = "FloatingOverlayService"
    private lateinit var gameInjector: GameInjector
    private lateinit var rootUtils: RootUtils
    private var overlayView: FrameLayout? = null
    private var windowManager: WindowManager? = null
    private var isMenuOpen = false
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "FloatingOverlayService Created")
        
        try {
            rootUtils = RootUtils()
            gameInjector = GameInjector(rootUtils, this)
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            
            createFloatingMenu()
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing: ${e.message}")
        }
    }
    
    /**
     * Floating menu banao
     */
    private fun createFloatingMenu() {
        try {
            overlayView = FrameLayout(this).apply {
                setBackgroundColor(ContextCompat.getColor(this@FloatingOverlayService, android.R.color.transparent))
            }
            
            // Main menu layout
            val menuLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(0xFF1a1a1a.toInt())
                alpha = 0.95f
            }
            
            // Title bar
            val titleBar = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setBackgroundColor(0xFF7c3aed.toInt())
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    120
                )
                gravity = Gravity.CENTER_VERTICAL
            }
            
            val title = TextView(this).apply {
                text = "⚡ FreeFire Mod"
                textSize = 20f
                setTextColor(0xFFFFFFFF.toInt())
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
                setPadding(20, 0, 0, 0)
            }
            
            val closeBtn = Button(this).apply {
                text = "✕"
                setTextColor(0xFFFFFFFF.toInt())
                setBackgroundColor(0xFF7c3aed.toInt())
                layoutParams = LinearLayout.LayoutParams(
                    100,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setOnClickListener {
                    isMenuOpen = false
                    updateMenuVisibility()
                }
            }
            
            titleBar.addView(title)
            titleBar.addView(closeBtn)
            menuLayout.addView(titleBar)
            
            // Scrollable content
            val scrollView = ScrollView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }
            
            val contentLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = FrameLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            
            // Add checkboxes for features
            addFeatureCheckbox(contentLayout, "Aim Assist", "aimAssist") { isChecked ->
                if (isChecked) {
                    gameInjector.injectAimBot()
                    showToast("✅ Aim Bot Injected!")
                } else {
                    gameInjector.killAimBot()
                    showToast("❌ Aim Bot Disabled")
                }
            }
            
            addFeatureCheckbox(contentLayout, "Auto Aim", "autoAim") { isChecked ->
                if (isChecked) {
                    gameInjector.injectAimBot()
                    showToast("✅ Auto Aim Injected!")
                } else {
                    gameInjector.killAimBot()
                    showToast("❌ Auto Aim Disabled")
                }
            }
            
            addFeatureCheckbox(contentLayout, "ESP Box", "espBox") { isChecked ->
                if (isChecked) {
                    gameInjector.injectESPBox()
                    showToast("✅ ESP Box Injected!")
                } else {
                    gameInjector.killESPBox()
                    showToast("❌ ESP Box Disabled")
                }
            }
            
            addFeatureCheckbox(contentLayout, "ESP Line", "espLine") { isChecked ->
                if (isChecked) {
                    gameInjector.injectESPLine()
                    showToast("✅ ESP Line Injected!")
                } else {
                    gameInjector.killESPLine()
                    showToast("❌ ESP Line Disabled")
                }
            }
            
            addFeatureCheckbox(contentLayout, "ESP Name", "espName") { isChecked ->
                if (isChecked) {
                    gameInjector.injectESPName()
                    showToast("✅ ESP Name Injected!")
                } else {
                    showToast("❌ ESP Name Disabled")
                }
            }
            
            addFeatureCheckbox(contentLayout, "ESP Health", "espHealth") { isChecked ->
                if (isChecked) {
                    gameInjector.injectESPHealth()
                    showToast("✅ ESP Health Injected!")
                } else {
                    showToast("❌ ESP Health Disabled")
                }
            }
            
            addFeatureCheckbox(contentLayout, "Headshot Only", "headshot") { isChecked ->
                if (isChecked) {
                    gameInjector.injectHeadshotOnly()
                    showToast("✅ Headshot Only Injected!")
                } else {
                    gameInjector.disableHeadshotOnly()
                    showToast("❌ Headshot Only Disabled")
                }
            }
            
            // Smooth Value Slider
            addSlider(contentLayout, "Smooth Value", 1f, 20f, 5f) { value ->
                gameInjector.injectAimBot(value)
            }
            
            // FOV Slider
            addSlider(contentLayout, "FOV", 60f, 120f, 90f) { value ->
                gameInjector.setFOV(value.toInt())
            }
            
            scrollView.addView(contentLayout)
            menuLayout.addView(scrollView)
            
            // Toggle button (small, always visible)
            val toggleBtn = Button(this).apply {
                text = "📱"
                setBackgroundColor(0xFF7c3aed.toInt())
                setTextColor(0xFFFFFFFF.toInt())
                layoutParams = FrameLayout.LayoutParams(
                    150,
                    150,
                    Gravity.BOTTOM or Gravity.START
                ).apply {
                    bottomMargin = 30
                    leftMargin = 30
                }
                setOnClickListener {
                    isMenuOpen = !isMenuOpen
                    updateMenuVisibility()
                }
            }
            
            overlayView?.addView(toggleBtn)
            overlayView?.addView(menuLayout)
            
            // Store menuLayout for visibility control
            overlayView?.tag = menuLayout
            
            // Add to window
            val params = WindowManager.LayoutParams().apply {
                type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    WindowManager.LayoutParams.TYPE_PHONE
                }
                format = android.graphics.PixelFormat.TRANSPARENT
                flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.MATCH_PARENT
            }
            
            windowManager?.addView(overlayView, params)
            Log.d(TAG, "Floating menu created successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error creating floating menu: ${e.message}")
            e.printStackTrace()
        }
    }
    
    /**
     * Add feature checkbox
     */
    private fun addFeatureCheckbox(
        parent: LinearLayout,
        label: String,
        tag: String,
        onCheckedChange: (Boolean) -> Unit
    ) {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                100
            )
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundColor(0xFF2a2a2a.toInt())
            setPadding(20, 10, 20, 10)
        }
        
        val checkbox = CheckBox(this).apply {
            isChecked = false
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            this.tag = tag
            setOnCheckedChangeListener { _, isChecked ->
                onCheckedChange(isChecked)
            }
        }
        
        val labelView = TextView(this).apply {
            text = label
            textSize = 16f
            setTextColor(0xFFFFFFFF.toInt())
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            setPadding(20, 0, 0, 0)
        }
        
        container.addView(checkbox)
        container.addView(labelView)
        parent.addView(container)
    }
    
    /**
     * Add slider control
     */
    private fun addSlider(
        parent: LinearLayout,
        label: String,
        min: Float,
        max: Float,
        default: Float,
        onValueChange: (Float) -> Unit
    ) {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setBackgroundColor(0xFF2a2a2a.toInt())
            setPadding(20, 15, 20, 15)
        }
        
        val labelView = TextView(this).apply {
            text = "$label: ${default.toInt()}"
            textSize = 14f
            setTextColor(0xFFFFFFFF.toInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        
        val slider = SeekBar(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            max = (max - min).toInt()
            progress = (default - min).toInt()
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    val value = min + progress
                    labelView.text = "$label: ${value.toInt()}"
                    onValueChange(value)
                }
                
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
        
        container.addView(labelView)
        container.addView(slider)
        parent.addView(container)
    }
    
    /**
     * Toggle menu visibility
     */
    private fun updateMenuVisibility() {
        try {
            val menuLayout = overlayView?.tag as? LinearLayout
            menuLayout?.visibility = if (isMenuOpen) android.view.View.VISIBLE else android.view.View.GONE
        } catch (e: Exception) {
            Log.e(TAG, "Error updating visibility: ${e.message}")
        }
    }
    
    /**
     * Show toast
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
        
        if (overlayView != null) {
            try {
                windowManager?.removeView(overlayView)
            } catch (e: Exception) {
                Log.e(TAG, "Error removing overlay: ${e.message}")
            }
        }
    }
}
