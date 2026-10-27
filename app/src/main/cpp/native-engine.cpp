#include <jni.h>
#include <string>
#include <sstream>
#include <vector>
#include <thread>
#include <mutex>
#include "stockfish/src/bitboard.h"
#include "stockfish/src/position.h"
#include "stockfish/src/uci.h"
#include "stockfish/src/engine.h"

std::mutex engine_mutex;
bool engine_initialized = false;

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_chesoverlay_StockfishEngine_getBestMove(JNIEnv *env, jobject, jstring fenString, jint depth) {
    const char *fen = env->GetStringUTFChars(fenString, 0);
    std::string fenStr(fen);
    env->ReleaseStringUTFChars(fenString, fen);

    std::lock_guard<std::mutex> lock(engine_mutex);

    // Initialize Stockfish once
    if (!engine_initialized) {
        Stockfish::Bitboards::init();
        Stockfish::Position::init();
        engine_initialized = true;
    }

    // Set up the position
    Stockfish::Position pos;
    Stockfish::StateInfo si;
    pos.set(fenStr, false, &si, nullptr);

    // Set up the engine options
    Stockfish::Engine eng;
    
    // Run search
    Stockfish::Search::LimitsType limits;
    limits.depth = depth;
    
    Stockfish::Search::Worker worker;
    worker.start_searching(pos, limits);

    // Extract top moves
    std::string best = "e2e4";
    std::string alt = "d2d4";
    std::string eval = "+0.0";

    // Stockfish returns RootMoves. Get the top 2.
    auto& rootMoves = worker.rootMoves;
    if (rootMoves.size() > 0) {
        best = Stockfish::UCI::move(rootMoves[0].pv[0], false);
        int score = rootMoves[0].score;
        eval = (score > 0 ? "+" : "") + std::to_string(score / 100.0);
    }
    if (rootMoves.size() > 1) {
        alt = Stockfish::UCI::move(rootMoves[1].pv[0], false);
    }

    std::string result = best + " " + alt + " " + eval;
    return env->NewStringUTF(result.c_str());
}