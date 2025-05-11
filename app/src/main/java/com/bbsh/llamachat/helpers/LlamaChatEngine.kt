package com.bbsh.llamachat.helpers

class LlamaChatEngine {

    private var modelPtr: Long = 0
    private var ctxPtr: Long = 0
    private var batchPtr: Long = 0
    private var samplerPtr: Long = 0
    private var intCounter = object {
        var i = 0
        fun getValue(): Int = i
        fun inc() { i++ }
    }

    fun init(modelPath: String) {

        LlamaBridge.backend_init()
        modelPtr = LlamaBridge.load_model(modelPath)
        ctxPtr = LlamaBridge.new_context(modelPtr)
        batchPtr = LlamaBridge.new_batch(512, 0, 1)
        samplerPtr = LlamaBridge.new_sampler()
    }

    fun sendMessage(prompt: String): String {
        intCounter.i = 0

        val maxTokens = 64
        LlamaBridge.completion_init(ctxPtr, batchPtr, prompt, false, maxTokens)

        val sb = StringBuilder()
        for (i in 0 until maxTokens) {
            val token = LlamaBridge.completion_loop(ctxPtr, batchPtr, samplerPtr, maxTokens, intCounter)
                ?: break
            sb.append(token)
        }

        return sb.toString()
    }

    fun release() {
        LlamaBridge.free_sampler(samplerPtr)
        LlamaBridge.free_batch(batchPtr)
        LlamaBridge.free_context(ctxPtr)
        LlamaBridge.free_model(modelPtr)
    }
}
