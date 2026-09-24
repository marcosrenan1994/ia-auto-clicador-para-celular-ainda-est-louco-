package com.example.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.viewmodel.AutomationStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Foreground Service that manages floating on-screen widgets:
 * 1. Windows 11 Glassmorphism Floating Control HUD with:
 *    - Live 1000 CPS speedometer badge
 *    - Smart Visual Trigger scanner toggle
 *    - Macro Recorder toggle
 *    - Multi-Point target manager
 *    - Microsoft Store Profile switcher
 * 2. Floating Hardware-Visible Mouse Pointer & Click Shockwave View
 */
class FloatingOverlayService : Service() {

    companion object {
        private const val TAG = "FloatingOverlayService"
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var windowManager: WindowManager? = null

    private var controlView: View? = null
    private var controlParams: WindowManager.LayoutParams? = null

    private var pointerView: MousePointerOverlayView? = null
    private var pointerParams: WindowManager.LayoutParams? = null

    // HUD Views
    private var statusIndicator: View? = null
    private var statsTextView: TextView? = null
    private var cpsBadgeView: TextView? = null
    private var profileBadgeView: TextView? = null
    private var tickerTextView: TextView? = null
    private var actionPlayPauseIcon: ImageView? = null
    private var visualTriggerIcon: ImageView? = null
    private var macroRecordIcon: ImageView? = null
    private var isExpanded = true
    private var expandedRowsContainer: LinearLayout? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForegroundSafely()
        OverlayBridge.isOverlayActive.value = true
        initializeOverlaysIfPermitted()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundSafely()
        OverlayBridge.isOverlayActive.value = true
        initializeOverlaysIfPermitted()
        return START_STICKY
    }

    private fun initializeOverlaysIfPermitted() {
        if (!Settings.canDrawOverlays(this)) {
            Log.w(TAG, "Cannot draw overlays yet - permission not granted")
            return
        }

        try {
            if (windowManager == null) {
                windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            }
            if (controlView == null) {
                setupControlOverlay()
            }
            if (pointerView == null) {
                setupPointerOverlay()
            }
            observeBridgeState()
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing floating overlays", e)
        }
    }

