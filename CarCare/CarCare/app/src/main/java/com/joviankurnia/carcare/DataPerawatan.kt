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

class DataPerawatan : AppCompatActivity() {
    lateinit var imageViewReturn: ImageView
    lateinit var buttonGotoMD: Button
    lateinit var buttonGotoVD: Button
    lateinit var textViewMaintenanceType: TextView
    lateinit var textViewMaintenanceDates: TextView
    lateinit var textViewMaintenancePrice: TextView
    lateinit var textViewMaintenanceNotes: TextView
    lateinit var prevClass: String
    lateinit var position: String

    lateinit var auth: FirebaseAuth
    lateinit var user: FirebaseUser
    lateinit var db : FirebaseFirestore

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_perawatan)
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        db = Firebase.firestore
        val email = user.email!!

        imageViewReturn = findViewById(R.id.maintenante_data_return)
        buttonGotoMD = findViewById(R.id.maintenance_data_goto_md_button)
        buttonGotoVD = findViewById(R.id.maintenance_data_goto_vd_button)
        textViewMaintenanceType = findViewById(R.id.maintenance_data_type_m_text)
        textViewMaintenanceDates = findViewById(R.id.maintenance_data_date_m_text)
        textViewMaintenancePrice = findViewById(R.id.maintenance_data_price_m_text)
        textViewMaintenanceNotes = findViewById(R.id.maintenance_data_note_m_text)
        prevClass = intent.getStringExtra("PREV_CLASS")!!
        position = intent.getStringExtra("POSITION")!!

        var maintenanceType = ""
        var maintenanceDates = ""
        var maintenancePrice = 0
        var maintenanceNote = ""

        GlobalScope.launch(Dispatchers.Main) {
            maintenanceType = getMaintenanceType(position, email)
            maintenanceDates = getMaintenanceDates(position, email)
            maintenancePrice = getMaintenancePrice(position, email)
            maintenanceNote = getMaintenanceNote(position, email)

            textViewMaintenanceType.text = maintenanceType
            textViewMaintenanceDates.text = maintenanceDates
            textViewMaintenancePrice.text = maintenancePrice.toString()
            textViewMaintenanceNotes.text = maintenanceNote
        }

        imageViewReturn.setOnClickListener(View.OnClickListener {
            var intentReturn = Intent(this@DataPerawatan,
                Class.forName(prevClass))
            intentReturn.putExtra("PREV_CLASS",
                this@DataPerawatan::class.java.name)
            intentReturn.putExtra("POSITION", position)
            startActivity(intentReturn)
            finish()
        })

        buttonGotoVD.setOnClickListener(View.OnClickListener {
            var intentGotoVD = Intent(this@DataPerawatan,
                PilihKendaraan::class.java)
            intentGotoVD.putExtra("PREV_CLASS",
                this@DataPerawatan::class.java.name)
            startActivity(intentGotoVD)
            finish()
        })

        buttonGotoMD.setOnClickListener(View.OnClickListener {
            var intentGotoMD = Intent(this@DataPerawatan,
            UpdateDataPerawatan::class.java)
            intentGotoMD.putExtra("PREV_CLASS",
            this@DataPerawatan::class.java.name)
            intentGotoMD.putExtra("POSITION", position)
            startActivity(intentGotoMD)
            finish()
        })
    }

    private suspend fun getMaintenanceType(position: String, email: String): String{
        val querySnapshot = db.collection("maintenances").get().await()
        var maintenanceType= ""
        for(document in querySnapshot.documents){
            val userEmail = document.getString("email")
            val note = document.getString("pos")
            if(email == userEmail && position == note){
                maintenanceType = document.getString("jenis")!!
            }
        }
        return maintenanceType
    }

    private suspend fun getMaintenanceDates(position: String, email: String): String{
        val querySnapshot = db.collection("maintenances").get().await()
        var maintenanceDates= ""
        for(document in querySnapshot.documents){
            val userEmail = document.getString("email")
            val note = document.getString("pos")
            if(email == userEmail && position == note){
                maintenanceDates = document.getString("tanggal")!!
            }
        }
        return maintenanceDates
    }

    private suspend fun getMaintenancePrice(position: String, email: String): Int{
        val querySnapshot = db.collection("maintenances").get().await()
        var maintenancePrice = 0
        for (document in querySnapshot.documents){
            val userEmail = document.getString("email")
            val note = document.getString("pos")
            if(email == userEmail && position == note){
                maintenancePrice = document.getLong("price")!!.toInt()
            }
        }
        return maintenancePrice
    }

    private suspend fun getMaintenanceNote(position: String, email: String): String{
        val querySnapshot = db.collection("maintenances").get().await()
        var maintenanceNote= ""
        for(document in querySnapshot.documents){
            val userEmail = document.getString("email")
            val note = document.getString("pos")
            if(email == userEmail && position == note){
                maintenanceNote = document.getString("catatan")!!
            }
        }
        return maintenanceNote
    }

    /*
    private fun test(){
        val imgReturn = findViewById<ImageView>(R.id.maintenante_data_return)
        imgReturn.setOnClickListener(View.OnClickListener {
            var intentReturn = Intent(this@DataPerawatan,
            UpdateDataPerawatan::class.java)
            startActivity(intentReturn)
        })

        val btnGotoMD = findViewById<Button>(R.id.maintenance_data_goto_md_button)
        btnGotoMD.setOnClickListener(View.OnClickListener {
            var intentGotoMD = Intent(this@DataPerawatan,
            UpdateDataPerawatan::class.java)
            startActivity(intentGotoMD)
        })

        val btnGotoVD = findViewById<Button>(R.id.maintenance_data_goto_vd_button)
        btnGotoVD.setOnClickListener(View.OnClickListener {
            var intentGotoVD = Intent(this@DataPerawatan,
            PilihKendaraan::class.java)
            startActivity(intentGotoVD)
        })

        val btnGotoPs = findViewById<Button>(R.id.maintenance_data_goto_ps_button)
        btnGotoPs.setOnClickListener(View.OnClickListener {
            var intentGotoPs = Intent(this@DataPerawatan,
            SlideBuktiPembayaran::class.java)
            startActivity(intentGotoPs)
        })
    }

     */
}