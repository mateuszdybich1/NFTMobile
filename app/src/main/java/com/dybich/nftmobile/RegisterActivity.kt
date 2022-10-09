package com.dybich.nftmobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class RegisterActivity : AppCompatActivity() {

    private val nickLayout : TextInputLayout by lazy {
        findViewById(R.id.Nicklayout)
    }
    private val nickET : TextInputEditText by lazy {
        findViewById(R.id.NickET)
    }

    private val emailLayout : TextInputLayout by lazy {
        findViewById(R.id.Email_reg_layout)
    }
    private val emailET : TextInputEditText by lazy {
        findViewById(R.id.Email_reg_ET)
    }
    private val passLayout : TextInputLayout by lazy {
        findViewById(R.id.Password_reg_layout)
    }
    private val passET : TextInputEditText by lazy {
        findViewById(R.id.Password_reg_ET)
    }
    private val repPassLayout : TextInputLayout by lazy {
        findViewById(R.id.Password_rep_reg_layout)
    }
    private val repPassET : TextInputEditText by lazy {
        findViewById(R.id.Password_rep_reg_ET)
    }
    private  var validNick :Boolean = false
    private  var validEmail :Boolean = false
    private  var validPass :Boolean = false
    private  var validRepPass :Boolean = false




    private lateinit var currentuser : FirebaseUser
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var registerbtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)





        database = Firebase.database.reference

        auth = Firebase.auth
        registerbtn = findViewById(R.id.btnregister)

        nickCheck()
        emailCheck()
        passCheck()
        repPassCheck()
        emailET.setOnFocusChangeListener{_,focused->
            if(!focused){
                emailValidation()
            }

        }
        registerbtn.setOnClickListener(){
                var logvar = nickET.text.toString()
                var emailvar = emailET.text.toString()
                var passvar = passET.text.toString()
                var reppassvar = repPassET.text.toString()




                if(validNick == true && validEmail == true && validPass == true && validRepPass == true && passvar.length >=6){
                    if(android.util.Patterns.EMAIL_ADDRESS.matcher(emailvar).matches()){
                        database.child("logins").child(logvar).get().addOnSuccessListener {
                            if(it.value == logvar)
                            {
                                nickLayout.error = "Nickname already used"
                                nickET.setOnFocusChangeListener{_,focused->
                                    if(!focused && logvar == nickET.text.toString()){
                                        nickLayout.error = "Nickname already used"

                                    }
                                    else if( logvar != nickET.text.toString()){
                                        nickLayout.error = null
                                    }
                                }
                            }
                            else
                            {
                                if(passvar == reppassvar && passvar.length >= 6){

                                    auth.createUserWithEmailAndPassword(emailvar, passvar)
                                        .addOnCompleteListener(this) { task ->
                                            if (task.isSuccessful) {
                                                // Sign in success, update UI with the signed-in user's information
                                                Log.d("TAG", "createUserWithEmail:success")
                                                val confirmed = "false"
                                                addUser(logvar,emailvar,confirmed)

                                            }
                                            else{

                                                emailLayout.error = "Email exists. Please sign in"
                                                emailET.setOnFocusChangeListener { _, focused ->
                                                    if (!focused && emailvar == emailET.text.toString()) {
                                                        emailLayout.error = "Email exists. Please sign in"

                                                    } else if (emailvar != emailET.text.toString()) {
                                                        emailLayout.error = null
                                                    }
                                                }
                                            }

                                        }

                                }

                                else if (passvar != reppassvar){

                                    repPassLayout.error = "Repeat password is incorrect"
                                    repPassET.setOnFocusChangeListener { _, focused ->
                                        if (!focused && (reppassvar != passET.text.toString() || reppassvar == repPassET.text.toString())) {
                                            passLayout.error = "Repeat password is incorrect"

                                        } else if (reppassvar == passET.text.toString() && reppassvar == repPassET.text.toString()) {
                                            passLayout.error = null
                                        }
                                    }
                                }
                                else if(passvar.length < 6) {

                                    passLayout.error = "Password must be at least 6 characters long"
                                    passET.setOnFocusChangeListener { _, focused ->
                                        if (!focused && passvar == passET.text.toString()) {
                                            passLayout.error = "Password must be at least 6 characters long"

                                        } else if (passvar != passET.text.toString()) {
                                            passLayout.error = null
                                        }
                                    }

                                }
                            }
                        }
                    }
                    else{
                        emailLayout.error = "Correct form of email needed"
                    }


                }

                else{
                    if (validNick == false){
                        if(logvar.length > 15){
                            nickLayout.error = "Nickname too long"
                        }
                        else if(logvar.contains(" ")){
                            nickLayout.error = "Nickname mustn't contain spaces"

                        }
                        else if(logvar == ""){
                            nickLayout.error = "Nickname is empty"
                        }

                    }
                    if (validEmail == false){
                        if(emailvar.contains(" ")){
                            emailLayout.error = "Email mustn't contain spaces"
                        }
                        else if(emailvar == ""){
                            emailLayout.error = "Email is empty"
                        }

                    }
                    if (validPass == false || passvar.length < 6){
                        if(passvar.length > 20){
                            nickLayout.error = "Password too long"
                        }
                        else if(passvar.contains(" ")){
                            passLayout.error = "Password mustn't contain spaces"

                        }

                        else if(passvar == ""){
                            passLayout.error = "Password is empty"
                        }
                        else if(passvar.length < 6){
                            passLayout.error = "Password must be at least 6 characters long"
                        }
                    }
                    if (validRepPass == false){
                        if(repPassET.text.toString() != passET.text.toString() && passET.text.toString() != "" ){
                            repPassLayout.error = "Repeat password is incorrect"

                        }

                    }
                }








        }

    }

    fun addUser(login: String, email: String,confirmed:String)
    {
         currentuser = auth.currentUser!!
        val uid = currentuser?.uid
        val user = User(login, email,confirmed)
        database.child("users").child(uid.toString()).setValue(user)
        database.child("logins").child(login).setValue(login)
        val intent = Intent(this,ConfirmMailActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right,
            R.anim.slide_out_left);
        finish()
    }

    private fun nickCheck(){
        nickET.addTextChangedListener {
            val id = it.toString()
            if(id == "" ){
                nickLayout.error = "Nickname is empty"
                validNick =false
            }
            else if(id.contains(" ")){
                nickLayout.error = "Nickname mustn't contain spaces"
                validNick = false
            }
            else if(id.length > 15){
                nickLayout.error = "Nickname too long"
                validNick = false
            }

            else {
                nickLayout.error = null
                validNick = true

            }

        }
    }

    private fun emailCheck(){
        emailET.addTextChangedListener {
            val id = it.toString()
            if(id == "" ){
                emailLayout.error = "Email is empty"
                validNick =false
            }
            else if(id.contains(" ")){
                emailLayout.error = "Email mustn't contain spaces"
                validEmail = false
            }

            else {
                emailLayout.error = null
                validEmail = true

            }

        }
    }
    private fun passCheck(){
        passET.addTextChangedListener {
            val id = it.toString()
            if(id == "" ){
                passLayout.error = "Password is empty"
                validPass =false
            }
            else if(id.contains(" ")){
                passLayout.error = "Password mustn't contain spaces"
                validPass =false
            }
            else if(id.length > 20){
                passLayout.error = "Password too long"
                validPass = false
            }
            else {
                passLayout.error = null
                validPass =true

            }

        }
    }
    private fun repPassCheck(){
        repPassET.addTextChangedListener {
            val id = it.toString()
            if(id != passET.text.toString() && passET.text.toString() != "" ){
                repPassLayout.error = "Repeat password is incorrect"
                validRepPass =false
            }

            else {
                repPassLayout.error = null
                validRepPass =true

            }

        }
    }
    private fun emailValidation() {
        if (emailET.text.toString() != "" && !emailET.text.toString().contains(" ")) {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailET.text.toString()).matches()) {
                emailLayout.error = "Correct form of email needed"

            } else {
                emailLayout.error = null

            }
        }
    }
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)

        startActivity(intent)

        overridePendingTransition(R.anim.slide_in_left,
            R.anim.slide_out_right);
        finish()
    }


}