package com.bjtmtech.servicejobtracker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText

import android.widget.TextView
import android.widget.Toast
import com.bjtmtech.servicejobtracker.databinding.ActivityLoginBinding
import com.bjtmtech.servicejobtracker.databinding.ActivityRegisterBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import android.content.SharedPreferences





class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    lateinit var shared : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        onStart() //check if user session is still logged in and go to dashboard

        val registerButton = findViewById<TextView>(R.id.registerUser)

        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.loginBtn.setOnClickListener {

            val loginEmail = binding.loginEmailText.text.toString().trim()

            val loginPassword = binding.loginTextPassword.text.toString().trim()

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

