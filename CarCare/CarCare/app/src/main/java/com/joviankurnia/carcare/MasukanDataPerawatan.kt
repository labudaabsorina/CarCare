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

class MasukanDataPerawatan : AppCompatActivity() {
    lateinit var buttonGotoMD: Button
    lateinit var buttonGotoVD: Button
    lateinit var buttonReturn: Button
    lateinit var buttonSave: Button
    lateinit var editTextTypeInput: EditText
    lateinit var editTextDatesInput: EditText
    lateinit var editTextPriceInput: EditText
    lateinit var editTextNotesInput: EditText
    lateinit var prevClass: String
    lateinit var position: String

    lateinit var auth: FirebaseAuth
    lateinit var user: FirebaseUser
    lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_masukan_data_perawatan)
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        db = Firebase.firestore

        buttonGotoMD = findViewById(R.id.input_md_goto_md_button)
        buttonGotoVD = findViewById(R.id.input_md_goto_vd_button)
        buttonReturn = findViewById(R.id.input_md_return_button)
        buttonSave = findViewById(R.id.input_md_save_button)
        editTextTypeInput = findViewById(R.id.input_md_type_input)
        editTextDatesInput = findViewById(R.id.input_md_dates_input)
        editTextPriceInput = findViewById(R.id.input_md_price_input)
        editTextNotesInput = findViewById(R.id.input_md_note_input)
        prevClass = intent.getStringExtra("PREV_CLASS")!!
        position = intent.getStringExtra("POSITION")!!

        buttonReturn.setOnClickListener(View.OnClickListener {
            var intentReturn = Intent(this@MasukanDataPerawatan,
            Class.forName(prevClass))
            if(prevClass == "AddPerawatan"){
                intentReturn.putExtra("PREV_CLASS", PilihKendaraan::class.java.name)
            }
            else{
                intentReturn.putExtra("PREV_CLASS", this@MasukanDataPerawatan::class.java.name)
            }
            intentReturn.putExtra("POSITION", position)
            startActivity(intentReturn)
            finish()
        })

        buttonGotoMD.setOnClickListener(View.OnClickListener {
            var intentGotoMD = Intent(this@MasukanDataPerawatan,
            UpdateDataPerawatan::class.java)
            intentGotoMD.putExtra("PREV_CLASS", this@MasukanDataPerawatan::class.java.name)
            intentGotoMD.putExtra("POSITION", position)
            startActivity(intentGotoMD)
            finish()
        })

        buttonGotoVD.setOnClickListener(View.OnClickListener {
            var intentGotoVD = Intent(this@MasukanDataPerawatan,
            PilihKendaraan::class.java)
            startActivity(intentGotoVD)
            finish()
        })

        buttonSave.setOnClickListener(View.OnClickListener {
            registerMaintenance()
        })

    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun registerMaintenance(){
        val type = editTextTypeInput.text.toString()
        val dates = editTextDatesInput.text.toString()
        val price = editTextPriceInput.text.toString().toInt()
        val notes = editTextNotesInput.text.toString()
        val email = user.email
        val name = user.displayName

        if(TextUtils.isEmpty(type)){
            Toast.makeText(applicationContext,
                "Masukkan Jenis",
                Toast.LENGTH_SHORT,).show()
            return
        }

        if(TextUtils.isEmpty(dates)){
            Toast.makeText(applicationContext,
                "Masukkan Tanggal",
                Toast.LENGTH_SHORT,).show()
            return
        }

        if(TextUtils.isEmpty(price.toString())){
            Toast.makeText(applicationContext,
                "Masukkan Biaya",
                Toast.LENGTH_SHORT,).show()
            return
        }

        if(TextUtils.isEmpty(notes)){
            Toast.makeText(applicationContext,
                "Masukkan Catatan",
                Toast.LENGTH_SHORT,).show()
            return
        }

        GlobalScope.launch(Dispatchers.Main) {
            val isMaxMaintenance = email?.let { checkIfMaxMaintenance(it) }

            val posMaintenance: String = when(email?.let { countNumMaintenance(it) }){
                0 -> "Pertama"
                1 -> "Kedua"
                else -> {
                    "Melebihi jumlah data"
                }
            }

            if (isMaxMaintenance == true){
                Toast.makeText(applicationContext,
                    "Data perawatan sudah penuh",
                    Toast.LENGTH_SHORT,).show()
            }
            else {
                val insertMaintenance = hashMapOf(
                    "pemilik" to name,
                    "email" to email,
                    "jenis" to type,
                    "tanggal" to dates,
                    "price" to price,
                    "catatan" to notes,
                    "pos" to posMaintenance
                )

                db.collection("maintenances")
                    .add(insertMaintenance)
                    .addOnSuccessListener {
                        Toast.makeText(applicationContext,
                        "Data perawatan berhasil ditambahkan",
                        Toast.LENGTH_SHORT).show()

                        editTextTypeInput.text.clear()
                        editTextDatesInput.text.clear()
                        editTextPriceInput.text.clear()
                        editTextNotesInput.text.clear()

                        var intentGotoPilihPerawatan = Intent(this@MasukanDataPerawatan,
                        UpdateDataPerawatan::class.java)
                        intentGotoPilihPerawatan.putExtra("PREV_CLASS",
                        this@MasukanDataPerawatan::class.java.name)
                        intentGotoPilihPerawatan.putExtra("POSITION", position)
                        startActivity(intentGotoPilihPerawatan)
                        finish()
                    }
                    .addOnFailureListener{ e ->
                        Toast.makeText(applicationContext,
                            "Data perawatan gagal ditambahkan karena $e",
                            Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private suspend fun checkIfMaxMaintenance(email: String): Boolean{
        val querySnapshot = db.collection("maintenances").get().await()
        val maxMaintenance = 2
        var numMaintenance = 0
        for (document in querySnapshot.documents) {
            val userEmail = document.getString("email")
            if (email == userEmail) {
                numMaintenance++
            }
        }
        return numMaintenance >= maxMaintenance
    }

    private suspend fun countNumMaintenance(email: String): Int{
        val querySnapshot = db.collection("maintenances").get().await()
        var numMaintenance = 0
        for (document in querySnapshot.documents){
            val userEmail = document.getString("email")
            if (email == userEmail){
                numMaintenance++
            }
        }
        return numMaintenance
    }

    /*
    private fun test(){
        /*
        val uploadProofBg = findViewById<ImageView>(R.id.input_md_upload_proof_bg)
        uploadProofBg.visibility = View.INVISIBLE
         */
        val btnGotoMD = findViewById<Button>(R.id.input_md_goto_md_button)
        btnGotoMD.setOnClickListener(View.OnClickListener {
            var intentGotoMD = Intent(this@MasukanDataPerawatan,
            UpdateDataPerawatan::class.java)
            startActivity(intentGotoMD)
        })

        val btnGotoVD = findViewById<Button>(R.id.input_md_goto_vd_button)
        btnGotoVD.setOnClickListener(View.OnClickListener {
            var intentGotoVD = Intent(this@MasukanDataPerawatan,
            PilihKendaraan::class.java)
            startActivity(intentGotoVD)
        })

        val iconGotoNotification = findViewById<ImageView>(R.id.input_md_notification_icon)
        iconGotoNotification.setOnClickListener(View.OnClickListener {
            var intentGotoNotif = Intent(this@MasukanDataPerawatan,
            LiatPengingat::class.java)
            startActivity(intentGotoNotif)
        })

        val btnSaveMD = findViewById<Button>(R.id.input_md_save_button)
        btnSaveMD.setOnClickListener(View.OnClickListener {
            var intentSaveMD = Intent(this@MasukanDataPerawatan,
            UpdateDataPerawatan::class.java)
            startActivity(intentSaveMD)
        })
    }

     */
}