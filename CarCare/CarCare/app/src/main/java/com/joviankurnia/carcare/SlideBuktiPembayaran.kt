package com.joviankurnia.carcare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView

class SlideBuktiPembayaran : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slide_bukti_pembayaran)

        val btnSpReturn = findViewById<ImageView>(R.id.payment_slide_return)
        btnSpReturn.setOnClickListener(View.OnClickListener {
            var intentReturn = Intent(this@SlideBuktiPembayaran,
            DataPerawatan::class.java)
            startActivity(intentReturn)
        })

        val btnGotoMd = findViewById<Button>(R.id.payment_slide_goto_md_button)
        btnGotoMd.setOnClickListener(View.OnClickListener {
            var intentGotoMd = Intent(this@SlideBuktiPembayaran,
            UpdateDataPerawatan::class.java)
            startActivity(intentGotoMd)
        })

        val btnGotoVd = findViewById<Button>(R.id.payment_slide_goto_vd_button)
        btnGotoVd.setOnClickListener(View.OnClickListener {
            var intentGotoVd = Intent(this@SlideBuktiPembayaran,
            PilihKendaraan::class.java)
            startActivity(intentGotoVd)
        })
    }
}