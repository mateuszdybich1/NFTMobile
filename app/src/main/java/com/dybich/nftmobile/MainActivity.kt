package com.dybich.nftmobile

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private var doublepress =false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val auth = Firebase.auth

        if(auth.currentUser != null){
            val intent = Intent(this,LoggedinActivity::class.java)
            startActivity(intent)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }



    }

    fun startlogin(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right,
            R.anim.slide_out_left);
    }

    fun startregister(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right,
            R.anim.slide_out_left);


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