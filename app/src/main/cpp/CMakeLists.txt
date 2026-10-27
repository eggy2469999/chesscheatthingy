cmake_minimum_required(VERSION 3.22.1)
project(chesoverlay)

add_library(native-engine SHARED native-engine.cpp)

file(GLOB_RECURSE STOCKFISH_SOURCES "stockfish/src/*.cpp")
list(FILTER STOCKFISH_SOURCES EXCLUDE REGEX ".*/universal/.*")

target_sources(native-engine PRIVATE ${STOCKFISH_SOURCES})
target_include_directories(native-engine PRIVATE "stockfish/src")
target_link_libraries(native-engine android log)