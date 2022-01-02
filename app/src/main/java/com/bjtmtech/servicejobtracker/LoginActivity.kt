package com.bjtmtech.servicejobtracker

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var shared : SharedPreferences
    val db = Firebase.firestore
    var queryFirstName: String ?= null
    var queryLastName: String ?= null
    var queryEmail: String ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        onStart() //check if user session is still logged in and go to dashboard

        val registerButton = findViewById<TextView>(R.id.registerUser)

        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
//            finish()
        }

        loginBtn.setOnClickListener {

            val loginEmail = loginEmailText.text.toString().trim()

            val loginPassword = loginTextPassword.text.toString().trim()

            if (!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPassword)) {
                loginUser(loginEmail, loginPassword)
            } else {
                Toast.makeText(
                    this,
                    "User input empty!",
                    Toast.LENGTH_SHORT
                ).show()

            }


        }

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            val currentUser = auth.currentUser
            var userName = currentUser?.email.toString().split("@")[0]
            if(currentUser != null){
                var dashboardIntent = Intent(this, MainActivity::class.java)
                shared = getSharedPreferences("myProfile" , Context.MODE_PRIVATE)
                val edit = shared.edit()
                edit.putString("email" , currentUser.email.toString())
                edit.putString("name", userName)
                edit.apply()

                startActivity(dashboardIntent)
                finish()

            }
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { mTask ->

                if (mTask.isSuccessful) {
                    Toast.makeText(
                        this,
                        "User Logged in Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    queryEmail = email.toString()

                    var userName = email.split("@")[0]
                    var dashboardIntent = Intent(this, MainActivity::class.java)
                    shared = getSharedPreferences("myProfile" , Context.MODE_PRIVATE)
                    val edit = shared.edit()
                    edit.putString("email" , email)
                    edit.putString("name", userName)
                    edit.apply()

                    startActivity(dashboardIntent)
                    finish()

                } else {

                    Toast.makeText(
                        this,
                        "User Logged in Failed!",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
    }

}

