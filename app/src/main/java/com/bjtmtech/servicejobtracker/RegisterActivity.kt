package com.bjtmtech.servicejobtracker


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bjtmtech.servicejobtracker.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRegisterBinding
    private lateinit var database : DatabaseReference
    private lateinit var auth : FirebaseAuth
    lateinit var shared : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Users")




        binding.registerBtn.setOnClickListener{
            val firstName = binding.firstNameEditText.text.toString().trim()
            val lastName = binding.lastNameEditText.text.toString().trim()
            val country = binding.countryEditText.text.toString().trim()
            val jobTitle = binding.jobTitleEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.initialPassword.text.toString().trim()
            val retypePassword = binding.reTypePassword.text.toString().trim()

//Check for empty input fields
            if(firstName.isNotEmpty() && lastName.isNotEmpty() && country.isNotEmpty() && jobTitle.isNotEmpty() && email.isNotEmpty()
                && email.contains("@") && password.isNotEmpty() && password.length >= 6 && retypePassword.isNotEmpty()){

                if( password == retypePassword){

                    val User = User(firstName, lastName, country, jobTitle, email)

                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity()) {
                            task ->
                        if(task.isSuccessful) {
                            Toast.makeText(
                                this,
                                "User Registration Successfully!",
                                Toast.LENGTH_SHORT
                            )
                                .show()

                            database.child(firstName+"_"+lastName).setValue(User).addOnSuccessListener {

                                binding.firstNameEditText.text.clear()
                                binding.lastNameEditText.text.clear()
                                binding.countryEditText.text.clear()
                                binding.jobTitleEditText.text.clear()
                                binding.emailEditText.text.clear()
                                binding.initialPassword.text.clear()
                                binding.reTypePassword.text.clear()

                                Toast.makeText(
                                    this,
                                    "User Profile Created!",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                //                                finish()
                            }.addOnFailureListener {
                                Toast.makeText(
                                    this,
                                    "User Registration Failed! Please try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }else{

//                            Login user if account exists
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener{mTask ->

                                    if(mTask.isSuccessful){
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

                                    }else{
                                        Toast.makeText(
                                            this,
                                            "User Registration Failed! Please try again",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    }
                                }
//
//                                    Toast.makeText(
//                                        this,
//                                        "User Registered Failed! Please try again",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
                        }
                    }


                }else{
                    Toast.makeText(this, "Password and Re-Enter Password is not a Match! ", Toast.LENGTH_SHORT).show()
                }

            }else{
                Toast.makeText(this, "Make sure all fields are filled", Toast.LENGTH_LONG).show()
            }

        }
    }

}

