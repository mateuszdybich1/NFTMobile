package com.dybich.nftmobile

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import java.lang.Runnable
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {

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
    var  url = "https://marketnft.000webhostapp.com/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


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
        val progressDialog = ProgressDialog(this@MainActivity)



        btnlogin.setOnClickListener() {
            val emailval = emailET.text.toString()
            val passval = passwordET.text.toString()

            if(emailval.isNotEmpty() && passval.isNotEmpty()) {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailval).matches()) {


                    progressDialog.show()

                    progressDialog.setCancelable(false)
                    progressDialog.setContentView(R.layout.progress_dialog_layout)
                    progressDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

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
                                        val rand = Random.nextInt(1111,9999)
                                        codeVerification(rand)
                                    }
                                }

                        } else {
                            try {
                                throw it.exception!!
                            } catch (e: FirebaseAuthInvalidUserException) {
                                progressDialog.hide()
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
                                progressDialog.hide()
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

        if(auth.currentUser!=null){


            progressDialog.show()

            progressDialog.setCancelable(false)
            progressDialog.setContentView(R.layout.progress_dialog_layout)
            progressDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            Handler().postDelayed({

                val intent = Intent(this, LoggedinActivity::class.java)
                startActivity(intent)
                progressDialog.hide()
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                finish()
            }, 2000)



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
    private fun codeVerification(rand:Int){
        Log.d("TAG", rand.toString())
        val value = rand.toString()
        val currentUser = auth.currentUser
        val email = currentUser?.email.toString()
        Log.d("TAG", email)


        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val postRequest = object: StringRequest(Method.POST,url,  Response.Listener<String?> {
                response ->
            Toast.makeText(this,response, Toast.LENGTH_LONG).show()

            val intent = Intent(this,ConfirmMailActivity::class.java)
            intent.putExtra("rand", value)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);
            finish()

        }, Response.ErrorListener {
                error ->
            Log.d("TAG", error.message.toString())
            Toast.makeText(this,error.message.toString(), Toast.LENGTH_LONG).show()
        })
        {
            override fun getParams() : Map<String,String>{
                val params = HashMap<String,String>()
                params["email"]= email
                params["code"] = value
                return params

            }
        }
        requestQueue.add(postRequest)
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

    fun tvClicked(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right,
            R.anim.slide_out_left);
    }

    fun forgotPassTV(view: View) {
        val intent = Intent(this, PasswordResetActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right,
            R.anim.slide_out_left);
    }
}