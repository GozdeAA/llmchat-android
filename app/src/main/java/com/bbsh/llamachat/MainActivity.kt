package com.bbsh.llamachat

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bbsh.llamachat.helpers.LlamaBridge
import com.bbsh.llamachat.helpers.LlamaChatEngine
import com.bbsh.llamachat.helpers.ModelFileHelper
import com.bbsh.llamachat.viewmodels.LlamaChatViewModel

class MainActivity : AppCompatActivity() {

//    private val viewModel: LlamaChatViewModel by viewModels()
//    private lateinit var adapter: ChatAdapter
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        adapter = ChatAdapter(emptyList())
//        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
//            layoutManager = LinearLayoutManager(this@MainActivity)
//            adapter = this@MainActivity.adapter
//        }
//
//        val inputField = findViewById<EditText>(R.id.inputEditText)
//        val sendButton = findViewById<Button>(R.id.sendButton)
//
//        sendButton.setOnClickListener {
//            val llama = LlamaChatEngine()
//            llama.init("/storage/emulated/0/Download" + "/Llama-3.2-1B-Instruct-Q4_K_M.gguf") // or wherever your model is stored
//
//            val response = llama.sendMessage("What is your name?")
//            Log.d("Chat", "AI: $response")
//
////            val input = inputField.text.toString()
////            if (input.isNotBlank()) {
////                viewModel.sendMessage(input)
////                inputField.text.clear()
////            }
//        }
//
//        viewModel.messages.observe(this) {
//            adapter.updateMessages(it)
//            recyclerView.scrollToPosition(it.size - 1)
//        }
//    }

    private lateinit var modelHelper: ModelFileHelper
    private val llama = LlamaChatEngine()

    private lateinit var chatTextView: TextView
    private lateinit var scrollView: ScrollView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button
    private lateinit var loadModelButton: Button

    private var isModelLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // UI references
        chatTextView = findViewById(R.id.chatTextView)
        scrollView = findViewById(R.id.chatScrollView)
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)
        loadModelButton = findViewById(R.id.loadModelButton)

        sendButton.isEnabled = false // disable until model is loaded

        // Model file picker
        modelHelper = ModelFileHelper(this) { modelPath ->
            llama.init(modelPath)
            isModelLoaded = true
            sendButton.isEnabled = true
            appendMessage("System", "Model loaded and ready!")
        }

        modelHelper.registerPicker(this)

        loadModelButton.setOnClickListener {
            modelHelper.launchPicker()
        }

        sendButton.setOnClickListener {
            val input = messageInput.text.toString().trim()
            if (input.isNotEmpty() && isModelLoaded) {
                appendMessage("You", input)
                messageInput.text.clear()

                val reply = llama.sendMessage(input)
                appendMessage("LLaMA", reply)
            }
        }
    }

    private fun appendMessage(sender: String, message: String) {
        chatTextView.append("\n$sender: $message")
        scrollView.post { scrollView.fullScroll(View.FOCUS_DOWN) }
    }

    override fun onDestroy() {
        llama.release()
        super.onDestroy()
    }

}

//Llama-3.2-1B-Instruct-Q4_K_M"

