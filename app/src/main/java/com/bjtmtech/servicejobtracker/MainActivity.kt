package com.bjtmtech.servicejobtracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.nav_header.*

class MainActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout

    lateinit var message1 : String
    lateinit var message2 : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
//                R.id.nav_sync -> Toast.makeText(applicationContext, " Clicked Synchronize", Toast.LENGTH_SHORT).show()
//                R.id.nav_trash -> Toast.makeText(applicationContext, " Clicked Trash", Toast.LENGTH_SHORT).show()
//                R.id.nav_settings -> replaceFragment(SettingsFragment(), it.title.toString())
//                R.id.nav_login -> Toast.makeText(applicationContext, " Clicked Login", Toast.LENGTH_SHORT).show()
//                R.id.nav_share -> Toast.makeText(applicationContext, " Clicked Share", Toast.LENGTH_SHORT).show()
//                R.id.nav_rate_us -> Toast.makeText(applicationContext, " Clicked Rate Us", Toast.LENGTH_SHORT).show()
            }
            true
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

        //Extract the data
        message1 = intent.getStringExtra("name").toString()
        message2 = intent.getStringExtra("email").toString()
        val messageTextView1: TextView = findViewById(R.id.user_name)
        messageTextView1.text = message1

        val messageTextView2: TextView = findViewById(R.id.user_email)
        messageTextView2.text = message2
    }



}