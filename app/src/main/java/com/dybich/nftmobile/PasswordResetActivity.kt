package com.dybich.nftmobile

import android.app.ActivityOptions
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

class PasswordResetActivity : AppCompatActivity() {

    private val emailResetLayout : TextInputLayout by lazy {
        findViewById(R.id.EmailResetlayout)
    }
    private val emailResetET : TextInputEditText by lazy {
        findViewById(R.id.EmailresetET)
    }

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var btnreset : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.password_reset)

        btnreset = findViewById(R.id.PassResetBTN)

        emailCheck()

        btnreset.setOnClickListener(){
            val emailval = emailResetET.text.toString()

            if(emailval.isNotEmpty()) {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailval).matches()) {
                    val progressDialog = ProgressDialog(this@PasswordResetActivity)

                    progressDialog.show()

                    progressDialog.setCancelable(false)
                    progressDialog.setContentView(R.layout.progress_dialog_layout)
                    progressDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                    Firebase.auth.sendPasswordResetEmail(emailval)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this,"Please, check our inbox",Toast.LENGTH_LONG).show()

                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                overridePendingTransition(R.anim.slide_in_left,
                                    R.anim.slide_out_right)
                                finishAffinity()
                                finish()
                            }
                            else{
                                try {
                                    throw task.exception!!
                                }
                                catch (e: FirebaseAuthInvalidUserException){
                                    progressDialog.hide()
                                    emailResetLayout.error = "Email doesn't exists"
                                }

                            }
                        }

                }
                else {

                    emailResetLayout.error = "Correct form of email needed"
                }

            }

            else if(emailval.isEmpty()){
                emailResetLayout.error = "Email is empty"
            }
        }

    }

    private fun emailCheck(){

        emailResetET.addTextChangedListener {
            val id = it.toString()
            if(id == "" ){
                emailResetLayout.error = "Email is empty"
            }
            else if(id.contains(" ")){
                emailResetLayout.error = "Email mustn't contain spaces"
            }

            else {
                emailResetLayout.error = null
            }
        }

    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left,
            R.anim.slide_out_right)
        finishAffinity()
        finish()

    }


}