cmake_minimum_required(VERSION 3.10.2)

project("llama")

# Create the llama library (STATIC or SHARED depending on your needs)
add_library(
        llama SHARED
        llama.cpp
        llama-adapter.cpp
        llama-arch.cpp
        llama-batch.cpp
        llama-chat.cpp
        llama-context.cpp
        llama-grammar.cpp
        llama-graph.cpp
        llama-hparams.cpp
        llama-impl.cpp
        llama-io.cpp
        llama-kv-cache.cpp
        llama-memory.cpp
        llama-mmap.cpp
        llama-model-loader.cpp
        llama-model.cpp
        llama-quant.cpp
        llama-sampling.cpp
        llama-vocab.cpp
        llama-jni.cpp
        unicode-data.cpp
        unicode.cpp
)

# Add ggml as a library
add_library(
        ggml STATIC
        ggml/ggml.c
        ggml/ggml-backend.cpp
        ggml/ggml-alloc.c
        ggml/gguf.cpp
        ggml/ggml-threading.cpp
        ggml/ggml-quants.c
        ggml/ggml-backend-reg.cpp
)


# Include directories (if your includes are in ../include, this is important)
target_include_directories(llama PUBLIC
        ${CMAKE_CURRENT_SOURCE_DIR}
        ${CMAKE_CURRENT_SOURCE_DIR}/../include
)

# ggml include directories
target_include_directories(ggml PUBLIC
        ${CMAKE_CURRENT_SOURCE_DIR}/../ggml
)

# llama include directories
target_include_directories(llama PUBLIC
        ${CMAKE_CURRENT_SOURCE_DIR}
        ${CMAKE_CURRENT_SOURCE_DIR}/../include
        ${CMAKE_CURRENT_SOURCE_DIR}/../ggml
)

target_link_libraries(llama PUBLIC ggml)

# Enable C++17
target_compile_features(llama PUBLIC cxx_std_17)

# Optional: position-independent code for shared libs (only needed if SHARED)
set_target_properties(llama PROPERTIES POSITION_INDEPENDENT_CODE ON)

# Definitions for shared builds
target_compile_definitions(llama PRIVATE LLAMA_BUILD)
target_compile_definitions(llama PUBLIC LLAMA_SHARED)
