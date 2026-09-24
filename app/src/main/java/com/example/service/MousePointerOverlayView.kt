package com.example.service

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import com.example.model.PointerStyle

/**
 * Custom High-Visibility Hardware-like Floating Digital Mouse Cursor View.
 * Renders an unmistakable classic PC mouse arrow, tactical laser crosshairs,
 * live coordinates HUD badge, and animated click shockwaves directly on the screen overlay.
 */
@SuppressLint("ViewConstructor")
class MousePointerOverlayView(
    context: Context,
    private val windowManager: WindowManager,
    private val layoutParams: WindowManager.LayoutParams
) : View(context) {

    private val density = context.resources.displayMetrics.density

    // Paints
    private val arrowFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    private val arrowBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 3f * density
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    private val laserDotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#EF4444") // Bright Laser Red
        style = Paint.Style.FILL
    }

    private val laserGlowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#66EF4444")
        style = Paint.Style.FILL
    }

    private val reticlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#06B6D4") // Cyan Neon
        style = Paint.Style.STROKE
        strokeWidth = 2.5f * density
    }

    private val badgeBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#D90F172A") // Deep Slate semi-opaque
        style = Paint.Style.FILL
    }

    private val badgeBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#38BDF8") // Sky blue border
        style = Paint.Style.STROKE
        strokeWidth = 1.5f * density
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 10.5f * density
        isFakeBoldText = true
    }

    private val ripplePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#38BDF8")
        style = Paint.Style.STROKE
        strokeWidth = 3f * density
    }

    // Geometry
    private val arrowPath = Path()
    private val badgeRect = RectF()

    // State
    var currentStyle: PointerStyle = PointerStyle.MOUSE_ARROW
        set(value) {
            field = value
            invalidate()
        }

    var targetX: Int = 540
        set(value) {
            field = value
            invalidate()
        }

    var targetY: Int = 960
        set(value) {
            field = value
            invalidate()
        }

    // Ripple animation state
    private var rippleRadius = 0f
    private var rippleAlpha = 0
    private var rippleAnimator: ValueAnimator? = null

    // Touch dragging tracking
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f

    init {
        buildArrowPath()
    }

    private fun buildArrowPath() {
        val s = density
        arrowPath.reset()
        // Hotspot exactly at (8dp, 8dp)
        arrowPath.moveTo(8f * s, 8f * s)
        arrowPath.lineTo(8f * s, 42f * s)
        arrowPath.lineTo(17f * s, 33f * s)
        arrowPath.lineTo(25f * s, 48f * s)
        arrowPath.lineTo(31f * s, 45f * s)
        arrowPath.lineTo(23f * s, 30f * s)
        arrowPath.lineTo(36f * s, 30f * s)
        arrowPath.close()
    }

    fun triggerClickAnimation() {
        rippleAnimator?.cancel()
        val maxRadius = 48f * density
        rippleAnimator = ValueAnimator.ofFloat(0f, maxRadius).apply {
            duration = 350L
            interpolator = DecelerateInterpolator()
            addUpdateListener { anim ->
                val progress = anim.animatedFraction
                rippleRadius = anim.animatedValue as Float
                rippleAlpha = ((1f - progress) * 255).toInt()
                invalidate()
            }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val s = density
        val hx = 8f * s
        val hy = 8f * s

        // 1. Draw click shockwave/ripple if active
        if (rippleAlpha > 0) {
            ripplePaint.alpha = rippleAlpha
            canvas.drawCircle(hx, hy, rippleRadius, ripplePaint)
            // Secondary inner laser glow
            laserGlowPaint.alpha = (rippleAlpha * 0.5f).toInt()
            canvas.drawCircle(hx, hy, rippleRadius * 0.6f, laserGlowPaint)
        }

        // 2. Draw Cursor based on selected style
        when (currentStyle) {
            PointerStyle.MOUSE_ARROW -> {
                // Outer subtle glow halo
                laserGlowPaint.color = Color.parseColor("#4438BDF8") // Sky blue aura
                canvas.drawCircle(hx, hy, 12f * s, laserGlowPaint)

                // High-contrast border and fill
                canvas.drawPath(arrowPath, arrowBorderPaint)
                canvas.drawPath(arrowPath, arrowFillPaint)

                // Glowing Hotspot Tip (Exact point of click)
                laserGlowPaint.color = Color.parseColor("#88EF4444")
                canvas.drawCircle(hx, hy, 6f * s, laserGlowPaint)
                canvas.drawCircle(hx, hy, 3f * s, laserDotPaint)
            }

            PointerStyle.TACTICAL_CROSSHAIR -> {
                val cx = 24f * s
                val cy = 24f * s
                val r = 18f * s

                // Outer circle
                canvas.drawCircle(cx, cy, r, reticlePaint)
                // Center dot
                canvas.drawCircle(cx, cy, 4f * s, laserDotPaint)

                // Crosshair tick lines
                canvas.drawLine(cx - r - 6f * s, cy, cx - 6f * s, cy, reticlePaint)
                canvas.drawLine(cx + 6f * s, cy, cx + r + 6f * s, cy, reticlePaint)
                canvas.drawLine(cx, cy - r - 6f * s, cx, cy - 6f * s, reticlePaint)
                canvas.drawLine(cx, cy + 6f * s, cx, cy + r + 6f * s, reticlePaint)
            }

            PointerStyle.CYBER_RETICLE -> {
                val cx = 24f * s
                val cy = 24f * s
                val r = 20f * s

                // Cyber square brackets
                reticlePaint.color = Color.parseColor("#A855F7") // Purple
                canvas.drawRoundRect(cx - r, cy - r, cx + r, cy + r, 6f * s, 6f * s, reticlePaint)
                canvas.drawCircle(cx, cy, 4f * s, laserDotPaint)
                reticlePaint.color = Color.parseColor("#06B6D4") // Reset
            }

            PointerStyle.PULSE_BULLSEYE -> {
                val cx = 20f * s
                val cy = 20f * s
                canvas.drawCircle(cx, cy, 18f * s, laserGlowPaint)
                canvas.drawCircle(cx, cy, 10f * s, reticlePaint)
                canvas.drawCircle(cx, cy, 4f * s, laserDotPaint)
            }
        }

        // 3. Live Coordinate Badge (positioned under the cursor)
        val coordText = "X:$targetX Y:$targetY"
        val textWidth = textPaint.measureText(coordText)
        val badgeX = 6f * s
        val badgeY = 52f * s
        val padH = 6f * s
        val padV = 3f * s

        badgeRect.set(badgeX, badgeY, badgeX + textWidth + (padH * 2), badgeY + 14f * s + (padV * 2))
        canvas.drawRoundRect(badgeRect, 6f * s, 6f * s, badgeBackgroundPaint)
        canvas.drawRoundRect(badgeRect, 6f * s, 6f * s, badgeBorderPaint)
        canvas.drawText(coordText, badgeX + padH, badgeY + 12f * s, textPaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = layoutParams.x
                initialY = layoutParams.y
                initialTouchX = event.rawX
                initialTouchY = event.rawY
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val newX = (initialX + (event.rawX - initialTouchX)).toInt().coerceAtLeast(0)
                val newY = (initialY + (event.rawY - initialTouchY)).toInt().coerceAtLeast(0)

                layoutParams.x = newX
                layoutParams.y = newY
                targetX = newX
                targetY = newY

                windowManager.updateViewLayout(this, layoutParams)
                OverlayBridge.updateCoordinatesFromDrag(newX, newY)
                invalidate()
                return true
            }

            MotionEvent.ACTION_UP -> {
                // If it was just a quick tap without drag, trigger a single test click!
                val dx = kotlin.math.abs(event.rawX - initialTouchX)
                val dy = kotlin.math.abs(event.rawY - initialTouchY)
                if (dx < 10 && dy < 10) {
                    triggerClickAnimation()
                    OverlayBridge.sendCommand(OverlayBridge.OverlayCommand.PerformSingleClick)
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}
