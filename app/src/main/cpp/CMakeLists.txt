cmake_minimum_required(VERSION 3.22.1)
project(chesoverlay)

# Don't use add_subdirectory. We compile the bridge directly.
add_library(native-engine SHARED native-engine.cpp)

# Find the prebuilt stockfish source files and compile them directly into our lib
file(GLOB STOCKFISH_SOURCES "stockfish/src/*.cpp")
target_sources(native-engine PRIVATE ${STOCKFISH_SOURCES})

# Include the directories
target_include_directories(native-engine PRIVATE "stockfish/src")

# Link android and log libraries
target_link_libraries(native-engine android log)