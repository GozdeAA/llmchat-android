//
// Created by Gözde Aydin on 25.04.2025.
//
#include <jni.h>
#include <string>

// Example version - you can change this however you want
std::string get_llama_version() {
    return "Llama.cpp Android Build v1.0"; // or dynamically get version if you have
}

// Stub for now - later this will call the actual llama.cpp logic
std::string generate_response(const std::string &input) {
    return "Echo: " + input; // Replace this with llama model inference later
}

// JNI bridge
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

    std::string output = generate_response(userInput);  // Here you’ll integrate llama.cpp later

    return env->NewStringUTF(output.c_str());
}