    private fun startForegroundSafely() {
        val channelId = "iaut_clic_overlay_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "IAut Clic Floating Overlay",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Controle flutuante Glassmorphism e mouse visível ativos sobre a tela"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("IAut Clic: HUD Windows 11 Glassmorphism")
            .setContentText("Auto-clicker 1000 CPS, gatilhos visuais e macros ativos.")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ServiceCompat.startForeground(
                    this,
                    1001,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                )
            } else {
                startForeground(1001, notification)
            }
        } catch (e: Exception) {
            Log.e(TAG, "startForeground error", e)
            try {
                startForeground(1001, notification)
            } catch (ex: Exception) {
                Log.e(TAG, "Fatal startForeground", ex)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupControlOverlay() {
        val wm = windowManager ?: return
        if (controlView != null) return

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 24
            y = 150
        }
        controlParams = params

        // Windows 11 Glassmorphism root container
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 12, 16, 12)
            gravity = Gravity.CENTER_HORIZONTAL

            val background = GradientDrawable().apply {
                setColor(Color.parseColor("#E60F172A")) // Translucent dark acrylic mica
                cornerRadius = 28f
                setStroke(2, Color.parseColor("#00E5FF")) // Cyber cyan neon border
            }
            setBackground(background)
            elevation = 28f
        }

        // Top Row: Status, CPS Badge, Main Controls & Collapse Toggle
        val topRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Draggable Grip & Status Dot
        val statusDot = View(this).apply {
            val dot = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.parseColor("#94A3B8")) // Default idle gray
            }
            background = dot
            layoutParams = LinearLayout.LayoutParams(20, 20).apply {
                rightMargin = 10
            }
        }
        statusIndicator = statusDot
        topRow.addView(statusDot)

        // Live 1000 CPS Speedometer Badge
        val cpsPill = TextView(this).apply {
            text = "⚡ 1000 CPS"
            setTextColor(Color.parseColor("#00E5FF"))
            textSize = 10.5f
            val pillBg = GradientDrawable().apply {
                setColor(Color.parseColor("#3300E5FF"))
                cornerRadius = 14f
                setStroke(1, Color.parseColor("#00E5FF"))
            }
            background = pillBg
            setPadding(12, 4, 12, 4)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                rightMargin = 10
            }
        }
        cpsBadgeView = cpsPill
        topRow.addView(cpsPill)

        // Counter text
        val infoText = TextView(this).apply {
            text = "0 clics"
            setTextColor(Color.WHITE)
            textSize = 11f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                rightMargin = 10
            }
        }
        statsTextView = infoText
        topRow.addView(infoText)

        // Play / Pause Button
        val playPauseBtn = ImageView(this).apply {
            setImageResource(android.R.drawable.ic_media_play)
            setColorFilter(Color.parseColor("#38BDF8"))
            layoutParams = LinearLayout.LayoutParams(48, 48).apply {
                rightMargin = 8
            }
            setOnClickListener {
                if (OverlayBridge.automationStatus.value == AutomationStatus.RUNNING) {
                    OverlayBridge.sendCommand(OverlayBridge.OverlayCommand.PauseAutomation)
                } else {
                    OverlayBridge.sendCommand(OverlayBridge.OverlayCommand.StartAutomation)
                }
            }
        }
        actionPlayPauseIcon = playPauseBtn
        topRow.addView(playPauseBtn)

        // Smart Visual Trigger Scanner Button
        val triggerBtn = ImageView(this).apply {
            setImageResource(android.R.drawable.ic_menu_search)
            setColorFilter(Color.parseColor("#10B981")) // Emerald green
            layoutParams = LinearLayout.LayoutParams(48, 48).apply {
                rightMargin = 8
            }
            setOnClickListener {
                OverlayBridge.sendCommand(OverlayBridge.OverlayCommand.ToggleVisualTriggerScanner)
            }
        }
        visualTriggerIcon = triggerBtn
        topRow.addView(triggerBtn)

        // Macro Recorder Button (Red circle)
        val macroBtn = ImageView(this).apply {
            setImageResource(android.R.drawable.presence_online)
            setColorFilter(Color.parseColor("#EF4444")) // Crimson record
            layoutParams = LinearLayout.LayoutParams(44, 44).apply {
                rightMargin = 8
            }
            setOnClickListener {
                OverlayBridge.sendCommand(OverlayBridge.OverlayCommand.ToggleMacroRecording)
            }
        }
        macroRecordIcon = macroBtn
        topRow.addView(macroBtn)

        // Add Target Point (+) Button
        val addPointBtn = ImageView(this).apply {
            setImageResource(android.R.drawable.ic_input_add)
            setColorFilter(Color.parseColor("#A855F7")) // Violet
            layoutParams = LinearLayout.LayoutParams(44, 44).apply {
                rightMargin = 8
            }
            setOnClickListener {
                OverlayBridge.sendCommand(OverlayBridge.OverlayCommand.AddMultiPoint)
            }
        }
        topRow.addView(addPointBtn)

        // Center Mouse Cursor Button
        val centerMouseBtn = ImageView(this).apply {
            setImageResource(android.R.drawable.ic_menu_mylocation)
            setColorFilter(Color.parseColor("#06B6D4")) // Cyan
            layoutParams = LinearLayout.LayoutParams(44, 44).apply {
                rightMargin = 8
            }
            setOnClickListener {
                OverlayBridge.sendCommand(OverlayBridge.OverlayCommand.CenterPointer)
            }
        }
        topRow.addView(centerMouseBtn)

        // Minimize / Expand Toggle
        val expandBtn = ImageView(this).apply {
            setImageResource(android.R.drawable.arrow_down_float)
            setColorFilter(Color.parseColor("#94A3B8"))
            layoutParams = LinearLayout.LayoutParams(40, 40).apply {
                rightMargin = 8
            }
            setOnClickListener {
                isExpanded = !isExpanded
                expandedRowsContainer?.visibility = if (isExpanded) View.VISIBLE else View.GONE
                setImageResource(if (isExpanded) android.R.drawable.arrow_up_float else android.R.drawable.arrow_down_float)
            }
        }
        topRow.addView(expandBtn)

        // Close Floating Service Button
        val closeBtn = ImageView(this).apply {
            setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            setColorFilter(Color.parseColor("#64748B"))
            layoutParams = LinearLayout.LayoutParams(40, 40)
            setOnClickListener {
                stopSelf()
            }
        }
        topRow.addView(closeBtn)
        root.addView(topRow)

        // Secondary Expanded Container (Glassmorphism sub-rows)
        val expandedContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 6
            }
        }
        expandedRowsContainer = expandedContainer

        // Row 2: Active Profile Pill with Next Cycle
        val profileRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(10, 4, 10, 4)
            val pill = GradientDrawable().apply {
                setColor(Color.parseColor("#26334155"))
                cornerRadius = 12f
                setStroke(1, Color.parseColor("#475569"))
            }
            background = pill
            setOnClickListener {
                OverlayBridge.sendCommand(OverlayBridge.OverlayCommand.CycleProfileNext)
            }
        }

        val profileLabel = TextView(this).apply {
            text = "⚡ Ultra CPS 1000 Gamer ❯"
            setTextColor(Color.parseColor("#F1F5F9"))
            textSize = 10f
        }
        profileBadgeView = profileLabel
        profileRow.addView(profileLabel)
        expandedContainer.addView(profileRow)

        // Row 3: Live AI Brain & Ticker
        val tickerRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(6, 4, 6, 2)
        }

        val tickerText = TextView(this).apply {
            text = "🧠 IA Quântica Ativa | 🎯 Gatilho Visual Monitorando"
            setTextColor(Color.parseColor("#94A3B8"))
            textSize = 9.5f
        }
        tickerTextView = tickerText
        tickerRow.addView(tickerText)
        expandedContainer.addView(tickerRow)

        root.addView(expandedContainer)

        // Dragging handler for the control bar
        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f

        root.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (event.rawX - initialTouchX).toInt()
                    params.y = initialY + (event.rawY - initialTouchY).toInt()
                    try {
                        wm.updateViewLayout(root, params)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error updating control layout", e)
                    }
                    true
                }
                else -> false
            }
        }

        controlView = root
        try {
            wm.addView(root, params)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add control view to WindowManager", e)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupPointerOverlay() {
        val wm = windowManager ?: return
        if (pointerView != null) return

        val metrics = resources.displayMetrics
        val density = metrics.density
        val pointerSize = (76 * density).toInt()

        val defaultCenterX = (metrics.widthPixels / 2) - (pointerSize / 2)
        val defaultCenterY = (metrics.heightPixels / 2) - (pointerSize / 2)

        val posX = if (OverlayBridge.currentX.value == 540) defaultCenterX else OverlayBridge.currentX.value
        val posY = if (OverlayBridge.currentY.value == 960) defaultCenterY else OverlayBridge.currentY.value

        OverlayBridge.currentX.value = posX
        OverlayBridge.currentY.value = posY

        val params = WindowManager.LayoutParams(
            pointerSize,
            pointerSize,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = posX
            y = posY
        }
        pointerParams = params

        val mousePointerView = MousePointerOverlayView(this, wm, params).apply {
            targetX = params.x
            targetY = params.y
            currentStyle = OverlayBridge.pointerStyle.value
        }

        pointerView = mousePointerView
        try {
            wm.addView(mousePointerView, params)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add pointer view to WindowManager", e)
        }
    }

    private fun observeBridgeState() {
        val wm = windowManager ?: return

        // Automation Status updates
        serviceScope.launch {
            OverlayBridge.automationStatus.collectLatest { status ->
                val color = when (status) {
                    AutomationStatus.IDLE -> Color.parseColor("#94A3B8")
                    AutomationStatus.RUNNING -> Color.parseColor("#10B981")
                    AutomationStatus.PAUSED -> Color.parseColor("#F59E0B")
                }
                (statusIndicator?.background as? GradientDrawable)?.setColor(color)

                if (status == AutomationStatus.RUNNING) {
                    actionPlayPauseIcon?.setImageResource(android.R.drawable.ic_media_pause)
                    actionPlayPauseIcon?.setColorFilter(Color.parseColor("#F59E0B"))
                } else {
                    actionPlayPauseIcon?.setImageResource(android.R.drawable.ic_media_play)
                    actionPlayPauseIcon?.setColorFilter(Color.parseColor("#38BDF8"))
                }
            }
        }

        // Live CPS Updates
        serviceScope.launch {
            OverlayBridge.currentCps.collectLatest { cps ->
                cpsBadgeView?.text = "⚡ $cps CPS"
            }
        }

        // Active Profile Updates
        serviceScope.launch {
            OverlayBridge.activeProfileTitle.collectLatest { title ->
                profileBadgeView?.text = "$title ❯"
            }
        }

        // Macro Recording State Updates
        serviceScope.launch {
            OverlayBridge.isMacroRecording.collectLatest { recording ->
                if (recording) {
                    macroRecordIcon?.setColorFilter(Color.parseColor("#EF4444")) // Glowing red
                } else {
                    macroRecordIcon?.setColorFilter(Color.parseColor("#64748B")) // Dimmed
                }
            }
        }

        // Visual Trigger Active Updates
        serviceScope.launch {
            OverlayBridge.isVisualTriggerActive.collectLatest { active ->
                if (active) {
                    visualTriggerIcon?.setColorFilter(Color.parseColor("#10B981")) // Green
                } else {
                    visualTriggerIcon?.setColorFilter(Color.parseColor("#64748B")) // Dimmed
                }
            }
        }

        // Click Counter updates
        serviceScope.launch {
            OverlayBridge.clickCounter.collectLatest { count ->
                val repeat = OverlayBridge.targetRepeat.value
                val repeatLabel = if (repeat <= 0) "∞" else repeat.toString()
                statsTextView?.text = "$count / $repeatLabel"
            }
        }

        // Visual pointer position sync
        serviceScope.launch {
            OverlayBridge.currentX.collectLatest { x ->
                pointerParams?.let { params ->
                    params.x = x
                    pointerView?.targetX = x
                    try {
                        pointerView?.let { wm.updateViewLayout(it, params) }
                    } catch (_: Exception) {}
                }
            }
        }

        serviceScope.launch {
            OverlayBridge.currentY.collectLatest { y ->
                pointerParams?.let { params ->
                    params.y = y
                    pointerView?.targetY = y
                    try {
                        pointerView?.let { wm.updateViewLayout(it, params) }
                    } catch (_: Exception) {}
                }
            }
        }

        // Pointer visibility
        serviceScope.launch {
            OverlayBridge.pointerVisible.collectLatest { visible ->
                pointerView?.visibility = if (visible) View.VISIBLE else View.GONE
            }
        }

        // Pointer style
        serviceScope.launch {
            OverlayBridge.pointerStyle.collectLatest { style ->
                pointerView?.currentStyle = style
            }
        }

        // Click shockwave pulse
        serviceScope.launch {
            OverlayBridge.clickPulseEvent.collectLatest {
                pointerView?.triggerClickAnimation()
            }
        }

        // Ticker & AI updates
        serviceScope.launch {
            OverlayBridge.spreadTickerInfo.collectLatest { info ->
                tickerTextView?.text = info
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        OverlayBridge.isOverlayActive.value = false
        serviceScope.cancel()

        try {
            controlView?.let { windowManager?.removeView(it) }
            pointerView?.let { windowManager?.removeView(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Error cleaning up floating views", e)
        }
    }
}
