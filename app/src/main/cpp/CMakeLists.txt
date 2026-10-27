cmake_minimum_required(VERSION 3.22.1)
project(chesoverlay)

add_library(native-engine SHARED native-engine.cpp)

# Grab every .cpp file in the stockfish/src directory and all its subdirectories
file(GLOB_RECURSE STOCKFISH_SOURCES "stockfish/src/*.cpp")
target_sources(native-engine PRIVATE ${STOCKFISH_SOURCES})

# Include the directories recursively
target_include_directories(native-engine PRIVATE "stockfish/src")

target_link_libraries(native-engine android log)