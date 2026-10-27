package com.example.chesoverlay

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect

data class BoardData(val fen: String, val bounds: Rect)

class BoardVision {
    fun analyze(bitmap: Bitmap, callback: (BoardData?) -> Unit) {
        val boardBounds = Rect(100, 500, 1000, 1400)
        val squareSize = boardBounds.width() / 8f

        if (bitmap.width < boardBounds.right || bitmap.height < boardBounds.bottom) {
            callback(BoardData("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", boardBounds))
            return
        }

        val fenBuilder = StringBuilder()
        
        for (rank in 0..7) {
            var emptyCount = 0
            for (file in 0..7) {
                val x = boardBounds.left + (file * squareSize).toInt() + (squareSize / 2).toInt()
                val y = boardBounds.top + (rank * squareSize).toInt() + (squareSize / 2).toInt()
                
                val pixelColor = bitmap.getPixel(x, y)
                val r = Color.red(pixelColor)
                val g = Color.green(pixelColor)
                val b = Color.blue(pixelColor)
                
                if (r > 200 && g > 200 && b > 200) {
                    if (emptyCount > 0) { fenBuilder.append(emptyCount); emptyCount = 0 }
                    fenBuilder.append("P") 
                } else if (r < 50 && g < 50 && b < 50) {
                    if (emptyCount > 0) { fenBuilder.append(emptyCount); emptyCount = 0 }
                    fenBuilder.append("p")
                } else {
                    emptyCount++
                }
            }
            if (emptyCount > 0) fenBuilder.append(emptyCount)
            if (rank < 7) fenBuilder.append("/")
        }
        
        fenBuilder.append(" w - - 0 1")
        callback(BoardData(fenBuilder.toString(), boardBounds))
    }
}