package com.yashagrawal.versevista.authentication
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.yashagrawal.versevista.MainActivity
import com.yashagrawal.versevista.R
import com.yashagrawal.versevista.showToast

class LoginActivity : AppCompatActivity() {
    private lateinit var et_email: TextInputLayout
    private lateinit var et_password: TextInputLayout
    private lateinit var login_btn: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var progress: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

                //Initializing all the views
        init()

        progress.visibility = View.GONE
         //Events that are going to happen after clicking on Login button
        login_btn.setOnClickListener {
            if (validateEmail() && validatePassword()) {
                val emailText: String = et_email.editText?.text.toString().trim()
                val passwordText: String = et_password.editText?.text.toString().trim()
                progress.visibility = View.VISIBLE

                auth.signInWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(this) { task ->
                        progress.visibility = View.GONE // Hide progress bar regardless of the outcome
                        if (task.isSuccessful) {
                            // Login successful
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // Handle login failure
                            task.exception?.let { e ->
                                when (e) {
                                    is FirebaseAuthInvalidUserException -> showToast(baseContext, "Invalid user")
                                    is FirebaseAuthInvalidCredentialsException -> showToast(baseContext, "Invalid credentials")
                                    is FirebaseNetworkException -> showToast(baseContext,"No network connection")

                                    else-> {
                                        showToast(baseContext, "authentication failed")
                                        Log.d("Code problem",task.exception.toString())
                                    }
                                }
                            }
                        }
                    }
            }

        }


    }

//
//    //Initialization of variables
        private fun init() {
            et_email = findViewById(R.id.email_login)
            et_password = findViewById(R.id.password_login)
            login_btn = findViewById(R.id.login)
            auth = Firebase.auth
            progress = findViewById(R.id.progress_login)
        }



        //
//    //Validating email
        private fun validateEmail(): Boolean {
            val temp: String = et_email.editText?.text.toString().trim()
            return if (temp.isEmpty()) {
                et_email.error = "Field cannot be empty"
                false
            } else {
                et_email.error = null
                et_email.isErrorEnabled = false
                true
            }
        }

        //Validating email
        private fun validatePassword(): Boolean {
            val temp: String = et_password.editText?.text.toString().trim()
            return if (temp.isEmpty()) {
                et_password.error = "Field cannot be empty"
                false
            } else {
                et_password.error = null
                et_password.isErrorEnabled = false
                true
            }
        }


    //Going to signUp activity for signUp of the user
    fun signUp(view: View) {
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
            finish()
    }
}