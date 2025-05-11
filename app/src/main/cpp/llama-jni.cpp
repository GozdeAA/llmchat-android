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

std::string generate_response(const std::string &input) {
    if (!g_ctx) {
        return "LLaMA model is not initialized.";
    }

    // Get model and vocab
    llama_model* model = llama_get_model(g_ctx.get());
    const llama_vocab* vocab = llama_model_get_vocab(model);

    // Step 1: Count tokens
    int n_tokens = llama_tokenize(
            vocab,
            input.c_str(),
            static_cast<int32_t>(input.length()),
            nullptr,         // no output array yet
            0,               // n_tokens_max = 0 for counting
            true,            // add_special
            true             // parse_special
    );

    if (n_tokens <= 0) {
        return "Failed to tokenize input.";
    }

    // Step 2: Actually tokenize
    std::vector<llama_token> tokens(n_tokens);
    int n_tokens_final = llama_tokenize(
            vocab,
            input.c_str(),
            static_cast<int32_t>(input.length()),
            tokens.data(),
            tokens.size(),
            true,
            true
    );

    if (n_tokens_final != n_tokens) {
        return "Tokenization mismatch.";
    }

    // Step 3: Prepare and decode batch
    llama_batch batch = llama_batch_init(512, 0, 1); // 512 tokens max

    batch.n_tokens = tokens.size();
    for (int i = 0; i < tokens.size(); ++i) {
        batch.token[i] = tokens[i];
        batch.pos[i] = i;
        batch.seq_id[i] = 0;
    }

    llama_decode(g_ctx.get(), batch);

    // Step 4: Generate output
    std::string result;
    for (int i = 0; i < 50; ++i) {
        llama_token new_token = llama_sample_token(g_ctx.get(), nullptr);

        if (new_token == llama_token_eos(g_ctx.get())) {
            break;
        }

        const char* piece = llama_token_to_piece(g_ctx.get(), new_token);
        if (piece) {
            result += piece;
        }

        // Feed the new token back to the model
        llama_batch next_batch = llama_batch_init(1, 0, 1);
        next_batch.n_tokens = 1;
        next_batch.token[0] = new_token;
        next_batch.pos[0] = i + tokens.size();
        next_batch.seq_id[0] = 0;

        llama_decode(g_ctx.get(), next_batch);
    }

    return result;
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
