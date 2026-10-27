package com.example.chesoverlay

class StockfishEngine {
    init {
        System.loadLibrary("stockfish")
    }

    // Native methods provided by the prebuilt .so
    private external fun nativeInit()
    private external fun nativeCommand(cmd: String)
    private external fun nativeReadOutput(): String

    fun analyze(fen: String, callback: (String, String) -> Unit) {
        Thread {
            try {
                nativeInit()
                nativeCommand("uci")
                nativeCommand("isready")
                nativeCommand("position fen $fen")
                nativeCommand("go depth 15")
                
                // Let the engine think
                Thread.sleep(2000)
                
                val output = nativeReadOutput()
                // Parse output for bestmove and info score
                var best = "e2e4"
                var alt = "d2d4"
                var eval = "+0.0"

                val lines = output.split("\n")
                for (line in lines) {
                    if (line.startsWith("info depth 15")) {
                        // Parse score
                        val scoreRegex = Regex("score cp (-?\\d+)")
                        val match = scoreRegex.find(line)
                        if (match != null) {
                            val cp = match.groupValues[1].toInt()
                            eval = if (cp > 0) "+${cp / 100.0}" else "${cp / 100.0}"
                        }
                    }
                    if (line.startsWith("bestmove")) {
                        val parts = line.split(" ")
                        if (parts.size > 1) {
                            best = parts[1]
                        }
                    }
                }
                
                callback("$best $alt", eval)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }
}