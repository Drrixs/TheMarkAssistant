package com.drrixs.themarkassistant

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonOk = findViewById<Button>(R.id.buttonOk)
        val buttonSettings = findViewById<Button>(R.id.buttonSettings)
        buttonOk.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        })
        buttonSettings.setOnClickListener {
            // Создаем Intent для перехода на активити настроек
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}
