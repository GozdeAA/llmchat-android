//
// Created by Gözde Aydin on 25.04.2025.
//

#include <jni.h>
#include <string>
#include <vector>
#include <memory>
#include "llama.h"

// Global context pointer
std::unique_ptr<llama_context, decltype(&llama_free)> g_ctx(nullptr, llama_free);

// Example version function
std::string get_llama_version() {
    return "Llama.cpp Android Build v1.0"; // You can make it dynamic later
}

bool init_llama_model(const char* model_path) {
    llama_backend_init(); // must call once

    llama_model_params model_params = llama_model_default_params();
    llama_model* model = llama_load_model_from_file(model_path, model_params);

    if (!model) {
        return false;
    }

    llama_context_params ctx_params = llama_context_default_params();
    g_ctx.reset(llama_new_context_with_model(model, ctx_params));

    return g_ctx != nullptr;
}

// JNI Bridge Functions
extern "C"
JNIEXPORT jstring JNICALL
Java_com_bbsh_llamachat_helpers_LlamaBridge_getLlamaVersion(JNIEnv *env, jobject thiz) {
    std::string version = get_llama_version();
    return env->NewStringUTF(version.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_bbsh_llamachat_helpers_LlamaBridge_sendMessageToLlama(JNIEnv *env, jobject thiz,
                                                               jstring input) {
    const char *inputChars = env->GetStringUTFChars(input, nullptr);
    std::string userInput(inputChars);
    env->ReleaseStringUTFChars(input, inputChars);

    std::string output = generate_response(userInput);

    return env->NewStringUTF(output.c_str());
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_bbsh_llamachat_helpers_LlamaBridge_initLlamaModel(JNIEnv *env, jobject /* this */,
                                                           jstring modelPath) {
    const char *modelPathCStr = env->GetStringUTFChars(modelPath, nullptr);
    bool success = init_llama_model(modelPathCStr);
    env->ReleaseStringUTFChars(modelPath, modelPathCStr);
    return success ? JNI_TRUE : JNI_FALSE;
}
