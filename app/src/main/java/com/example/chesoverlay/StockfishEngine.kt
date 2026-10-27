package com.example.chesoverlay

class StockfishEngine {
    init {
        System.loadLibrary("native-engine")
    }

    private external fun getBestMove(fen: String, depth: Int): String

    fun analyze(fen: String, callback: (String, String) -> Unit) {
        Thread {
            try {
                val result = getBestMove(fen, 15)
                val parts = result.split(" ")
                if (parts.size >= 2) {
                    val best = parts[0]
                    val alt = parts.getOrElse(1) { "" }
                    val eval = parts.getOrElse(2) { "0.0" }
                    callback("$best $alt", eval)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }
}
