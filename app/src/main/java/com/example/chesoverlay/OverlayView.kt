package com.example.chesoverlay

import android.content.Context
import android.graphics.*
import android.view.View
import android.view.WindowManager

class OverlayView(context: Context) : View(context) {

    private val paintRed = Paint().apply { color = Color.RED; style = Paint.Style.STROKE; strokeWidth = 12f; isAntiAlias = true }
    private val paintBlack = Paint().apply { color = Color.BLACK; style = Paint.Style.FILL; isAntiAlias = true }
    private val paintGray = Paint().apply { color = Color.GRAY; style = Paint.Style.STROKE; strokeWidth = 10f; isAntiAlias = true }
    private val paintText = Paint().apply { color = Color.WHITE; textSize = 60f; isAntiAlias = true; typeface = Typeface.DEFAULT_BOLD }

    private var boardBounds = Rect(100, 500, 1000, 1400)
    private var bestMove = ""
    private var altMove = ""
    private var evalText = ""

    init {
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        )
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.addView(this, params)
    }

    fun updateOverlay(best: String, alt: String, eval: String) {
        this.bestMove = best
        this.altMove = alt
        this.evalText = eval
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (bestMove.isEmpty() || bestMove.length < 4) return

        val squareSize = boardBounds.width() / 8f
        val startSquare = bestMove.substring(0, 2)
        val endSquare = bestMove.substring(2, 4)

        val startX = squareToX(startSquare, squareSize) + squareSize / 2
        val startY = squareToY(startSquare, squareSize) + squareSize / 2
        val endX = squareToX(endSquare, squareSize) + squareSize / 2
        val endY = squareToY(endSquare, squareSize) + squareSize / 2

        canvas.drawLine(startX, startY, endX, endY, paintGray)
        canvas.drawCircle(startX, startY, squareSize * 0.4f, paintRed)
        canvas.drawCircle(endX, endY, squareSize * 0.4f, paintBlack)

        val displayEval = if (evalText.startsWith("-")) evalText else "+$evalText"
        canvas.drawText(displayEval, boardBounds.left.toFloat(), boardBounds.top.toFloat() - 20f, paintText)
        canvas.drawText("Alt: $altMove", boardBounds.left.toFloat(), boardBounds.top.toFloat() - 90f, paintText)
    }

    private fun squareToX(square: String, size: Float): Float {
        return boardBounds.left + ((square[0] - 'a') * size)
    }

    private fun squareToY(square: String, size: Float): Float {
        return boardBounds.top + ((8 - (square[1] - '0')) * size)
    }
}
