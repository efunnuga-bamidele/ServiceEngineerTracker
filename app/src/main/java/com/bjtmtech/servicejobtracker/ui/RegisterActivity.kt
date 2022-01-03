package com.bjtmtech.servicejobtracker.ui


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.bjtmtech.servicejobtracker.R
import com.bjtmtech.servicejobtracker.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

//    private lateinit var binding : ActivityRegisterBinding
//    private lateinit var database : DatabaseReference
    val db = Firebase.firestore
    private lateinit var auth : FirebaseAuth
    lateinit var shared : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()


        //        Code to getting country list
        val countryNamesList = ArrayList<String>()
        val countriesList: MutableList<String> = ArrayList()
        val locales = Locale.getISOCountries()
        for (countryCode in locales) {
            val obj = Locale("", countryCode)
            countriesList.add(obj.getDisplayCountry(Locale.ENGLISH))
            Collections.sort(countriesList)
        }
        for (s in countriesList) {
            countryNamesList.add(s)
        }

        val arrayAdapterCountry = ArrayAdapter(this,
            R.layout.customer_name_dropdown_items, countryNamesList)
        countryEditText.setAdapter(arrayAdapterCountry)





        registerBtn.setOnClickListener{


            val firstName = firstNameEditText.text.toString().trim()
            val lastName = lastNameEditText.text.toString().trim()
            val country = countryEditText.text.toString().trim()
            val jobTitle = jobTitleEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = initialPassword.text.toString().trim()
            val retypePassword = reTypePassword.text.toString().trim()



//Check for empty input fields
            if(firstName.isNotEmpty() && lastName.isNotEmpty() && country.isNotEmpty() && jobTitle.isNotEmpty() && email.isNotEmpty()
                && email.contains("@") && password.isNotEmpty() && password.length >= 6 && retypePassword.isNotEmpty()){

                if( password == retypePassword){

                    val User = User(firstName, lastName, country, jobTitle, email)

                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                        RegisterActivity()
                    ) {
                            task ->
                        if(task.isSuccessful) {
                            Toast.makeText(
                                this,
                                "User Registration Successfully!",
                                Toast.LENGTH_SHORT
                            )
                                .show()

                            val user = hashMapOf(
                                "firstName" to firstName,
                                "lastName" to lastName,
                                "country" to country,
                                "jobTitle" to jobTitle,
                                "email" to email,
                                "level" to "default",
                                "language" to "english"
                            )

                            db.collection("users").document(user["firstName"].toString()+"_"+user["lastName"].toString())
                                .set(user)
                                .addOnSuccessListener { documentReference ->
                                    firstNameEditText.text!!.clear()
                                    lastNameEditText.text!!.clear()
                                    countryEditText.text!!.clear()
                                    jobTitleEditText.text!!.clear()
                                    emailEditText.text!!.clear()
                                    initialPassword.text!!.clear()
                                    reTypePassword.text!!.clear()

                                    Toast.makeText(
                                        this,
                                        "User Profile Created!",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener { e ->
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
                                            "User Already Exist Logging In User",
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

