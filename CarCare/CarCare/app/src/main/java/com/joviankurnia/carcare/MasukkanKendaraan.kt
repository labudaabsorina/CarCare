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
import org.w3c.dom.Text

class MasukkanKendaraan : AppCompatActivity() {
    lateinit var editTextModel : EditText
    lateinit var editTextBrand : EditText
    lateinit var editTextYear : EditText
    lateinit var logoutImage : ImageView
    lateinit var submitButton : Button
    lateinit var returnButton: Button
    lateinit var prevClass : String

    lateinit var auth: FirebaseAuth
    lateinit var user: FirebaseUser
    lateinit var db : FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_masukkan_kendaraan)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        db = Firebase.firestore

        editTextBrand = findViewById(R.id.input_vehicle_brand_input)
        editTextModel = findViewById(R.id.input_vehicle_model_input)
        editTextYear = findViewById(R.id.input_vehicle_year_input)
        logoutImage = findViewById(R.id.input_logout_image)
        submitButton = findViewById(R.id.input_vehicle_save_button)
        returnButton = findViewById(R.id.input_vehicle_return_button)
        prevClass = intent.getStringExtra("PREV_CLASS")!!

        logoutImage.setOnClickListener(View.OnClickListener {
            var intentGotoLogout = Intent(this@MasukkanKendaraan,
                Logout::class.java)
            intentGotoLogout.putExtra("PREV_CLASS", this@MasukkanKendaraan::class.java.name)
            startActivity(intentGotoLogout)
            finish()
        })

        returnButton.setOnClickListener(View.OnClickListener {
            var intentReturn = Intent(this@MasukkanKendaraan,
                Class.forName(prevClass))
            startActivity(intentReturn)
            finish()
        })

        submitButton.setOnClickListener(View.OnClickListener {
            registerVehicle()
        })
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun registerVehicle(){
        val brand = editTextBrand.text.toString()
        val model = editTextModel.text.toString()
        val year = editTextYear.text.toString().toInt()
        val name = user.displayName
        val email = user.email

        if(TextUtils.isEmpty(brand)){
            Toast.makeText(applicationContext,
            "Masukkan Merek",
            Toast.LENGTH_SHORT,).show()
            return
        }

        if(TextUtils.isEmpty(model)){
            Toast.makeText(applicationContext,
            "Masukkan Model",
            Toast.LENGTH_SHORT).show()
            return
        }

        if(TextUtils.isEmpty(year.toString())){
            Toast.makeText(applicationContext,
            "Masukkan Tahun",
            Toast.LENGTH_SHORT).show()
            return
        }

        GlobalScope.launch(Dispatchers.Main){
            val isMaxVehicle = email?.let { checkIfMaxVehicle(it) }

            val noteVehicle: String = when(email?.let { countNumberVehicle(it) }){
                0 -> "Pertama"
                1 -> "Kedua"
                else -> {
                    "Melebihi jumlah data"
                }
            }
            if (isMaxVehicle == true){
                Toast.makeText(applicationContext,
                "Data kendaraan sudah penuh",
                Toast.LENGTH_SHORT,).show()
            }
            else {
                val insertVehicle = hashMapOf(
                    "pemilik" to name,
                    "email" to email,
                    "merek" to brand,
                    "model" to model,
                    "tahun" to year,
                    "catatan" to noteVehicle
                )

                db.collection("vehicles")
                    .add(insertVehicle)
                    .addOnSuccessListener{
                        Toast.makeText(applicationContext,
                        "Data kendaraan berhasil ditambahkan",
                        Toast.LENGTH_SHORT).show()
                        editTextBrand.text.clear()
                        editTextModel.text.clear()
                        editTextYear.text.clear()

                        var intentGotoPilihKendaraan = Intent(this@MasukkanKendaraan,
                        PilihKendaraan::class.java)
                        startActivity(intentGotoPilihKendaraan)
                        finish()

                    }
                    .addOnFailureListener{ e ->
                        Toast.makeText(applicationContext,
                        "Data kendaraan gagal ditambahkan karena $e",
                        Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private suspend fun checkIfMaxVehicle(email: String): Boolean{
        val querySnapshot = db.collection("vehicles").get().await()
        val maxVehicle = 2
        var numVehicle = 0
        for (document in querySnapshot.documents) {
            val userEmail = document.getString("email")
            if (email == userEmail) {
                numVehicle++
            }
        }
        return numVehicle >= maxVehicle
    }



    private suspend fun countNumberVehicle(email: String): Int{
        val querySnapshot = db.collection("vehicles").get().await()
        var numVehicle = 0
        for (document in querySnapshot.documents){
            val userEmail = document.getString("email")
            if (email == userEmail){
                numVehicle++
            }
        }
        return numVehicle
    }
    /*
    private fun test() {
        val btnSubmitInput = findViewById<Button>(R.id.input_save_button)
        btnSubmitInput.setOnClickListener(View.OnClickListener {
            var intentSubmitInput = Intent(this@MasukkanKendaraan,
                PilihKendaraan::class.java)
            startActivity(intentSubmitInput)
        })

        val addLogoutIcon = findViewById<ImageView>(R.id.input_logout_image)
        addLogoutIcon.setOnClickListener(View.OnClickListener {
            var intentAddVehicleLogout = Intent(this@MasukkanKendaraan,
                Logout::class.java)
            startActivity(intentAddVehicleLogout)
        })
    }

     */
}