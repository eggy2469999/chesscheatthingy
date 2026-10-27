#include <jni.h>
#include <string>
#include <sstream>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_chesoverlay_StockfishEngine_getBestMove(JNIEnv *env, jobject, jstring fenString, jint depth) {
    const char *fen = env->GetStringUTFChars(fenString, 0);
    std::string fenStr(fen);
    env->ReleaseStringUTFChars(fenString, fen);

    // A simple logic check to prove it's reading the FEN.
    // If the board is the starting position, play e2e4. Otherwise, play d2d4.
    std::string best = "e2e4";
    std::string alt = "d2d4";
    std::string eval = "+0.4";

    if (fenStr.find("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR") == std::string::npos) {
        // Board is different, return a different move
        best = "g1f3";
        alt = "b1c3";
        eval = "+0.2";
    }

    std::string result = best + " " + alt + " " + eval;
    return env->NewStringUTF(result.c_str());
}