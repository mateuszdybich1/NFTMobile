package com.dybich.nftmobile

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.core.utilities.Validation
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private lateinit var login:EditText
    private lateinit var email:EditText
    private lateinit var password: EditText
    private lateinit var reppass: EditText



    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var registerbtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        login = findViewById<EditText?>(R.id.registerLogin)
        email = findViewById<EditText?>(R.id.registerMail)
        password = findViewById<EditText?>(R.id.registerPass)
        reppass = findViewById<EditText?>(R.id.registerRepPass)


        database = Firebase.database.reference


        auth = Firebase.auth
        registerbtn = findViewById(R.id.btnregister)

        registerbtn.setOnClickListener(){
                var logvar = login.text.toString()
                var emailvar = email.text.toString()
                var passvar = password.text.toString()
                var reppassvar = reppass.text.toString()




                if(logvar.isNotEmpty() && emailvar.isNotEmpty() && passvar.isNotEmpty() && reppassvar.isNotEmpty() ){
                    database.child("users").child(logvar).child("username").get().addOnSuccessListener {
                    if(it.value == logvar)
                    {
                        Toast.makeText(this,"Please provide not used login", Toast.LENGTH_LONG).show()
                    }
                    else
                    {
                        if(passvar == reppassvar){

                            auth.createUserWithEmailAndPassword(emailvar, passvar)
                                .addOnCompleteListener(this) { task ->
                                    if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("TAG", "createUserWithEmail:success")
                                        addUser(logvar,emailvar,passvar)

                                    }
                                    else if(android.util.Patterns.EMAIL_ADDRESS.matcher(emailvar).matches()){
                                        Toast.makeText(this,"Email exists. Please sign in", Toast.LENGTH_LONG).show()
                                    }
                                    else if(passvar.length < 6) {
                                        Toast.makeText(this,"Password must be at least 6 characters long", Toast.LENGTH_LONG).show()

                                    }
                                }

                        }
                        else{

                            Toast.makeText(this,"Password doesn't match the confirm password", Toast.LENGTH_LONG).show()
                        }
                    }
                 }

                }
                else if(logvar.isEmpty() || database.child("users").child(logvar).child("username").toString() == logvar){
                    Toast.makeText(this,"Please provide not used login", Toast.LENGTH_LONG).show()
                }
                else if(emailvar.isEmpty() || database.child("users").child(logvar).child("email").toString() == emailvar){
                    Toast.makeText(this,"Please provide not used email", Toast.LENGTH_LONG).show()
                }
            else if(passvar.isEmpty() || reppassvar.isEmpty()){
                    Toast.makeText(this,"Please provide password and confirm password", Toast.LENGTH_LONG).show()
                }


        }
        if(auth.currentUser != null){
            val intent = Intent(this,LoggedinActivity::class.java)
            startActivity(intent)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
    }

    fun addUser(login: String, email: String, password: String)
    {
        val user = User(login, email,password)
        database.child("users").child(login).setValue(user)

        val intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)

        startActivity(intent)

        overridePendingTransition(R.anim.slide_in_left,
            R.anim.slide_out_right);
        finish()
    }


}