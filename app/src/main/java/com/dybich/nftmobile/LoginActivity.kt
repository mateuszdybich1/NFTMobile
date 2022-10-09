package com.dybich.nftmobile

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class LoginActivity: AppCompatActivity() {


private val passwordLayout : TextInputLayout by lazy {
    findViewById(R.id.Passwordlayout)
}
    private val passwordET : TextInputEditText by lazy {
        findViewById(R.id.PasswordET)
    }
    private val emailLayout : TextInputLayout by lazy {
        findViewById(R.id.Emaillayout)
    }
    private val emailET : TextInputEditText by lazy {
        findViewById(R.id.EmailET)
    }



    private lateinit var database:DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var btnlogin : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)


        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference
        btnlogin = findViewById(R.id.btnlogin)

        emailCheck()
        passwordCheck()


        emailET.setOnFocusChangeListener{_,focused->
           if(!focused){
                emailValidation()
            }

        }

        btnlogin.setOnClickListener() {
            val emailval = emailET.text.toString()
            val passval = passwordET.text.toString()

            if(emailval.isNotEmpty() && passval.isNotEmpty()) {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailval).matches()) {


                    auth.signInWithEmailAndPassword(emailval, passval).addOnCompleteListener {

                    if (it.isSuccessful) {

                        val currentUser = auth.currentUser
                        database.child("users").child(currentUser?.uid.toString()).child("isconfirmed").get()
                            .addOnSuccessListener { it ->
                                if (it.value == "true") {
                                    val intent = Intent(this, LoggedinActivity::class.java)
                                    startActivity(intent)
                                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                                    finish()
                                } else {
                                    val intent = Intent(this, ConfirmMailActivity::class.java)
                                    startActivity(intent)
                                    overridePendingTransition(
                                        R.anim.slide_in_right,
                                        R.anim.slide_out_left
                                    );
                                    finish()
                                }
                            }

                        } else {
                        try {
                            throw it.exception!!
                        } catch (e: FirebaseAuthInvalidUserException) {

                            emailLayout.error = "Email does not exists"
                            emailET.setOnFocusChangeListener{_,focused->
                                if(!focused && emailval == emailET.text.toString()){
                                    emailLayout.error = "Email does not exists"

                                }
                                else if( emailval != emailET.text.toString()){
                                    emailLayout.error = null
                                }


                            }

                        } catch (e: FirebaseAuthException){
                            passwordLayout.error = "Wrong password"

                        }

                        }
                    }
                }
                else {
                    emailLayout.error = "Correct form of email needed"
                }

            }
            else if(passval.isEmpty() && emailval.isNotEmpty()){
                passwordLayout.error = "Password is empty"
            }
            else if(emailval.isEmpty() && passval.isNotEmpty()){
                emailLayout.error = "Email is empty"
            }
            else if (emailval.isEmpty() && passval.isEmpty()){
                emailLayout.error = "Email is empty"
                passwordLayout.error = "Password is empty"
            }

        }
        if(auth.currentUser != null){
            database.child("users").child(auth.currentUser?.uid.toString()).child("isconfirmed").get()
                .addOnSuccessListener { it ->
                    if (it.value == "true") {
                        val intent = Intent(this, LoggedinActivity::class.java)
                        startActivity(intent)
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                        finish()
                    } else {
                        val intent = Intent(this, ConfirmMailActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        );
                        finish()
                    }
                }
        }
    }
private fun emailCheck(){

        emailET.addTextChangedListener {
            val id = it.toString()
            if(id == "" ){
                emailLayout.error = "Email is empty"

            }
            else if(id.contains(" ")){
                emailLayout.error = "Email mustn't contain spaces"

            }


            else {
                emailLayout.error = null


            }


    }

}
    private fun passwordCheck(){
        passwordET.addTextChangedListener {
            val id = it.toString()
            if(id == "" ){
                passwordLayout.error = "Password is empty"
            }
            else if(id.contains(" ")){
                passwordLayout.error = "Password mustn't contain spaces"
            }

            else {
                passwordLayout.error = null

            }

        }
    }
    private fun emailValidation(){
if(emailET.text.toString() != "" && !emailET.text.toString().contains(" ")){
    if(!android.util.Patterns.EMAIL_ADDRESS.matcher(emailET.text.toString()).matches())
    {
        emailLayout.error = "Correct form of email needed"

    }
    else{
        emailLayout.error = null

    }
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
