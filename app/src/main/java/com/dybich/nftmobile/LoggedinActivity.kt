package com.dybich.nftmobile

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase


class LoggedinActivity: AppCompatActivity() {
    private lateinit var email:String
    private lateinit var password:String
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var logout:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loggedin)


        auth = FirebaseAuth.getInstance()
        logout = findViewById(R.id.btnlogout)
        val user = auth.currentUser
        user?.let {

            val email = user.email.toString()

            val tv: TextView = findViewById(R.id.username)
            tv.text= email




        }
    logout.setOnClickListener(){
        Firebase.auth.signOut()
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left,
            R.anim.slide_out_right);
    }
    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish()
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }



}