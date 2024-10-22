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
import org.w3c.dom.Text

class LiatPengingat : AppCompatActivity() {
    lateinit var textViewFirstNotifType : TextView
    lateinit var textViewFirstNotifDates : TextView
    lateinit var textViewSecondNotifType : TextView
    lateinit var textViewSecondNotifDates : TextView
    lateinit var imageViewReturn : ImageView
    lateinit var prevClass: String

    lateinit var auth: FirebaseAuth
    lateinit var user: FirebaseUser
    lateinit var db : FirebaseFirestore

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liat_pengingat)
        textViewFirstNotifType = findViewById(R.id.notification_first_text_input)
        textViewFirstNotifDates = findViewById(R.id.notification_first_date_input)
        textViewSecondNotifType = findViewById(R.id.notification_second_text_input)
        textViewSecondNotifDates = findViewById(R.id.notification_second_date_input)
        imageViewReturn = findViewById(R.id.notification_return_image)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        db = Firebase.firestore
        val userEmail = user.email

        prevClass = intent.getStringExtra("PREV_CLASS").toString()!!

        imageViewReturn.setOnClickListener(View.OnClickListener {
            var intentReturn = Intent(this@LiatPengingat,
            Class.forName(prevClass))
            intentReturn.putExtra("PREV_CLASS",
            this@LiatPengingat::class.java.name)
            intentReturn.putExtra("POSITION", "Pertama")
            startActivity(intentReturn)
            finish()
        })

        GlobalScope.launch(Dispatchers.Main){
            var notifFirstType = userEmail?.let { getNotifType("Pertama", it) }
            var notifFirstDates = userEmail?.let { getNotifDates("Pertama", it) }
            var notifSecondType = userEmail?.let { getNotifType("Kedua", it) }
            var notifSecondDates = userEmail?.let { getNotifDates("Kedua", it) }

            textViewFirstNotifType.text = notifFirstType
            textViewFirstNotifDates.text = notifFirstDates
            textViewSecondNotifType.text = notifSecondType
            textViewSecondNotifDates.text = notifSecondDates
        }
    }

    private suspend fun getNotifType(position: String, email: String) : String{
        val querySnapshot = db.collection("notifications").get().await()
        var notifType = ""
        for (document in querySnapshot.documents){
            val userEmail = document.getString("email")
            val notifPos = document.getString("catatan")
            if(email == userEmail && position == notifPos){
                notifType = document.getString("jenis")!!
            }
        }
        return notifType
    }

    private suspend fun getNotifDates(position:String, email: String) : String{
        val querySnapshot = db.collection("notifications").get().await()
        var notifDates = ""
        for (document in querySnapshot.documents){
            val userEmail = document.getString("email")
            val notifPos = document.getString("catatan")
            if(email == userEmail && position == notifPos){
                notifDates = document.getString("tanggal")!!
            }
        }
        return notifDates
    }
}