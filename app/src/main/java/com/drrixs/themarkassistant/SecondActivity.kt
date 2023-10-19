package com.drrixs.themarkassistant

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import android.util.Log
import android.widget.ListView
import org.json.JSONObject
import androidx.recyclerview.widget.RecyclerView



class SecondActivity : AppCompatActivity() {
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var chatListView: ListView
    private val chatMessages = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private val questionsAndAnswers = HashMap<String, String>()
    private lateinit var chatHistoryManager: ChatHistoryManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)






        chatHistoryManager = ChatHistoryManager(this)

        // Загрузка истории чата
        chatMessages.addAll(chatHistoryManager.loadChatHistory())



        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)
        chatListView = findViewById(R.id.chatListView)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, chatMessages)
        chatListView.adapter = adapter

        loadQuestionsAndAnswersFromFile("questions_answers.json") // Загружаем из JSON-файла

        // Проверяем наличие дополнительной информации в интенте
        if (intent.getBooleanExtra("clearChat", false)) {
            clearChat()
        }

        sendButton.setOnClickListener(View.OnClickListener {
            val message = messageEditText.text.toString().trim()
            if (message.isNotEmpty()) {
                chatMessages.add("Вы: $message")

                // Получить ответ бота
                val botResponse = getBotResponse(message)

                chatMessages.add("Бот: $botResponse")
                adapter.notifyDataSetChanged()
                messageEditText.text.clear()

                // Сохранить историю чата после каждого сообщения
                chatHistoryManager.saveChatHistory(chatMessages)
            }
        })



        messageEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Не нужно реализовывать
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Включаем или отключаем кнопку в зависимости от наличия текста
                val text = s.toString().trim()
                sendButton.isEnabled = text.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {
                // Не нужно реализовывать
            }
        })

    }


    private fun loadQuestionsAndAnswersFromFile(fileName: String) {
        try {
            val inputStream = resources.openRawResource(R.raw.questions_answers)
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            val jsonString = bufferedReader.readText()
            val jsonObject = JSONObject(jsonString)

            for (key in jsonObject.keys()) {
                questionsAndAnswers[key] = jsonObject.getString(key)
            }

            inputStreamReader.close()
            inputStream.close()
        } catch (e: Exception) {
            Log.e("SecondActivity", "Ошибка при чтении файла: ${e.message}")
            e.printStackTrace()
        }
        // Логируем количество вопросов и ответов
        Log.d("SecondActivity", "Загружено ${questionsAndAnswers.size} вопросов и ответов")
    }

    private fun getBotResponse(question: String): String {
        val trimmedQuestion = question.trim().toLowerCase()

        // Проверка на математическую операцию
        if (containsMathOperator(trimmedQuestion)) {
            try {
                val result = evaluateMathExpression(trimmedQuestion)
                return "Результат: $result"
            } catch (e: Exception) {
                return "Произошла ошибка при выполнении математической операции."
            }
        }

        // Поиск наилучшего соответствия в базе вопросов и ответов
        var bestMatch: String? = null
        var bestMatchDistance = Int.MAX_VALUE

        for (key in questionsAndAnswers.keys) {
            val distance = calculateHammingDistance(trimmedQuestion, key)
            if (distance < bestMatchDistance) {
                bestMatchDistance = distance
                bestMatch = key
            }
        }

        // Установите порог на 1/3 длины вопроса вместо половины
        val questionLength = trimmedQuestion.length
        val threshold = questionLength / 3

        return if (bestMatchDistance <= threshold) {
            questionsAndAnswers[bestMatch] ?: "Извините, я не понимаю ваш вопрос."
        } else {
            "Извините, я не понимаю ваш вопрос."
        }
    }


    private fun containsMathOperator(input: String): Boolean {
        return input.contains("+") || input.contains("-") || input.contains("*") || input.contains("/")
    }

    private fun evaluateMathExpression(expression: String): Double {
        // Разделите выражение на операнды и оператор
        val operator = findMathOperator(expression)
        val operands = expression.split(operator)

        if (operands.size != 2) {
            throw IllegalArgumentException("Недопустимое математическое выражение")
        }

        val operand1 = operands[0].toDouble()
        val operand2 = operands[1].toDouble()

        return when (operator) {
            "+" -> operand1 + operand2
            "-" -> operand1 - operand2
            "*" -> operand1 * operand2
            "/" -> operand1 / operand2
            else -> throw IllegalArgumentException("Недопустимая математическая операция")
        }
    }

    private fun findMathOperator(expression: String): String {
        if (expression.contains("+")) {
            return "+"
        } else if (expression.contains("-")) {
            return "-"
        } else if (expression.contains("*")) {
            return "*"
        } else if (expression.contains("/")) {
            return "/"
        } else {
            throw IllegalArgumentException("Математический оператор не найден")
        }
    }


    private fun calculateHammingDistance(input: String, target: String): Int {
        // Проверяем, что строки имеют одинаковую длину
        if (input.length != target.length) {
            return Int.MAX_VALUE
        }

        var distance = 0

        for (i in input.indices) {
            if (input[i] != target[i]) {
                distance++
            }
        }

        return distance
    }


    // Метод для очистки чата
    private fun clearChat() {
        chatMessages.clear()
        adapter.notifyDataSetChanged()
    }




}
