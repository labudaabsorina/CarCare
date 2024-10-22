package com.joviankurnia.carcare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnNext = findViewById<Button>(R.id.intro_next_button)
        btnNext.setOnClickListener(View.OnClickListener {
            var intentNext = Intent(this@MainActivity,
                Login::class.java)
            startActivity(intentNext)
        })
    }
}