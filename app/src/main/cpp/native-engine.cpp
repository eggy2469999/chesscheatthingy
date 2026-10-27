#include <jni.h>
#include <string>
#include <sstream>
#include <iostream>
#include "stockfish/src/uci.h"
#include "stockfish/src/position.h"

// Stockfish defines these globally in uci.cpp, not in the namespace.
namespace Stockfish {
    class OptionsMap;
    extern OptionsMap Options;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_chesoverlay_StockfishEngine_getBestMove(JNIEnv *env, jobject, jstring fenString, jint depth) {
    const char *fen = env->GetStringUTFChars(fenString, 0);
    std::string fenStr(fen);
    env->ReleaseStringUTFChars(fenString, fen);

    // Use the correct namespace and engine init
    Stockfish::UCIEngine::init(Stockfish::Options);
    
    Stockfish::Position pos;
    Stockfish::StateInfo si;
    pos.set(fenStr, false, &si);

    // Capture output
    std::stringstream buffer;
    std::streambuf* oldCout = std::cout.rdbuf(buffer.rdbuf());
    
    // Send the command using the correct namespace
    Stockfish::UCIEngine::command("go depth " + std::to_string(depth));
    
    // Restore
    std::cout.rdbuf(oldCout);

    // Parse output
    std::string best = "e2e4";
    std::string alt = "d2d4";
    std::string eval = "+0.0";
    
    std::stringstream ss(buffer.str());
    std::string line;
    while (std::getline(ss, line)) {
        if (line.find("score cp") != std::string::npos) {
            size_t pos = line.find("score cp") + 9;
            std::string scoreStr = line.substr(pos, line.find(" ", pos) - pos);
            eval = (std::stoi(scoreStr) > 0 ? "+" : "") + std::to_string(std::stoi(scoreStr) / 100.0);
        }
        if (line.find("bestmove") != std::string::npos) {
            best = line.substr(9, 4);
            size_t pvPos = line.find(" pv ");
            if (pvPos != std::string::npos) {
                alt = line.substr(pvPos + 4, 4);
            }
        }
    }

    std::string result = best + " " + alt + " " + eval;
    return env->NewStringUTF(result.c_str());
}