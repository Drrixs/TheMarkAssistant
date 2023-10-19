package com.drrixs.themarkassistant

import android.content.Context
import java.io.*

class ChatHistoryManager(private val context: Context) {
    private val fileName = "chat_history.txt"

    fun saveChatHistory(chatMessages: List<String>) {
        try {
            val fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            val writer = BufferedWriter(OutputStreamWriter(fileOutputStream))

            for (message in chatMessages) {
                writer.write(message)
                writer.newLine()
            }

            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadChatHistory(): List<String> {
        val chatMessages = ArrayList<String>()

        try {
            val fileInputStream = context.openFileInput(fileName)
            val reader = BufferedReader(InputStreamReader(fileInputStream))
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                chatMessages.add(line ?: "")
            }

            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return chatMessages
    }
}
