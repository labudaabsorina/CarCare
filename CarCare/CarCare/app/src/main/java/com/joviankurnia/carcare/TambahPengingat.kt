package com.joviankurnia.carcare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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

class TambahPengingat : AppCompatActivity() {
    lateinit var editTextTypeNotif : EditText
    lateinit var editTextDatesNotif : EditText
    lateinit var buttonSaveNotif : Button
    lateinit var imageViewNotif : ImageView
    lateinit var buttonReturn : Button

    lateinit var auth: FirebaseAuth
    lateinit var user: FirebaseUser
    lateinit var db : FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_pengingat)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        db = Firebase.firestore

        editTextTypeNotif = findViewById(R.id.add_n_type_notif_input)
        editTextDatesNotif = findViewById(R.id.add_n_dates_notif_input)
        buttonSaveNotif = findViewById(R.id.add_n_save_n_button)
        imageViewNotif = findViewById(R.id.add_n_goto_notif_image)
        buttonReturn = findViewById(R.id.add_n_return_button)

        buttonReturn.setOnClickListener(View.OnClickListener {
            var intentReturn = Intent(this@TambahPengingat,
            PilihKendaraan::class.java)
            startActivity(intentReturn)
            finish()
        })

        imageViewNotif.setOnClickListener(View.OnClickListener {
            var intentGotoNotif = Intent(this@TambahPengingat,
            LiatPengingat::class.java)
            intentGotoNotif.putExtra("PREV_CLASS", this@TambahPengingat::class.java.name)
            startActivity(intentGotoNotif)
            finish()
        })

        buttonSaveNotif.setOnClickListener(View.OnClickListener {
            registerNotif()
        })

    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun registerNotif(){
        val typeNotif = editTextTypeNotif.text.toString()
        val datesNotif = editTextDatesNotif.text.toString()
        val email = user.email

        if(TextUtils.isEmpty(typeNotif)){
            Toast.makeText(applicationContext,
            "Masukkan Jenis Perawatan",
            Toast.LENGTH_SHORT).show()
            return
        }

        if(TextUtils.isEmpty(datesNotif)){
            Toast.makeText(applicationContext,
            "Masukkan Tanggal Pengingat",
            Toast.LENGTH_SHORT).show()
            return
        }

        GlobalScope.launch(Dispatchers.Main) {
            val isMaxNotif = email?.let { checkIfMaxNotif(it) }

            val noteNotif: String = when(email?.let { countNumberNotif(it) }){
                0 -> "Pertama"
                1 -> "Kedua"
                else -> {
                    "Melebihi jumlah data"
                }
            }

            if (isMaxNotif == true){
                Toast.makeText(applicationContext,
                "Data notifikasi sudah penuh",
                Toast.LENGTH_SHORT).show()
            }
            else {
                val insertNotif = hashMapOf(
                    "email" to email,
                    "jenis" to typeNotif,
                    "tanggal" to datesNotif,
                    "catatan" to noteNotif
                )

                db.collection("notifications")
                    .add(insertNotif)
                    .addOnSuccessListener {
                        Toast.makeText(applicationContext,
                        "Data pengingat berhasil ditambahkan",
                        Toast.LENGTH_SHORT).show()

                        editTextDatesNotif.text.clear()
                        editTextTypeNotif.text.clear()

                        var intentGotoVehicle = Intent(this@TambahPengingat,
                        PilihKendaraan::class.java)

                    }
            }
        }
    }

    private suspend fun checkIfMaxNotif(email: String): Boolean{
        val querySnapshot = db.collection("notifications").get().await()
        val maxNotif = 2
        var numNotif = 0
        for (document in querySnapshot.documents){
            val userEmail = document.getString("email")
            if(email == userEmail){
                numNotif++
            }
        }
        return numNotif >= maxNotif
    }

    private suspend fun countNumberNotif(email:String): Int{
        val querySnapshot = db.collection("notifications").get().await()
        var numNotif = 0
        for (document in querySnapshot.documents){
            val userEmail = document.getString("email")
            if(email == userEmail){
                numNotif++
            }
        }
        return numNotif
    }
}