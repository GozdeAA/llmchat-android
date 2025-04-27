package com.bbsh.llamachat.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bbsh.llamachat.models.LlamaModel

data class ChatMessage(val message: String, val isUser: Boolean)

class LlamaChatViewModel : ViewModel() {

    private val _messages = MutableLiveData<List<ChatMessage>>(emptyList())
    val messages: LiveData<List<ChatMessage>> = _messages

    private val llamaModel = LlamaModel()

    fun sendMessage(input: String) {
        val current = _messages.value ?: emptyList()
        _messages.value = current + ChatMessage(input, true)

        // Run LLaMA model in a background thread
        Thread {
            val response = llamaModel.runModel(input)
            _messages.postValue(_messages.value!! + ChatMessage(response, false))
        }.start()
    }
}
