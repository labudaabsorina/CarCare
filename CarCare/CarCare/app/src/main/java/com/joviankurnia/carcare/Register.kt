package com.joviankurnia.carcare

import android.content.ContentValues
import android.content.ContentValues.TAG
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
import com.google.android.recaptcha.RecaptchaClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.recaptcha.Recaptcha
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CountDownLatch


class Register : AppCompatActivity() {
    lateinit var editTextName : EditText
    lateinit var editTextEmail : EditText
    lateinit var editTextPassword : EditText
    lateinit var editTextConfirm : EditText
    lateinit var gotoLoginText : TextView
    lateinit var buttonRegister : Button

    lateinit var auth : FirebaseAuth
    lateinit var db : FirebaseFirestore
    //lateinit var recaptchaClient: RecaptchaClient

    /*
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null).
        val currentUser = auth.currentUser
        if (currentUser != null) {
            var intentLogin = Intent(this@Register,
                TambahKendaraan::class.java)
            startActivity(intentLogin)
            finish()
        }
    }\

     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        //initializeRecaptchaClient()
        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore

        editTextName = findViewById(R.id.register_name_input)
        editTextEmail = findViewById(R.id.register_email_input)
        editTextPassword = findViewById(R.id.register_password_input)
        editTextConfirm = findViewById(R.id.register_confirm_password_input)
        gotoLoginText = findViewById(R.id.register_goto_login_text)
        buttonRegister = findViewById(R.id.register_submit_button)

        gotoLoginText.setOnClickListener(View.OnClickListener {
            var intentRegister = Intent(this@Register,
                Login::class.java)
            startActivity(intentRegister)
            finish()
        })

        buttonRegister.setOnClickListener(View.OnClickListener {
            registerUser()
        })
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun registerUser(){
        val name = editTextName.text.toString()
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()
        val confirmPassword = editTextConfirm.text.toString().trim()

        if (TextUtils.isEmpty(name)){
            Toast.makeText(applicationContext,
            "Masukkan Nama",
            Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(email)){
            Toast.makeText(applicationContext,
                "Masukkan Email",
                Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(password)){
            Toast.makeText(applicationContext,
                "Masukkan Kata Sandi",
                Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(confirmPassword)){
            Toast.makeText(applicationContext,
                "Masukkan Ulangi Kata Sandi",
                Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword){
            Toast.makeText(applicationContext,
                "Kata Sandi tidak sama",
                Toast.LENGTH_SHORT).show()
            return
        }

        GlobalScope.launch(Dispatchers.Main){
            val isEmailExists = checkIfEmailExists(email)

            if(isEmailExists){
                Toast.makeText(
                    this@Register,
                    "Email sudah ada, gunakan yang lain.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
            else{
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this@Register) { task : Task<AuthResult?> ->
                        if (task.isSuccessful){
                            val user = auth.currentUser
                            val profileUpdates = userProfileChangeRequest {
                                displayName = name
                            }

                            user!!.updateProfile(profileUpdates)
                                .addOnCompleteListener(this@Register) { task ->
                                    if (task.isSuccessful){
                                        val insertUser = hashMapOf(
                                            "nama" to name,
                                            "email" to email,
                                            "password" to password
                                        )
                                        db.collection("users")
                                            .add(insertUser)
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    this@Register,
                                                    "Akun berhasil dibuat.",
                                                    Toast.LENGTH_SHORT,
                                                ).show()
                                                editTextConfirm.text.clear()
                                                editTextEmail.text.clear()
                                                editTextName.text.clear()
                                                editTextPassword.text.clear()

                                            }
                                            .addOnFailureListener{ e ->
                                                Toast.makeText(applicationContext,
                                                "Gagal membuat akun karena $e",
                                                Toast.LENGTH_SHORT).show()
                                            }
                                        /*
                                        db.collection("users")
                                            .get()
                                            .addOnSuccessListener { result ->
                                                for (document in result){
                                                    Log.d(TAG, "${document.id} => ${document.data}")
                                                }
                                            }
                                            .addOnFailureListener { exception ->
                                                Log.w(TAG, "Gagal mendapatkan data.", exception)
                                            }

                                         */
                                    }
                                }
                        }
                        else {
                            Toast.makeText(applicationContext,
                                "Pendaftaran Gagal",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    private suspend fun checkIfEmailExists(email: String): Boolean {
        val querySnapshot = db.collection("users").get().await()
        for (document in querySnapshot.documents) {
            val userEmail = document.getString("email")
            if (email == userEmail) {
                return true
            }
        }
        return false
    }
    /*
    private fun test(){
        lateinit var name : String
        lateinit var email : String
        lateinit var password : String
        lateinit var confirmPass : String
        name = editTextName.text.toString()
        email = editTextEmail.text.toString()
        password = editTextPassword.text.toString()
        confirmPass = editTextConfirm.text.toString()

        if (TextUtils.isEmpty(name)){
            Toast.makeText(
                this@Register,
                "Masukkan Nama",
                Toast.LENGTH_SHORT,
            ).show()
            return@OnClickListener
        }

        if (TextUtils.isEmpty(email)){
            Toast.makeText(
                this@Register,
                "Masukkan Email",
                Toast.LENGTH_SHORT,
            ).show()
            return@OnClickListener
        }

        if (TextUtils.isEmpty(password)){
            Toast.makeText(
                this@Register,
                "Masukkan Kata Sandi",
                Toast.LENGTH_SHORT,
            ).show()
            return@OnClickListener
        }

        if (TextUtils.isEmpty(confirmPass)){
            Toast.makeText(
                this@Register,
                "Masukkan Ulang Kata Sandi",
                Toast.LENGTH_SHORT,
            ).show()
            return@OnClickListener
        }

        if(confirmPass == password){
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        println("Masuk Ke Kirim Data")
                        val user = auth.currentUser
                        val profileUpdates = userProfileChangeRequest {
                            displayName = name
                        }

                        user!!.updateProfile(profileUpdates)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful){
                                    Toast.makeText(
                                        this@Register,
                                        "Akun berhasil dibuat.",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                }
                            }
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            this@Register,
                            "Akun gagal dibuat.",
                            Toast.LENGTH_SHORT,
                        ).show()
                        println(task)
                    }
                }
        } else {
            Toast.makeText(
                this@Register,
                "Kata sandi tidak sesuai",
                Toast.LENGTH_SHORT,
            ).show()
            return@OnClickListener
        }
    }

     */
    /*
    private fun initializeRecaptchaClient(){
        lifecycleScope.launch {
                Recaptcha.getClient(application, "6Lf6s-cmAAAAAC8v1lbzcChErtOjiLqEF0PIohL7")
                    .onSuccess { client ->
                        recaptchaClient = client
                    }
                    .onFailure {
                        Toast.makeText(
                            this@Register,
                            "Captcha sedang error",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
        }
    }

     */
}