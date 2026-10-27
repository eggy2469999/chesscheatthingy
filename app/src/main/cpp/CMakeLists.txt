cmake_minimum_required(VERSION 3.22.1)
project(chesoverlay)

add_subdirectory(stockfish)

add_library(native-engine SHARED native-engine.cpp)
target_link_libraries(native-engine stockfish android log)
