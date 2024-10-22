package com.joviankurnia.carcare

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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

class AddPerawatan : AppCompatActivity() {
    lateinit var buttonReturn: ImageView
    lateinit var buttonGotoMD: Button
    lateinit var buttonGotoVD: Button
    lateinit var textViewAddMaintenance: TextView
    lateinit var prevClass: String
    lateinit var position: String

    lateinit var auth: FirebaseAuth
    lateinit var user: FirebaseUser
    lateinit var db : FirebaseFirestore

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_perawatan)
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        db = Firebase.firestore

        buttonReturn = findViewById(R.id.add_m_return_button)
        buttonGotoMD = findViewById(R.id.add_m_goto_md_button)
        buttonGotoVD = findViewById(R.id.add_m_goto_vd_button)
        textViewAddMaintenance = findViewById(R.id.add_m_goto_add_md_text)
        prevClass = intent.getStringExtra("PREV_CLASS")!!
        position = intent.getStringExtra("POSITION")!!

        buttonReturn.setOnClickListener(View.OnClickListener {
            var intentReturn = Intent(this@AddPerawatan,
            Class.forName(prevClass))
            if(prevClass == "DataKendaraan"){
                intentReturn.putExtra("PREV_CLASS", PilihKendaraan::class.java.name)
            }
            else {
                intentReturn.putExtra("PREV_CLASS", this@AddPerawatan::class.java.name)
            }
            intentReturn.putExtra("POSITION", position)
            startActivity(intentReturn)
            finish()
        })

        buttonGotoMD.setOnClickListener(View.OnClickListener {
            Toast.makeText(applicationContext,
            "Tidak dapat berpindah karena tidak ada data perawatan",
            Toast.LENGTH_SHORT).show()
        })

        buttonGotoVD.setOnClickListener(View.OnClickListener {
            var intentGotoPilihKendaraan = Intent(this@AddPerawatan,
                PilihKendaraan::class.java)
            startActivity(intentGotoPilihKendaraan)
            finish()
        })

        textViewAddMaintenance.setOnClickListener(View.OnClickListener {
            var intentGotoAddMD = Intent(this@AddPerawatan,
            MasukanDataPerawatan::class.java)
            intentGotoAddMD.putExtra("PREV_CLASS", this@AddPerawatan::class.java.name)
            intentGotoAddMD.putExtra("POSITION", position)
            startActivity(intentGotoAddMD)
            finish()
        })

        GlobalScope.launch(Dispatchers.Main){
            val email = user.email
            val isMaintenanceAvailable = email?.let { checkIfMaintenanceAvailable(email) }

            if (isMaintenanceAvailable == true){
                var intentGotoMD = Intent(this@AddPerawatan,
                    UpdateDataPerawatan::class.java)
                intentGotoMD.putExtra("PREV_CLASS",
                    PilihKendaraan::class.java.name)
                intentGotoMD.putExtra("POSITION", position)
                startActivity(intentGotoMD)
                finish()
            }
        }
    }

    private suspend fun checkIfMaintenanceAvailable(email: String): Boolean{
        val querySnapshot = db.collection("maintenances").get().await()
        for (document in querySnapshot.documents) {
            val userEmail = document.getString("email")
            if (email == userEmail) {
                return true
            }
        }
        return false
    }
    /*
    private fun test() {
        val btnGotoMD = findViewById<Button>(R.id.add_m_goto_md_button)
        btnGotoMD.setOnClickListener(View.OnClickListener {
            var intentGotoMD = Intent(this@AddPerawatan,
                AddPerawatan::class.java)
            startActivity(intentGotoMD)
        })

        val btnGotoVD = findViewById<Button>(R.id.add_m_goto_vd_button)
        btnGotoVD.setOnClickListener(View.OnClickListener {
            var intentGotoVD = Intent(this@AddPerawatan,
                PilihKendaraan::class.java)
            startActivity(intentGotoVD)
        })

        val textGotoAddMD = findViewById<TextView>(R.id.add_m_goto_add_md_text)
        textGotoAddMD.setOnClickListener(View.OnClickListener {
            var intentGotoAddMD = Intent(this@AddPerawatan,
                MasukanDataPerawatan::class.java)
            startActivity(intentGotoAddMD)
        })

        val imageReturn = findViewById<ImageView>(R.id.add_m_return_button)
        imageReturn.setOnClickListener(View.OnClickListener {
            var intentGotoCV = Intent(this@AddPerawatan,
                DataKendaraan::class.java)
            startActivity(intentGotoCV)
        })
    }

     */
}