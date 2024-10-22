package com.joviankurnia.carcare

import android.content.Intent
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
import org.w3c.dom.Text

class DataKendaraan : AppCompatActivity() {
    lateinit var imageViewReturn: ImageView
    lateinit var buttonGotoMD: Button
    lateinit var buttonGotoVD: Button
    lateinit var textViewVehicleBrand: TextView
    lateinit var textViewVehicleModel: TextView
    lateinit var textViewVehicleYear: TextView
    lateinit var prevClass: String
    lateinit var position: String

    lateinit var auth: FirebaseAuth
    lateinit var user: FirebaseUser
    lateinit var db : FirebaseFirestore

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_kendaraan)
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        db = Firebase.firestore

        imageViewReturn = findViewById(R.id.vehicle_data_back_icon)
        buttonGotoMD = findViewById(R.id.vehicle_data_goto_md_button)
        buttonGotoVD = findViewById(R.id.vehicle_data_goto_vd_button)
        textViewVehicleBrand = findViewById(R.id.vehicle_data_brand_text)
        textViewVehicleModel = findViewById(R.id.vehicle_data_model_text)
        textViewVehicleYear = findViewById(R.id.vehicle_data_year_text)
        prevClass = intent.getStringExtra("PREV_CLASS")!!
        position = intent.getStringExtra("POSITION")!!
        var vehicleBrand = ""
        var vehicleModel = ""
        var vehicleYear = 0

        GlobalScope.launch(Dispatchers.Main){
            vehicleBrand = getVehicleBrand(position)
            vehicleModel = getVehicleModel(position)
            vehicleYear = getVehicleYear(position)

            textViewVehicleBrand.text = vehicleBrand
            textViewVehicleModel.text = vehicleModel
            textViewVehicleYear.text = vehicleYear.toString()
        }

        imageViewReturn.setOnClickListener(View.OnClickListener {
            var intentReturn = Intent(this@DataKendaraan,
            Class.forName(prevClass))
            intentReturn.putExtra("PREV_CLASS", this@DataKendaraan::class.java.name)
            startActivity(intentReturn)
            finish()
        })

        buttonGotoVD.setOnClickListener(View.OnClickListener {
            var intentGotoVD = Intent(this@DataKendaraan,
            PilihKendaraan::class.java)
            intentGotoVD.putExtra("PREV_CLASS", this@DataKendaraan::class.java.name)
            startActivity(intentGotoVD)
            finish()
        })

        buttonGotoMD.setOnClickListener(View.OnClickListener {
            var intentGotoMD = Intent(this@DataKendaraan,
            AddPerawatan::class.java)
            intentGotoMD.putExtra("PREV_CLASS", this@DataKendaraan::class.java.name)
            intentGotoMD.putExtra("POSITION", position)
            startActivity(intentGotoMD)
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
        val btnReturnData = findViewById<ImageView>(R.id.vehicle_data_back_icon)
        btnReturnData.setOnClickListener(View.OnClickListener {
            var intentReturnData = Intent(this@DataKendaraan,
                PilihKendaraan::class.java)
            startActivity(intentReturnData)
        })

        val btnGotoMD = findViewById<Button>(R.id.vehicle_data_goto_md_button)
        btnGotoMD.setOnClickListener(View.OnClickListener {
            var intentGotoMD = Intent(this@DataKendaraan,
                AddPerawatan::class.java)
            startActivity(intentGotoMD)
        })

        val btnGotoVD = findViewById<Button>(R.id.vehicle_data_goto_vd_button)
        btnGotoVD.setOnClickListener(View.OnClickListener {
            var intentGotoVD = Intent(this@DataKendaraan,
                PilihKendaraan::class.java)
            startActivity(intentGotoVD)
        })
        /*
        val brandText : TextView = findViewById<TextView>(R.id.vehicle_data_brand_text)
        brandText.text = intent.getStringExtra("USER_NAME")
        println(intent.getStringExtra("USER_NAME"))
        */
    }

     */
}