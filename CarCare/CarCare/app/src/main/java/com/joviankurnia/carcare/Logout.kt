package com.joviankurnia.carcare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Logout : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var logoutButton: Button
    lateinit var logoutInfoText: TextView
    lateinit var logoutUserText: TextView
    lateinit var returnButton: Button
    lateinit var user: FirebaseUser
    lateinit var prevClass : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logout)

        auth = Firebase.auth
        logoutButton = findViewById(R.id.logout_exit_button)
        logoutInfoText = findViewById(R.id.logout_info_text)
        logoutUserText = findViewById(R.id.logout_user_info_text)
        returnButton = findViewById(R.id.logout_return_button)
        if(auth.currentUser == null){
            var intentLogin = Intent(this@Logout,
            Login::class.java)
            startActivity(intentLogin)
            finish()
        }
        user = auth.currentUser!!
        prevClass = intent.getStringExtra("PREV_CLASS")!!
        println("Prev Class Isinya $prevClass")

        logoutUserText.text = user.displayName

        logoutButton.setOnClickListener(View.OnClickListener {
            FirebaseAuth.getInstance().signOut()
            var intentLogin = Intent(this@Logout,
                Login::class.java)
            startActivity(intentLogin)
            finish()
        })

        returnButton.setOnClickListener(View.OnClickListener {
            var intentReturn = Intent(this@Logout,
            Class.forName(prevClass))
            if(prevClass == "MasukkanKendaraan"){
                intentReturn.putExtra("PREV_CLASS", PilihKendaraan::class.java.name)
            }else {
                intentReturn.putExtra("PREV_CLASS", this@Logout::class.java.name)
            }
            startActivity(intentReturn)
            finish()
        })

        /*
        val btnLogout = findViewById<Button>(R.id.logout_exit_button)
        btnLogout.setOnClickListener(View.OnClickListener {
            var intentLogout = Intent(this@Logout,
            Login::class.java)
            startActivity(intentLogout)
        })

        val btnReturn = findViewById<Button>(R.id.logout_return_button)
        btnReturn.setOnClickListener(View.OnClickListener {
            var intentReturn = Intent(this@Logout,
            TambahKendaraan::class.java)
            startActivity(intentReturn)
            finish()
        })

         */
    }
}