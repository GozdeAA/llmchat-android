package com.bbsh.llamachat.helpers

object LlamaBridge {
    init {
        System.loadLibrary("llama-android")
    }

    external fun log_to_android()
    // Model + Context
    external fun backend_init()
    external fun load_model(modelPath: String): Long
    external fun new_context(modelPtr: Long): Long
    external fun free_model(modelPtr: Long)
    external fun free_context(ctxPtr: Long)

    // Chat Runtime
    external fun new_batch(nTokens: Int, embd: Int, nSeqMax: Int): Long
    external fun free_batch(batchPtr: Long)

    external fun new_sampler(): Long
    external fun free_sampler(samplerPtr: Long)

    // Completion
    external fun completion_init(ctxPtr: Long, batchPtr: Long, prompt: String, formatChat: Boolean, maxTokens: Int): Int
    external fun completion_loop(ctxPtr: Long, batchPtr: Long, samplerPtr: Long, maxTokens: Int, intVar: Any): String?

    external fun kv_cache_clear(ctxPtr: Long)

}
