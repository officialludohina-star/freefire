package com.rootpanel.freefire

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.*
import android.widget.*

class FloatingOverlayService : Service() {
    
    private lateinit var windowManager: WindowManager
    private var floatingView: FrameLayout? = null
    private var modMenuView: LinearLayout? = null
    private var modMenuVisible = false
    private var gamePackage = ""
    
    private lateinit var gameInjector: GameInjector
    private lateinit var rootUtils: RootUtils
    
    // Drag tracking
    private var lastX = 0
    private var lastY = 0
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var isDragging = false
    
    private var floatingParams: WindowManager.LayoutParams? = null
    private var modMenuParams: WindowManager.LayoutParams? = null
    
    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        rootUtils = RootUtils()
        gameInjector = GameInjector(rootUtils)
        Log.d("FloatingOverlay", "✅ Service Created")
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        gamePackage = intent?.getStringExtra("game_package") ?: "com.dts.freefireth"
        Log.d("FloatingOverlay", "📱 Game Package: $gamePackage")
        
        createFloatingButtons()
        return START_STICKY
    }
    
    private fun createFloatingButtons() {
        try {
            Log.d("FloatingOverlay", "🎨 Creating draggable floating buttons...")
            
            // Main floating container
            floatingView = FrameLayout(this).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
            }
            
            // Inner container for buttons
            val buttonContainer = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
            }
            
            // HINA Button (Menu Toggle) - Gold
            val hinaBtn = Button(this).apply {
                text = "🔥 HINA"
                textSize = 12f
                setBackgroundColor(android.graphics.Color.parseColor("#FFD700"))
                setTextColor(android.graphics.Color.BLACK)
                layoutParams = LinearLayout.LayoutParams(90, 70)
                setPadding(5, 5, 5, 5)
                setTypeface(null, android.graphics.Typeface.BOLD)
                
                setOnClickListener {
                    if (!isDragging) {
                        Log.d("FloatingOverlay", "👆 HINA clicked - toggling mod menu")
                        toggleModMenu()
                    }
                }
            }
            
            // LEGEND Button (Close) - Red
            val legendBtn = Button(this).apply {
                text = "✕"
                textSize = 16f
                setBackgroundColor(android.graphics.Color.parseColor("#FF3333"))
                setTextColor(android.graphics.Color.WHITE)
                layoutParams = LinearLayout.LayoutParams(50, 70)
                setPadding(5, 5, 5, 5)
                setTypeface(null, android.graphics.Typeface.BOLD)
                
                setOnClickListener {
                    if (!isDragging) {
                        Log.d("FloatingOverlay", "👆 LEGEND clicked - closing overlay")
                        stopSelf()
                    }
                }
            }
            
            buttonContainer.addView(hinaBtn)
            buttonContainer.addView(legendBtn)
            floatingView?.addView(buttonContainer)
            
            // Window params for floating buttons
            floatingParams = WindowManager.LayoutParams().apply {
                type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else
                    WindowManager.LayoutParams.TYPE_PHONE
                
                format = PixelFormat.TRANSPARENT
                flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                
                width = WindowManager.LayoutParams.WRAP_CONTENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
                x = 10
                y = 100
            }
            
            lastX = floatingParams!!.x
            lastY = floatingParams!!.y
            
            // Touch listener for dragging
            floatingView?.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        isDragging = false
                        lastTouchX = event.rawX
                        lastTouchY = event.rawY
                        true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val deltaX = event.rawX - lastTouchX
                        val deltaY = event.rawY - lastTouchY
                        
                        if (Math.abs(deltaX) > 5 || Math.abs(deltaY) > 5) {
                            isDragging = true
                        }
                        
                        if (isDragging) {
                            lastX = (lastX + deltaX).toInt()
                            lastY = (lastY + deltaY).toInt()
                            
                            floatingParams!!.x = lastX
                            floatingParams!!.y = lastY
                            
                            windowManager.updateViewLayout(floatingView, floatingParams)
                        }
                        
                        lastTouchX = event.rawX
                        lastTouchY = event.rawY
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        isDragging = false
                        true
                    }
                    else -> false
                }
            }
            
            windowManager.addView(floatingView, floatingParams)
            Log.d("FloatingOverlay", "✅ Floating buttons added")
            
        } catch (e: Exception) {
            Log.e("FloatingOverlay", "❌ Error creating floating buttons: ${e.message}", e)
        }
    }
    
    private fun toggleModMenu() {
        try {
            Log.d("FloatingOverlay", "Toggling mod menu... current state: $modMenuVisible")
            
            if (modMenuView == null) {
                Log.d("FloatingOverlay", "Creating mod menu for first time...")
                createModMenu()
            }
            
            modMenuVisible = !modMenuVisible
            Log.d("FloatingOverlay", "Setting visibility to: ${if (modMenuVisible) "VISIBLE" else "GONE"}")
            
            modMenuView?.visibility = if (modMenuVisible) View.VISIBLE else View.GONE
            
            if (modMenuVisible) {
                Toast.makeText(this, "✅ Mod Menu Opened!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "❌ Mod Menu Closed", Toast.LENGTH_SHORT).show()
            }
            
            Log.d("FloatingOverlay", "✅ Mod menu ${if (modMenuVisible) "OPENED" else "CLOSED"}")
            
        } catch (e: Exception) {
            Log.e("FloatingOverlay", "❌ Error toggling mod menu: ${e.message}", e)
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun createModMenu() {
        try {
            Log.d("FloatingOverlay", "🎨 Creating mod menu...")
            
            modMenuView = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(android.graphics.Color.parseColor("#1A1A2E"))
                layoutParams = LinearLayout.LayoutParams(320, LinearLayout.LayoutParams.WRAP_CONTENT)
                setPadding(12, 12, 12, 12)
            }
            
            // Title
            val titleTV = TextView(this).apply {
                text = "⚡ MOD MENU ⚡"
                textSize = 16f
                setTextColor(android.graphics.Color.parseColor("#FFD700"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                gravity = android.view.Gravity.CENTER
                setTypeface(null, android.graphics.Typeface.BOLD)
                setPadding(0, 0, 0, 10)
            }
            modMenuView?.addView(titleTV)
            
            // ====== AIM SECTION ======
            val aimTitle = TextView(this).apply {
                text = "🎯 AIM FEATURES"
                textSize = 14f
                setTextColor(android.graphics.Color.parseColor("#FFD700"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(0, 10, 0, 8)
                setTypeface(null, android.graphics.Typeface.BOLD)
            }
            modMenuView?.addView(aimTitle)
            
            // Aim Assist
            modMenuView?.addView(createCheckbox("🎯 Aim Assist") { isChecked ->
                if (isChecked) {
                    gameInjector.injectAimBot(5.0f)
                    Log.d("ModMenu", "✅ Aim Assist WORKING IN GAME!")
                } else {
                    gameInjector.killAimBot()
                }
            })
            
            // Silent Aim
            modMenuView?.addView(createCheckbox("🎯 Silent Aim") { isChecked ->
                if (isChecked) {
                    gameInjector.injectSilentAim()
                    Log.d("ModMenu", "✅ Silent Aim WORKING IN GAME!")
                } else {
                    Log.d("ModMenu", "❌ Silent Aim OFF")
                }
            })
            
            // Auto Aim
            modMenuView?.addView(createCheckbox("🤖 Auto Aim") { isChecked ->
                if (isChecked) {
                    gameInjector.injectAimBot(8.0f)
                    Log.d("ModMenu", "✅ Auto Aim WORKING IN GAME!")
                } else {
                    gameInjector.killAimBot()
                }
            })
            
            // ====== ESP SECTION ======
            val espTitle = TextView(this).apply {
                text = "👁️ ESP FEATURES"
                textSize = 14f
                setTextColor(android.graphics.Color.parseColor("#00FF00"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(0, 10, 0, 8)
                setTypeface(null, android.graphics.Typeface.BOLD)
            }
            modMenuView?.addView(espTitle)
            
            // ESP Box
            modMenuView?.addView(createCheckbox("📦 ESP Box") { isChecked ->
                if (isChecked) {
                    gameInjector.injectESPBox()
                    Log.d("ModMenu", "✅ ESP Box WORKING IN GAME!")
                } else {
                    gameInjector.killESPBox()
                }
            })
            
            // ESP Line
            modMenuView?.addView(createCheckbox("📏 ESP Line") { isChecked ->
                if (isChecked) {
                    gameInjector.injectESPLine()
                    Log.d("ModMenu", "✅ ESP Line WORKING IN GAME!")
                } else {
                    gameInjector.killESPLine()
                }
            })
            
            // ESP Name
            modMenuView?.addView(createCheckbox("📝 ESP Name") { isChecked ->
                if (isChecked) {
                    gameInjector.injectESPName()
                    Log.d("ModMenu", "✅ ESP Name WORKING IN GAME!")
                } else {
                    Log.d("ModMenu", "❌ ESP Name OFF")
                }
            })
            
            // ESP Health
            modMenuView?.addView(createCheckbox("❤️ ESP Health") { isChecked ->
                if (isChecked) {
                    gameInjector.injectESPHealth()
                    Log.d("ModMenu", "✅ ESP Health WORKING IN GAME!")
                } else {
                    Log.d("ModMenu", "❌ ESP Health OFF")
                }
            })
            
            // Headshot Only
            modMenuView?.addView(createCheckbox("💀 Headshot Only") { isChecked ->
                if (isChecked) {
                    gameInjector.injectHeadshotOnly()
                    Log.d("ModMenu", "✅ Headshot Only WORKING IN GAME!")
                } else {
                    gameInjector.disableHeadshotOnly()
                }
            })
            
            // ====== FOV SLIDER ======
            val fovLabel = TextView(this).apply {
                text = "🔍 FOV: 90°"
                textSize = 12f
                setTextColor(android.graphics.Color.WHITE)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(0, 10, 0, 5)
                tag = "fovLabel"
            }
            modMenuView?.addView(fovLabel)
            
            val fovSeekbar = SeekBar(this).apply {
                max = 180
                progress = 90
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    50
                )
                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        if (fromUser) {
                            gameInjector.setFOV(progress)
                            fovLabel.text = "🔍 FOV: $progress°"
                            Log.d("ModMenu", "✅ FOV $progress° WORKING IN GAME!")
                        }
                    }
                    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                })
            }
            modMenuView?.addView(fovSeekbar)
            
            // Close button
            val closeBtn = Button(this).apply {
                text = "❌ Close Menu"
                setBackgroundColor(android.graphics.Color.parseColor("#FF3333"))
                setTextColor(android.graphics.Color.WHITE)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(0, 8, 0, 0)
                setOnClickListener {
                    modMenuVisible = false
                    modMenuView?.visibility = View.GONE
                    Toast.makeText(this@FloatingOverlayService, "Menu Closed", Toast.LENGTH_SHORT).show()
                }
            }
            modMenuView?.addView(closeBtn)
            
            // Add to window manager
            modMenuParams = WindowManager.LayoutParams().apply {
                type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else
                    WindowManager.LayoutParams.TYPE_PHONE
                
                format = PixelFormat.TRANSLUCENT
                flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                
                width = 340
                height = WindowManager.LayoutParams.WRAP_CONTENT
                x = 20
                y = 200
            }
            
            windowManager.addView(modMenuView, modMenuParams)
            modMenuView?.visibility = View.GONE
            Log.d("FloatingOverlay", "✅ Mod menu created successfully")
            
        } catch (e: Exception) {
            Log.e("FloatingOverlay", "❌ Error creating mod menu: ${e.message}", e)
            e.printStackTrace()
        }
    }
    
    private fun createCheckbox(label: String, onChecked: (Boolean) -> Unit): CheckBox {
        return CheckBox(this).apply {
            text = label
            textSize = 12f
            isChecked = false
            setTextColor(android.graphics.Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(8, 4, 8, 4)
            
            setOnCheckedChangeListener { _, isChecked ->
                onChecked(isChecked)
            }
        }
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        super.onDestroy()
        try {
            floatingView?.let { windowManager.removeView(it) }
            modMenuView?.let { windowManager.removeView(it) }
            Log.d("FloatingOverlay", "✅ Service destroyed")
        } catch (e: Exception) {
            Log.e("FloatingOverlay", "Error in onDestroy: ${e.message}")
        }
    }
}
