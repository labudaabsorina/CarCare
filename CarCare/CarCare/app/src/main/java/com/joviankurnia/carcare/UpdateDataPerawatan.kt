package com.joviankurnia.carcare

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.Global
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

class UpdateDataPerawatan : AppCompatActivity() {
    lateinit var textViewFirstMaintenance : TextView
    lateinit var textViewSecondMaintenance : TextView
    lateinit var imageViewFirstMaintenance : ImageView
    lateinit var imageViewSecondMaintenance :ImageView
    lateinit var textAddMaintenance : TextView
    lateinit var imageViewNotification : ImageView
    lateinit var buttonAddNotification : Button
    lateinit var buttonGotoVD: Button
    lateinit var buttonGotoMD: Button
    lateinit var prevClass: String
    lateinit var position: String

    lateinit var auth: FirebaseAuth
    lateinit var user: FirebaseUser
    lateinit var db : FirebaseFirestore

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_data_perawatan)
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        db = Firebase.firestore
        val email = user.email!!

        textViewFirstMaintenance = findViewById(R.id.update_maintenance_first_text)
        textViewSecondMaintenance = findViewById(R.id.update_maintenance_second_text)
        imageViewFirstMaintenance = findViewById(R.id.update_maintenance_first_image)
        imageViewSecondMaintenance = findViewById(R.id.update_maintenance_second_image)
        textAddMaintenance = findViewById(R.id.update_add_maintenance_text)
        imageViewNotification = findViewById(R.id.update_goto_notification_image)
        buttonAddNotification = findViewById(R.id.update_add_notification_button)
        buttonGotoVD = findViewById(R.id.update_VD_goto_VD_button)
        buttonGotoMD = findViewById(R.id.update_MD_goto_MD_button)
        prevClass = intent.getStringExtra("PREV_CLASS")!!
        position = intent.getStringExtra("POSITION")!!

        var firstMaintenanceType = ""
        var secondMaintenanceType = ""

        GlobalScope.launch(Dispatchers.Main) {
            firstMaintenanceType = getMaintenanceType("Pertama", email)
            secondMaintenanceType = getMaintenanceType("Kedua", email)

            textViewFirstMaintenance.text = "$firstMaintenanceType"
            textViewSecondMaintenance.text = "$secondMaintenanceType"
        }

        buttonGotoVD.setOnClickListener(View.OnClickListener {
            var intentGotoVD = Intent(this@UpdateDataPerawatan,
            PilihKendaraan::class.java)
            startActivity(intentGotoVD)
            finish()
        })

        buttonGotoMD.setOnClickListener(View.OnClickListener {
            var intentGotoMD = Intent(this@UpdateDataPerawatan,
            UpdateDataPerawatan::class.java)
            intentGotoMD.putExtra("PREV_CLASS",
                this@UpdateDataPerawatan::class.java.name)
            intentGotoMD.putExtra("POSITION", position)
            startActivity(intentGotoMD)
            finish()
        })

        textAddMaintenance.setOnClickListener(View.OnClickListener {
            var intentGotoAddVehicle = Intent(this@UpdateDataPerawatan,
                MasukanDataPerawatan::class.java)
            intentGotoAddVehicle.putExtra("PREV_CLASS",
                this@UpdateDataPerawatan::class.java.name)
            intentGotoAddVehicle.putExtra("POSITION", position)
            startActivity(intentGotoAddVehicle)
            finish()
        })

        imageViewNotification.setOnClickListener(View.OnClickListener {
            var intentGotoNotif = Intent(this@UpdateDataPerawatan,
                LiatPengingat::class.java)
            intentGotoNotif.putExtra("PREV_CLASS",
                this@UpdateDataPerawatan::class.java.name)
            startActivity(intentGotoNotif)
            finish()
        })

        buttonAddNotification.setOnClickListener(View.OnClickListener {
            var intentGotoAddNotif = Intent(this@UpdateDataPerawatan,
                TambahPengingat::class.java)
            intentGotoAddNotif.putExtra("PREV_CLASS",
                this@UpdateDataPerawatan::class.java.name)
            startActivity(intentGotoAddNotif)
            finish()
        })

        imageViewFirstMaintenance.setOnClickListener(View.OnClickListener {
            var intentGotoFirstMaintenance = Intent(this@UpdateDataPerawatan,
            DataPerawatan::class.java)
            intentGotoFirstMaintenance.putExtra("PREV_CLASS",
                this@UpdateDataPerawatan::class.java.name)
            intentGotoFirstMaintenance.putExtra("POSITION", "Pertama")
            startActivity(intentGotoFirstMaintenance)
            finish()
        })

        imageViewSecondMaintenance.setOnClickListener(View.OnClickListener {
            var intentGotoSecondMaintenance = Intent(this@UpdateDataPerawatan,
                DataPerawatan::class.java)
            intentGotoSecondMaintenance.putExtra("PREV_CLASS",
                this@UpdateDataPerawatan::class.java.name)
            intentGotoSecondMaintenance.putExtra("POSITION", "Kedua")
            startActivity(intentGotoSecondMaintenance)
            finish()
        })
    }

    private suspend fun getMaintenanceType(position: String, email: String): String{
        val querySnapshot = db.collection("maintenances").get().await()
        var maintenanceType = ""
        for(document in querySnapshot.documents){
            val userEmail = document.getString("email")
            val note = document.getString("pos")
            if(email == userEmail && position == note){
                maintenanceType = document.getString("jenis")!!
            }
        }
        return maintenanceType
    }

    /*
    private fun test(){
        val btnGotoMD = findViewById<Button>(R.id.update_MD_goto_MD_button)
        btnGotoMD.setOnClickListener(View.OnClickListener {
            var intentGotoMD = Intent(this@UpdateDataPerawatan,
            UpdateDataPerawatan::class.java)
            startActivity(intentGotoMD)
        })

        val btnGotoVD = findViewById<Button>(R.id.update_VD_goto_VD_button)
        btnGotoVD.setOnClickListener(View.OnClickListener {
            var intentGotoVD = Intent(this@UpdateDataPerawatan,
            PilihKendaraan::class.java)
            startActivity(intentGotoVD)
        })

        val imageGotoFirstMD = findViewById<ImageView>(R.id.update_maintenance_first_image)
        imageGotoFirstMD.setOnClickListener(View.OnClickListener {
            var intentFirstMD = Intent(this@UpdateDataPerawatan,
            DataPerawatan::class.java)
            startActivity(intentFirstMD)
        })

        val imageGotoSecondMD = findViewById<ImageView>(R.id.update_maintenance_second_image)
        imageGotoSecondMD.setOnClickListener(View.OnClickListener {
            var intentSecondMD = Intent(this@UpdateDataPerawatan,
            DataPerawatan::class.java)
            startActivity(intentSecondMD)
        })

        val textAddPerawatan = findViewById<TextView>(R.id.update_add_maintenance_text)
        textAddPerawatan.setOnClickListener(View.OnClickListener {
            var intentAddM = Intent(this@UpdateDataPerawatan,
            MasukanDataPerawatan::class.java)
            startActivity(intentAddM)
        })

        val imageGotoNotif = findViewById<ImageView>(R.id.update_goto_notification_image)
        imageGotoNotif.setOnClickListener(View.OnClickListener {
            var intentGotoNotif = Intent(this@UpdateDataPerawatan,
            LiatPengingat::class.java)
            startActivity(intentGotoNotif)
        })

        val btnAddNotif = findViewById<Button>(R.id.update_add_notification_button)
        btnAddNotif.setOnClickListener(View.OnClickListener {
            var intentAddNotif = Intent(this@UpdateDataPerawatan,
            TambahPengingat::class.java)
            startActivity(intentAddNotif)
        })
    }

     */
}