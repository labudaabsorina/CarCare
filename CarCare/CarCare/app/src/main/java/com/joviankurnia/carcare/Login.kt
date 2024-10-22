package com.joviankurnia.carcare

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var gotoRegisterText: TextView
    private lateinit var buttonLogin: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore

        /*
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result){
                    //Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                    println("${document.get("email")}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Gagal mendapatkan data.", exception)
            }

         */

        editTextEmail = findViewById(R.id.login_email_input)
        editTextPassword = findViewById(R.id.login_password_input)
        gotoRegisterText = findViewById(R.id.login_goto_register_text)
        buttonLogin = findViewById(R.id.login_logged_button)

        gotoRegisterText.setOnClickListener {
            var intentRegister = Intent(this@Login,
            Register::class.java)
            startActivity(intentRegister)
            finish()
        }

        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            if (isInvalidEmail(email) || isInvalidPassword(password)) {
                return@setOnClickListener
            }

            login(email, password)
        }
    }

    private fun isInvalidEmail(email: String): Boolean {
        if (TextUtils.isEmpty(email)) {
            showToast(R.string.enter_email)
            return true
        }
        return false
    }

    private fun isInvalidPassword(password: String): Boolean {
        if (TextUtils.isEmpty(password)) {
            showToast(R.string.enter_password)
            return true
        }
        return false
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    showToast(R.string.login_success)
                    val intentLogin = Intent(this@Login,
                        TambahKendaraan::class.java)
                    startActivity(intentLogin)
                    finish()
                } else {
                    showToast(R.string.login_failed)
                }
            }
    }

    private fun showToast(messageResId: Int) {
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }

    /*
    lateinit var editTextEmail : EditText
    lateinit var editTextPassword : EditText
    lateinit var gotoRegisterText : TextView
    lateinit var buttonLogin : Button
    lateinit var auth : FirebaseAuth

     */

    /*
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null).
        val currentUser = auth.currentUser
        if (currentUser != null) {
            var intentLogin = Intent(this@Login,
                TambahKendaraan::class.java)
            startActivity(intentLogin)
            finish()
        }
    }

     */

    /*
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth
        editTextEmail = findViewById(R.id.login_email_input)
        editTextPassword = findViewById(R.id.login_password_input)
        gotoRegisterText = findViewById(R.id.login_goto_register_text)
        buttonLogin = findViewById(R.id.login_logged_button)

        gotoRegisterText.setOnClickListener(View.OnClickListener {
            var intentRegister = Intent(this@Login,
                Register::class.java)
            startActivity(intentRegister)
            finish()
        })

        buttonLogin.setOnClickListener(View.OnClickListener {
            lateinit var email : String
            lateinit var password : String
            email = editTextEmail.text.toString()
            password = editTextPassword.text.toString()

            if (TextUtils.isEmpty(email)){
                Toast.makeText(
                    this@Login,
                    "Masukkan Email",
                    Toast.LENGTH_SHORT,
                ).show()
                return@OnClickListener
            }

            if (TextUtils.isEmpty(password)){
                Toast.makeText(
                    this@Login,
                    "Masukkan Kata Sandi",
                    Toast.LENGTH_SHORT,
                ).show()
                return@OnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(
                            this@Login,
                            "Masuk Berhasil.",
                            Toast.LENGTH_SHORT,
                        ).show()
                        var intentLogin = Intent(this@Login,
                        TambahKendaraan::class.java)
                        startActivity(intentLogin)
                        finish()
                    } else {
                        Toast.makeText(
                            this@Login,
                            "Masuk Gagal.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        })

        /*
        val btnLoginNext = findViewById<Button>(R.id.login_logged_button)
        btnLoginNext.setOnClickListener(View.OnClickListener {
            var intentLogin = Intent(this@Login,
                TambahKendaraan::class.java)
            startActivity(intentLogin)
        })
        val textGotoRegister = findViewById<TextView>(R.id.login_goto_register)
        textGotoRegister.setOnClickListener(View.OnClickListener {
            var intentGotoRegister = Intent(this@Login,
                Register::class.java)
            startActivity(intentGotoRegister)
        })

         */
    }
    */

}