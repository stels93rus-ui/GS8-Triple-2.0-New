package ru.gs8triple

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView

class OverlayService : Service() {
    private lateinit var wm: WindowManager
    private var shortcut: TextView? = null
    private lateinit var params: WindowManager.LayoutParams

    override fun onCreate() {
        super.onCreate()
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        showShortcut()
    }

    private fun showShortcut() {
        val density = resources.displayMetrics.density
        val size = (54 * density).toInt()
        val view = TextView(this).apply {
            text = "GS8"
            textSize = 13f
            setTextColor(0xFFFFFFFF.toInt())
            gravity = Gravity.CENTER
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(0xEE173B59.toInt())
                setStroke((2 * density).toInt(), 0xFF65B9FF.toInt())
            }
            elevation = 8 * density
        }

        params = WindowManager.LayoutParams(
            size,
            size,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = (24 * density).toInt()
            y = (90 * density).toInt()
        }

        view.setOnTouchListener(object : View.OnTouchListener {
            private var downX = 0f
            private var downY = 0f
            private var startX = 0
            private var startY = 0
            private var moved = false

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        downX = event.rawX
                        downY = event.rawY
                        startX = params.x
                        startY = params.y
                        moved = false
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val dx = (event.rawX - downX).toInt()
                        val dy = (event.rawY - downY).toInt()
                        if (kotlin.math.abs(dx) > 8 || kotlin.math.abs(dy) > 8) moved = true
                        if (moved) {
                            params.x = (startX + dx).coerceAtLeast(0)
                            params.y = (startY + dy).coerceAtLeast(0)
                            try { wm.updateViewLayout(view, params) } catch (_: Exception) {}
                        }
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        if (!moved) {
                            val intent = Intent(this@OverlayService, MainActivity::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            startActivity(intent)
                        }
                        return true
                    }
                    MotionEvent.ACTION_CANCEL -> return true
                }
                return false
            }
        })

        wm.addView(view, params)
        shortcut = view
    }

    override fun onDestroy() {
        shortcut?.let { try { wm.removeView(it) } catch (_: Exception) {} }
        shortcut = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
