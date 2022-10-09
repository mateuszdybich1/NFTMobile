package com.dybich.nftmobile

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
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
        database = Firebase.database.reference

        auth = FirebaseAuth.getInstance()
        logout = findViewById(R.id.btnlogout)
        val user = auth.currentUser
        user?.let {

            val email = user.email.toString()
        database.child("users").child(user.uid.toString()).child("username").get().addOnSuccessListener {
            val tvemail: TextView = findViewById(R.id.loggedinEmail)
            val tvlogin: TextView = findViewById(R.id.loggedinUsername)
            tvemail.text= email
            val login : String = it.value.toString()
            tvlogin.text = login
        }





        }
    logout.setOnClickListener(){
        Firebase.auth.signOut()
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left,
            R.anim.slide_out_right)
        finishAffinity();
        finish()
    }
    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity()
            finish()
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }



}