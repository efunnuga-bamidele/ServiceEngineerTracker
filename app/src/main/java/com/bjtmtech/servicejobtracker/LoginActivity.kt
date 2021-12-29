package com.bjtmtech.servicejobtracker

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


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

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
                    dashboardIntent.putExtra("name", userName)
                    dashboardIntent.putExtra("email", email)
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

