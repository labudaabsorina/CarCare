package com.joviankurnia.carcare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TambahKendaraan : AppCompatActivity() {

    lateinit var userText : TextView
    lateinit var auth : FirebaseAuth
    lateinit var user : FirebaseUser
    lateinit var db : FirebaseFirestore

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_kendaraan)
        auth = FirebaseAuth.getInstance()
        userText = findViewById(R.id.addvehicle_greetings_text)
        user = auth.currentUser!!
        db = Firebase.firestore
        val userName = user.displayName
        userText.text = "Halo, " + userName

        val textGotoRegisterVehicle = findViewById<TextView>(R.id.addvehicle_goto_registervehicle)
        textGotoRegisterVehicle.setOnClickListener(View.OnClickListener {
            var intentGotoRegisterVehicle = Intent(this@TambahKendaraan,
            MasukkanKendaraan::class.java)
            intentGotoRegisterVehicle.putExtra("PREV_CLASS", this@TambahKendaraan::class.java.name)
            startActivity(intentGotoRegisterVehicle)
            finish()
        })

        val imageGotoLogout = findViewById<ImageView>(R.id.addvehicle_goto_logout)
        imageGotoLogout.setOnClickListener(View.OnClickListener {
            var intentGotoLogout = Intent(this@TambahKendaraan,
            Logout::class.java)
            intentGotoLogout.putExtra("PREV_CLASS", this@TambahKendaraan::class.java.name)
            startActivity(intentGotoLogout)
            finish()
        })

        GlobalScope.launch(Dispatchers.Main){
            val email = user.email
            val isVehicleAvailable = email?.let { checkIfVehicleAvailable(email) }

            if (isVehicleAvailable == true){
                var intentGotoPilihKendaraan = Intent(this@TambahKendaraan,
                    PilihKendaraan::class.java)
                startActivity(intentGotoPilihKendaraan)
                finish()
            }
        }
    }

    private suspend fun checkIfVehicleAvailable(email: String): Boolean{
        val querySnapshot = db.collection("vehicles").get().await()
        for (document in querySnapshot.documents) {
            val userEmail = document.getString("email")
            if (email == userEmail) {
                return true
            }
        }
        return false
    }
}