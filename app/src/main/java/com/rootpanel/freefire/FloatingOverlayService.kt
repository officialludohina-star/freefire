package com.rootpanel.freefire

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.app.NotificationCompat

class FloatingOverlayService : Service() {
    
    private lateinit var windowManager: WindowManager
    private var floatingView: LinearLayout? = null
    private var modMenuView: LinearLayout? = null
    private var modMenuVisible = false
    private var gamePackage = ""
    private var buttonAdded = false
    
    private lateinit var gameInjector: GameInjector
    private lateinit var rootUtils: RootUtils
    
    companion object {
        private const val CHANNEL_ID = "freefire_mod_channel"
        private const val NOTIFICATION_ID = 1001
    }
    
    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        rootUtils = RootUtils()
        gameInjector = GameInjector(rootUtils, this)
        Log.d("FloatingOverlay", "Service Created")
        
        createNotificationChannel()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Free Fire Mod",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        gamePackage = intent?.getStringExtra("game_package") ?: "com.dts.freefireth"
        Log.d("FloatingOverlay", "Starting for: $gamePackage")
        
        startForegroundNotification()
        
        // Try multiple times to add buttons
        Handler(Looper.getMainLooper()).postDelayed({
            createFloatingButtons()
        }, 300)
        
        Handler(Looper.getMainLooper()).postDelayed({
            if (!buttonAdded) {
                Log.d("FloatingOverlay", "Retry 1...")
                createFloatingButtons()
            }
        }, 1500)
        
        Handler(Looper.getMainLooper()).postDelayed({
            if (!buttonAdded) {
                Log.d("FloatingOverlay", "Retry 2...")
                createFloatingButtons()
            }
        }, 3000)
        
