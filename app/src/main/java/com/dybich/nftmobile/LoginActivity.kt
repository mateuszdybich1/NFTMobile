package com.dybich.nftmobile

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class LoginActivity: AppCompatActivity() {

    private lateinit var email:EditText
    private lateinit var password:EditText

    private lateinit var auth: FirebaseAuth

    private lateinit var btnlogin : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)


        email = findViewById<EditText>(R.id.loginmail)
        password = findViewById<EditText>(R.id.loginpass)
        auth = FirebaseAuth.getInstance()

        btnlogin = findViewById(R.id.btnlogin)


        btnlogin.setOnClickListener() {
            var emailvar = email.text.toString()
            var passvar = password.text.toString()

            if(emailvar.isNotEmpty() && passvar.isNotEmpty()){
                if(android.util.Patterns.EMAIL_ADDRESS.matcher(emailvar).matches()){
                    auth.signInWithEmailAndPassword(emailvar,passvar).addOnCompleteListener{
                        if(it.isSuccessful){
                                val intent = Intent(this,LoggedinActivity::class.java)
                                startActivity(intent)
                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                            finish()
                        }
                        else{
                            Toast.makeText(this,"Incorrect password. Please try again", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                else{
                    Toast.makeText(this,"Email doesn't exists. Please sign up", Toast.LENGTH_LONG).show()
                }
            }
            else{
                Toast.makeText(this,"Empty fields are not allowed", Toast.LENGTH_LONG).show()
            }
        }
        if(auth.currentUser != null){
            val intent = Intent(this,LoggedinActivity::class.java)
            startActivity(intent)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
    }
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left,
            R.anim.slide_out_right)
        finish()

    }

}
