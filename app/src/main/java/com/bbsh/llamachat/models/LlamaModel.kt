package com.bbsh.llamachat.models

class LlamaModel {
    companion object {
        init {
            System.loadLibrary("llama") // Must match your built C++ library name
        }
    }

    external fun runModel(input: String): String
}