        return START_STICKY
    }
    
    private fun startForegroundNotification() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Mod Menu Active")
            .setContentText("HINA button on screen")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        
        startForeground(NOTIFICATION_ID, notification)
    }
    
    private fun createFloatingButtons() {
        try {
            if (buttonAdded && floatingView != null) {
                Log.d("FloatingOverlay", "Buttons already added")
                return
            }
            
            Log.d("FloatingOverlay", "Adding buttons...")
            
            floatingView = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setBackgroundColor(Color.TRANSPARENT)
            }
            
            val hinaBtn = Button(this).apply {
                text = "HINA\n🔥"
                textSize = 11f
                setBackgroundColor(Color.parseColor("#FFD700"))
                setTextColor(Color.BLACK)
                setAllCaps(false)
                layoutParams = LinearLayout.LayoutParams(80, 80)
                setPadding(2, 2, 2, 2)
                setTypeface(null, android.graphics.Typeface.BOLD)
                
                setOnClickListener {
                    Log.d("FloatingOverlay", "HINA CLICKED")
                    toggleModMenu()
                }
            }
            
            val legendBtn = Button(this).apply {
                text = "LEGEND\n✕"
                textSize = 11f
                setBackgroundColor(Color.parseColor("#FF3333"))
                setTextColor(Color.WHITE)
                setAllCaps(false)
                layoutParams = LinearLayout.LayoutParams(80, 80)
                setPadding(2, 2, 2, 2)
                setTypeface(null, android.graphics.Typeface.BOLD)
                
                setOnClickListener {
                    Log.d("FloatingOverlay", "LEGEND CLICKED")
                    stopSelf()
                }
            }
            
            floatingView?.addView(hinaBtn)
            floatingView?.addView(legendBtn)
            
            val params = WindowManager.LayoutParams().apply {
                type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    @Suppress("DEPRECATION")
                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
                }
                
                format = PixelFormat.TRANSLUCENT
                
                flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                
                width = 170
                height = 85
                x = 10
                y = 100
            }
            
            windowManager.addView(floatingView, params)
            buttonAdded = true
            
            Log.d("FloatingOverlay", "✅ Buttons Added!")
            
            createModMenu()
            
        } catch (e: Exception) {
            Log.e("FloatingOverlay", "Error: ${e.message}", e)
            buttonAdded = false
        }
    }
    
    private fun toggleModMenu() {
        if (modMenuView == null) {
            createModMenu()
        }
        
        modMenuVisible = !modMenuVisible
        modMenuView?.visibility = if (modMenuVisible) View.VISIBLE else View.GONE
        
        Toast.makeText(this, "Menu ${if (modMenuVisible) "Open" else "Close"}", Toast.LENGTH_SHORT).show()
    }
    
    private fun createModMenu() {
        try {
            modMenuView = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(Color.parseColor("#1A1A2E"))
                layoutParams = LinearLayout.LayoutParams(320, LinearLayout.LayoutParams.WRAP_CONTENT)
                setPadding(12, 12, 12, 12)
            }
            
            val titleTV = TextView(this).apply {
                text = "MOD MENU"
                textSize = 15f
                setTextColor(Color.parseColor("#FFD700"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                gravity = android.view.Gravity.CENTER
                setTypeface(null, android.graphics.Typeface.BOLD)
                setPadding(0, 0, 0, 10)
            }
            modMenuView?.addView(titleTV)
            
            val aimTitle = TextView(this).apply {
                text = "AIM"
                textSize = 12f
                setTextColor(Color.parseColor("#FFD700"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(0, 8, 0, 5)
                setTypeface(null, android.graphics.Typeface.BOLD)
            }
            modMenuView?.addView(aimTitle)
            
            modMenuView?.addView(createCheckbox("Aim Assist") { if (it) gameInjector.injectAimBot(5.0f) else gameInjector.killAimBot() })
            modMenuView?.addView(createCheckbox("Silent Aim") { if (it) gameInjector.injectSilentAim() })
            modMenuView?.addView(createCheckbox("Auto Aim") { if (it) gameInjector.injectAimBot(8.0f) else gameInjector.killAimBot() })
            
            val espTitle = TextView(this).apply {
                text = "ESP"
                textSize = 12f
                setTextColor(Color.parseColor("#00FF00"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(0, 8, 0, 5)
                setTypeface(null, android.graphics.Typeface.BOLD)
            }
            modMenuView?.addView(espTitle)
            
            modMenuView?.addView(createCheckbox("ESP Box") { if (it) gameInjector.injectESPBox() else gameInjector.killESPBox() })
            modMenuView?.addView(createCheckbox("ESP Line") { if (it) gameInjector.injectESPLine() else gameInjector.killESPLine() })
            modMenuView?.addView(createCheckbox("ESP Name") { if (it) gameInjector.injectESPName() })
            modMenuView?.addView(createCheckbox("ESP Health") { if (it) gameInjector.injectESPHealth() })
            modMenuView?.addView(createCheckbox("Headshot Only") { if (it) gameInjector.injectHeadshotOnly() else gameInjector.disableHeadshotOnly() })
            
            val fovSeekbar = SeekBar(this).apply {
                max = 180
                progress = 90
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    40
                )
                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        if (fromUser) gameInjector.setFOV(progress)
                    }
                    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                })
            }
            modMenuView?.addView(fovSeekbar)
            
            val params = WindowManager.LayoutParams().apply {
                type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    @Suppress("DEPRECATION")
                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
                }
                format = PixelFormat.TRANSLUCENT
                flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                
                width = 340
                height = WindowManager.LayoutParams.WRAP_CONTENT
                x = 15
                y = 200
            }
            
            windowManager.addView(modMenuView, params)
            modMenuView?.visibility = View.GONE
            
        } catch (e: Exception) {
            Log.e("FloatingOverlay", "Menu error: ${e.message}")
        }
    }
    
    private fun createCheckbox(label: String, onChecked: (Boolean) -> Unit): CheckBox {
        return CheckBox(this).apply {
            text = label
            textSize = 11f
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(8, 3, 8, 3)
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
        } catch (e: Exception) {
            Log.e("FloatingOverlay", "Destroy error: ${e.message}")
        }
    }
}
