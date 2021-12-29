package com.bjtmtech.servicejobtracker

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

class MainActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    private lateinit var auth: FirebaseAuth
    lateinit var firebaseUser: FirebaseUser
    lateinit var message1:String
    lateinit var message2:String

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//            onResume()


//
//        val messageTextView1: TextView = findViewById(R.id.user_name)
//        messageTextView1.text = message1
//
//        val messageTextView2: TextView = findViewById(R.id.user_email)
//        messageTextView2.text = message2
//



        drawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        replaceFragment(dashboardFragment(), "Home")
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

        //Extract th
        val intent = intent
        if (intent.hasExtra("name")) {
            intent.extras!!.getBoolean("name")
            val messageTextView1: TextView = findViewById(R.id.user_name)
            messageTextView1.text = intent.getStringExtra("name").toString()
            // TODO: Do something with the value of isNew.
            Toast.makeText(applicationContext, intent.getStringExtra("name").toString(), Toast.LENGTH_SHORT).show()
        }


//        message1 = intent.getStringExtra("name").toString()
//        message2 = intent.getStringExtra("email").toString()
//        val messageTextView1: TextView = findViewById(R.id.user_name)
//        messageTextView1.text = message1
//        val messageTextView2: TextView = findViewById(R.id.user_email)
//        messageTextView2.text = message2
//        Toast.makeText(applicationContext, message1 +" : "+message2, Toast.LENGTH_SHORT).show()

    }

    private fun signout(){
        Firebase.auth.signOut()
        var loginPage = Intent(this, LoginActivity::class.java)
        startActivity(loginPage)
        finish()
    }

    public override fun onResume() {
        super.onResume()
//        message1 = intent.getStringExtra("name").toString()
//        message2 = intent.getStringExtra("email").toString()
//        val messageTextView1: TextView = findViewById(R.id.user_name)
//        messageTextView1.text = message1
//        val messageTextView2: TextView = findViewById(R.id.user_email)
//        messageTextView2.text = message2
//        Toast.makeText(applicationContext, message1 +" : "+message2, Toast.LENGTH_SHORT).show()
    }

}