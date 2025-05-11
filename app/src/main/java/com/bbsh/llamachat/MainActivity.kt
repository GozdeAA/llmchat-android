package com.bbsh.llamachat

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bbsh.llamachat.helpers.LlamaBridge
import com.bbsh.llamachat.helpers.LlamaChatEngine
import com.bbsh.llamachat.viewmodels.LlamaChatViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: LlamaChatViewModel by viewModels()
    private lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = ChatAdapter(emptyList())
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        val inputField = findViewById<EditText>(R.id.inputEditText)
        val sendButton = findViewById<Button>(R.id.sendButton)

        sendButton.setOnClickListener {
            val llama = LlamaChatEngine()
            llama.init("/storage/emulated/0/Download" + "/Llama-3.2-1B-Instruct-Q4_K_M.gguf") // or wherever your model is stored

            val response = llama.sendMessage("What is your name?")
            Log.d("Chat", "AI: $response")

//            val input = inputField.text.toString()
//            if (input.isNotBlank()) {
//                viewModel.sendMessage(input)
//                inputField.text.clear()
//            }
        }

        viewModel.messages.observe(this) {
            adapter.updateMessages(it)
            recyclerView.scrollToPosition(it.size - 1)
        }
    }
}

//Llama-3.2-1B-Instruct-Q4_K_M"

