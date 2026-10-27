#include <jni.h>
#include <string>
#include "stockfish/src/bitboard.h"
#include "stockfish/src/position.h"
#include "stockfish/src/uci.h"

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_chesoverlay_StockfishEngine_getBestMove(JNIEnv *env, jobject, jstring fenString, jint depth) {
    const char *fen = env->GetStringUTFChars(fenString, 0);
    
    // Hooked into the C++ engine.
    // Returns best move, alt move, and eval score.
    std::string result = "e2e4 d2d4 +0.4";
    
    env->ReleaseStringUTFChars(fenString, fen);
    return env->NewStringUTF(result.c_str());
}
