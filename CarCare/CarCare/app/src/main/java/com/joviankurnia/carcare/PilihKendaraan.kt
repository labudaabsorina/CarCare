package com.joviankurnia.carcare

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
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

class PilihKendaraan : AppCompatActivity() {
    lateinit var textViewFirstVehicle : TextView
    lateinit var textViewSecondVehicle : TextView
    lateinit var imageViewLogout : ImageView
    lateinit var imageViewFirstVehicle : ImageView
    lateinit var imageViewSecondVehicle :ImageView
    lateinit var textAddVehicle : TextView
    lateinit var imageViewNotification : ImageView
    lateinit var buttonAddNotification : Button

    lateinit var auth: FirebaseAuth
    lateinit var user: FirebaseUser
    lateinit var db : FirebaseFirestore

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pilih_kendaraan)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        db = Firebase.firestore

        textViewFirstVehicle = findViewById(R.id.choose_first_vehicle_text)
        textViewSecondVehicle = findViewById(R.id.choose_second_vehicle_text)
        imageViewLogout = findViewById(R.id.choose_logout_icon)
        imageViewFirstVehicle = findViewById(R.id.choose_first_vehicle_image)
        imageViewSecondVehicle = findViewById(R.id.choose_second_vehicle_image)
        imageViewNotification = findViewById(R.id.choose_notification_image)
        textAddVehicle = findViewById(R.id.choose_add_vehicle_text)
        buttonAddNotification = findViewById(R.id.choose_add_reminder_button)
        var firstVehicleBrand = ""
        var firstVehicleModel = ""
        var secondVehicleBrand = ""
        var secondVehicleModel = ""
        var firstVehicleYear = 0
        var secondVehicleYear = 0

        GlobalScope.launch(Dispatchers.Main) {
            firstVehicleBrand = getVehicleBrand("Pertama")
            firstVehicleModel = getVehicleModel("Pertama")
            firstVehicleYear = getVehicleYear("Pertama")
            secondVehicleBrand = getVehicleBrand("Kedua")
            secondVehicleModel = getVehicleModel("Kedua")
            secondVehicleYear = getVehicleYear("Kedua")


            textViewFirstVehicle.text = "$firstVehicleBrand $firstVehicleModel"
            textViewSecondVehicle.text = "$secondVehicleBrand $secondVehicleModel"
        }

        imageViewLogout.setOnClickListener(View.OnClickListener {
            var intentGotoLogout = Intent(this@PilihKendaraan,
                Logout::class.java)
            intentGotoLogout.putExtra("PREV_CLASS", this@PilihKendaraan::class.java.name)
            startActivity(intentGotoLogout)
            finish()
        })

        textAddVehicle.setOnClickListener(View.OnClickListener {
            var intentGotoAddVehicle = Intent(this@PilihKendaraan,
            MasukkanKendaraan::class.java)
            intentGotoAddVehicle.putExtra("PREV_CLASS", this@PilihKendaraan::class.java.name)
            startActivity(intentGotoAddVehicle)
            finish()
        })

        imageViewNotification.setOnClickListener(View.OnClickListener {
            var intentGotoNotif = Intent(this@PilihKendaraan,
            LiatPengingat::class.java)
            intentGotoNotif.putExtra("PREV_CLASS", this@PilihKendaraan::class.java.name)
            startActivity(intentGotoNotif)
            finish()
        })

        buttonAddNotification.setOnClickListener(View.OnClickListener {
            var intentGotoAddNotif = Intent(this@PilihKendaraan,
            TambahPengingat::class.java)
            intentGotoAddNotif.putExtra("PREV_CLASS", this@PilihKendaraan::class.java.name)
            startActivity(intentGotoAddNotif)
            finish()
        })

        imageViewFirstVehicle.setOnClickListener(View.OnClickListener {
            var intentGotoFirstVehicle = Intent(this@PilihKendaraan,
            DataKendaraan::class.java)
            intentGotoFirstVehicle.putExtra("PREV_CLASS", this@PilihKendaraan::class.java.name)
            intentGotoFirstVehicle.putExtra("POSITION", "Pertama")
            startActivity(intentGotoFirstVehicle)
            finish()
        })

        imageViewSecondVehicle.setOnClickListener(View.OnClickListener {
            var intentGotoSecondVehicle = Intent(this@PilihKendaraan,
            DataKendaraan::class.java)
            intentGotoSecondVehicle.putExtra("PREV_CLASS", this@PilihKendaraan::class.java.name)
            intentGotoSecondVehicle.putExtra("POSITION", "Kedua")
            startActivity(intentGotoSecondVehicle)
            finish()
        })
    }

    private suspend fun getVehicleBrand(position: String): String{
        val email = user.email
        val querySnapshot = db.collection("vehicles").get().await()
        var vehicleBrand = ""
        for(document in querySnapshot.documents){
            val userEmail = document.getString("email")
            val note = document.getString("catatan")
            if(email == userEmail && position == note){
                vehicleBrand = document.getString("merek")!!
            }
        }
        return vehicleBrand
    }

    private suspend fun getVehicleModel(position: String): String{
        val email = user.email
        val querySnapshot = db.collection("vehicles").get().await()
        var vehicleModel = ""
        for(document in querySnapshot.documents){
            val userEmail = document.getString("email")
            val note = document.getString("catatan")
            if(email == userEmail && position == note){
                vehicleModel = document.getString("model")!!
            }
        }
        return vehicleModel
    }

    private suspend fun getVehicleYear(position: String): Int{
        val email = user.email
        val querySnapshot = db.collection("vehicles").get().await()
        var vehicleYear = 0
        for (document in querySnapshot.documents){
            val userEmail = document.getString("email")
            val note = document.getString("catatan")
            if(email == userEmail && position == note){
                vehicleYear = document.getLong("tahun")!!.toInt()
            }
        }
        return vehicleYear
    }

    /*
    private fun test(){
        val chooseFirstVehicleIcon = findViewById<ImageView>(R.id.choose_first_vehicle_image)
        chooseFirstVehicleIcon.setOnClickListener(View.OnClickListener {
            var intentChooseFirstVehicle = Intent(this@PilihKendaraan,
                DataKendaraan::class.java)
            startActivity(intentChooseFirstVehicle)
            finish()
        })

        val chooseSecondVehicleIcon = findViewById<ImageView>(R.id.choose_second_vehicle_image)
        chooseSecondVehicleIcon.setOnClickListener(View.OnClickListener {
            var intentChooseSecondVehicle = Intent(this@PilihKendaraan,
                DataKendaraan::class.java)
            startActivity(intentChooseSecondVehicle)
            finish()
        })

        val chooseAddVehicleText = findViewById<TextView>(R.id.choose_add_vehicle_text)
        chooseAddVehicleText.setOnClickListener(View.OnClickListener {
            var intentChooseAddVehicle = Intent(this@PilihKendaraan,
                MasukkanKendaraan::class.java)
            intentChooseAddVehicle.putExtra("PREV_CLASS", this@PilihKendaraan::class.java.name)
            startActivity(intentChooseAddVehicle)
            finish()
        })

        val chooseNotificationIcon = findViewById<ImageView>(R.id.choose_notification_image)
        chooseNotificationIcon.setOnClickListener(View.OnClickListener {
            var intentChooseNotification = Intent(this@PilihKendaraan,
                LiatPengingat::class.java)
            startActivity(intentChooseNotification)
            finish()
        })

        val chooseAddNotification = findViewById<Button>(R.id.choose_add_reminder_button)
        chooseAddNotification.setOnClickListener(View.OnClickListener {
            var intentAddNotification = Intent(this@PilihKendaraan,
                TambahPengingat::class.java)
            startActivity(intentAddNotification)
            finish()
        })

        val addVechicleLogout = findViewById<ImageView>(R.id.choose_logout_icon)
        addVechicleLogout.setOnClickListener(View.OnClickListener {
            var intentChooseVehicleLogout = Intent(this@PilihKendaraan,
                Logout::class.java)
            startActivity(intentChooseVehicleLogout)
            finish()
        })
    }

     */
}