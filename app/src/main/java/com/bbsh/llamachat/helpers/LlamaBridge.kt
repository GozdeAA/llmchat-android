package com.bbsh.llamachat.helpers

object LlamaBridge {
    init {
        System.loadLibrary("llama") // This loads libllama.so
    }

    external fun getLlamaVersion(): String
    external fun sendMessageToLlama(input: String): String
}
