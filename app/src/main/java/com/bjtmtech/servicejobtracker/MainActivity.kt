package com.bjtmtech.servicejobtracker

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.nav_header.*
import android.content.SharedPreferences
import android.util.Log
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    private lateinit var auth: FirebaseAuth
    lateinit var firebaseUser: FirebaseUser
    lateinit var message1:String
    lateinit var message2:String
    lateinit var shared : SharedPreferences

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val messageTextView2: TextView = findViewById(R.id.user_email)
//        messageTextView2.text = message2
//



        drawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        replaceFragment(dashboardFragment(), "Dashboard")
        navView.setCheckedItem(R.id.nav_dashboard)

        navView.setNavigationItemSelectedListener {

            it.isChecked = true  //to highlight the item
            when (it.itemId) {

                R.id.nav_dashboard -> replaceFragment(dashboardFragment(), it.title.toString())
                R.id.nav_create_job -> replaceFragment(createJobFragment(), it.title.toString())
                R.id.nav_view_active_job -> replaceFragment(activeJobFragment(), it.title.toString())
                R.id.nav_jobHistory -> replaceFragment(jobHistoryFragment(), it.title.toString())
                R.id.nav_jobType -> replaceFragment(jobtypeFragment(), it.title.toString())
                R.id.nav_language -> replaceFragment(languageFragment(), it.title.toString())
                R.id.nav_manageusers -> replaceFragment(manageusersFragment(), it.title.toString())
                R.id.nav_customers -> replaceFragment(customersFragment(), it.title.toString())
                R.id.nav_occupancy -> replaceFragment(occupancyFragment(), it.title.toString())
                R.id.nav_managedb -> replaceFragment(managedbFragment(), it.title.toString())
                R.id.nav_profile -> replaceFragment(profileFragment(), it.title.toString())
                R.id.nav_logout -> signout()
            }
            true
        }


        }

    private var back_press: Long = 0

    override fun onBackPressed() {
        if (back_press + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
        } else {
            Toast.makeText(
                baseContext, "Please press again to exit!",
                Toast.LENGTH_SHORT
            ).show()
            back_press = System.currentTimeMillis()
        }
    }

    //    used to replace fragment
    private fun replaceFragment(fragment: Fragment, title: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.framelayout, fragment)
        fragmentTransaction.commit()
        drawerLayout.closeDrawers()
        setTitle(title)  //set title

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {
            getExtras()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    private fun getExtras(){

        //Extract user details
        shared = getSharedPreferences("myProfile" , Context.MODE_PRIVATE)
        user_email.text = shared.getString("email" , "defaultemail@mail.com" ).toString()
        user_name.text = shared.getString("name" , "defultuser" ).toString()
    }

    private fun signout(){
        Firebase.auth.signOut()
        var loginPage = Intent(this, LoginActivity::class.java)
        startActivity(loginPage)
        finish()
        val edit = shared.edit()
        edit.clear()
    }



}


