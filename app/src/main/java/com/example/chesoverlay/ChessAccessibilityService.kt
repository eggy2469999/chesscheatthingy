package com.example.chesoverlay

import android.accessibilityservice.AccessibilityService
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.view.Display
import android.view.accessibility.AccessibilityEvent

class ChessAccessibilityService : AccessibilityService() {

    private var overlay: OverlayView? = null
    private val engine = StockfishEngine()
    private val vision = BoardVision()
    
    override fun onServiceConnected() {
        super.onServiceConnected()
        overlay = OverlayView(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (event.packageName == "com.chess" && event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            
            Handler(Looper.getMainLooper()).postDelayed({
                takeScreenshot(Display.DEFAULT_DISPLAY, mainExecutor, object : TakeScreenshotCallback {
                    override fun onSuccess(screenshot: ScreenshotResult) {
                        val bitmap = Bitmap.wrapHardwareBuffer(screenshot.hardwareBuffer, screenshot.colorSpace)
                        processBoard(bitmap, screenshot.hardwareBuffer)
                    }
                    override fun onFailure(errorCode: Int) {
                        // If screenshot fails, run the engine anyway to test the UI
                        processBoard(null, null)
                    }
                })
            }, 500)
        }
    }

    private fun processBoard(bitmap: Bitmap?, buffer: android.hardware.HardwareBuffer?) {
        val boardData = vision.analyze(bitmap ?: Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888))
        buffer?.close()

        if (boardData != null) {
            engine.analyze(boardData.fen) { bestMoves, eval ->
                val parts = bestMoves.split(" ")
                if (parts.size >= 2) {
                    overlay?.updateOverlay(parts[0], parts[1], eval)
                }
            }
        }
    }

    override fun onInterrupt() {}
}