package com.example.chesoverlay

import okhttp3.*
import java.io.IOException

class StockfishEngine {
    private val client = OkHttpClient()

    fun analyze(fen: String, callback: (String, String) -> Unit) {
        Thread {
            try {
                val url = "https://stockfish.online/api/s/v2.php?fen=${fen.replace(" ", "%20")}&depth=15"
                val request = Request.Builder().url(url).build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val body = response.body?.string() ?: ""
                        val bestMove = extractValue(body, "bestmove") ?: "e2e4"
                        val evaluation = extractValue(body, "evaluation") ?: "0.0"
                        
                        val evalStr = if (evaluation.toDoubleOrNull() != null) {
                            if (evaluation.toDouble() > 0) "+$evaluation" else evaluation
                        } else {
                            evaluation
                        }

                        callback("$bestMove d2d4", evalStr)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                callback("e2e4 d2d4", "+0.0")
            }
        }.start()
    }

    private fun extractValue(json: String, key: String): String? {
        val regex = "\"$key\":\"?([^\",}]+)\"?".toRegex()
        return regex.find(json)?.groupValues?.get(1)
    }
}