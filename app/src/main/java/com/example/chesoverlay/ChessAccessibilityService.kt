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
        // Trigger when chess.com is active and the screen changes
        if (event.packageName == "com.chess" && event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            
            Handler(Looper.getMainLooper()).postDelayed({
                takeScreenshot(Display.DEFAULT_DISPLAY, mainExecutor, object : TakeScreenshotCallback {
                    override fun onSuccess(screenshot: ScreenshotResult) {
                        val bitmap = Bitmap.wrapHardwareBuffer(screenshot.hardwareBuffer, screenshot.colorSpace)
                        processBoard(bitmap, screenshot.hardwareBuffer)
                    }
                    override fun onFailure(errorCode: Int) {
                        // Fallback: create a dummy bitmap so the app doesn't crash
                        val dummyBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
                        processBoard(dummyBitmap, null)
                    }
                })
            }, 500)
        }
    }

    private fun processBoard(bitmap: Bitmap, buffer: android.hardware.HardwareBuffer?) {
        // 1. Vision runs on the bitmap
        val boardData = vision.analyze(bitmap)
        
        // Close the hardware buffer to free memory
        if (buffer != null) {
            buffer.close()
        }

        // 2. Engine analyzes the FEN and calls back
        if (boardData != null) {
            engine.analyze(boardData.fen) { bestMoves, eval ->
                val parts = bestMoves.split(" ")
                if (parts.size >= 2) {
                    // 3. Overlay draws the result
                    overlay?.updateOverlay(parts[0], parts[1], eval)
                }
            }
        }
    }

    override fun onInterrupt() {}
}