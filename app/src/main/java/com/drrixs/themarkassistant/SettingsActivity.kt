package com.drrixs.themarkassistant
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.media.MediaPlayer




class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settingsss)

        // Для очистки чата
        val clearChatButton: Button = findViewById(R.id.button677)

        clearChatButton.setOnClickListener {
            // Создаем интент для перехода на SecondActivity
            val intent = Intent(this, SecondActivity::class.java)

            // Добавляем дополнительную информацию в интент, чтобы указать, что нужно очистить чат
            intent.putExtra("clearChat", true)

            // Запускаем SecondActivity
            startActivity(intent)
        }


        val playMusicButton: Button = findViewById(R.id.playMusic)
        playMusicButton.setOnClickListener {
            val intent = Intent(this, MusicService::class.java)
            startService(intent)
        }

    }






}


