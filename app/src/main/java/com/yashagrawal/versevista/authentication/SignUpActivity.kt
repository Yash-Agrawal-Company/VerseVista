package com.yashagrawal.versevista.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yashagrawal.versevista.MainActivity
import com.yashagrawal.versevista.R
import com.yashagrawal.versevista.showToast


class SignUpActivity : AppCompatActivity() {
    private lateinit var fullName: TextInputLayout
    private lateinit var userName: TextInputLayout
    private lateinit var email: TextInputLayout
    private lateinit var password: TextInputLayout
    private lateinit var confirmPassword: TextInputLayout
    private lateinit var auth: FirebaseAuth
    private lateinit var submit: Button
    private lateinit var progress: ProgressBar
    private lateinit var COLLECTION : String
    private lateinit var db : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        init()
        progress.visibility = View.GONE
        submit.setOnClickListener {
            if (validateFullName() && validateUserName() && emailValidator(email) && validatePassword() && validateConfirmPassword()) {
                val fullNameText = fullName.editText?.text.toString().trim()
                val userNameText = userName.editText?.text.toString().trim()
                val emailText = email.editText?.text.toString().trim()
                val passwordText = password.editText?.text.toString().trim()
                progress.visibility = View.VISIBLE
                auth.createUserWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            var userData = mutableMapOf<String,String>()
                            userData["Name"] = fullNameText
                            userData["Username"] = userNameText
                            userData["Email address"] = emailText
                            //Taking user details and uploading in the cloud
                            db.collection(COLLECTION).document(auth.currentUser!!.uid).set(userData)
                            showToast(this, "User Created Successfully")
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            progress.visibility = View.GONE
                            finish()
                        } else {
                            showToast(
                                baseContext,
                                "authentication Failed\n" + task.exception?.message.toString()
                            )
                            progress.visibility = View.GONE

                        }
                    }
            } else
                return@setOnClickListener
        }

    }

    //Initialization of variables
    fun init() {
        fullName = findViewById(R.id.fullName)
        userName = findViewById(R.id.uname)
        email = findViewById(R.id.email)
        password = findViewById(R.id.pass)
        confirmPassword = findViewById(R.id.passc)
        submit = findViewById(R.id.submit)
        auth = Firebase.auth
        progress = findViewById(R.id.progress)
        COLLECTION = "Users"
        db = Firebase.firestore
    }


    //Moving back to Login Activity for sign in
    fun loginPage(view: View) {
        val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun validateFullName(): Boolean {
        val temp = fullName.editText!!.text.toString().trim()
        return if (temp.isEmpty()) {
            fullName.error = "Field cannot be empty"
            false
        } else {
            fullName.error = null
            fullName.isErrorEnabled = false
            true
        }
    }

    private fun emailValidator(etMail: TextInputLayout?): Boolean {

        // extract the entered data from the EditText
        val emailToText = etMail?.editText?.text.toString().trim()
        if (emailToText.isEmpty()) {
            etMail?.error = "Field cannot be empty"
            return false
        } else if (emailToText.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailToText)
                .matches()
        ) {
            etMail?.error = null
            etMail?.isErrorEnabled = false
            return true
        } else {
            etMail?.error = "Enter valid Email address !"
            return false
        }
    }

    private fun validateUserName(): Boolean {
        val i = 0
        val temp: String = userName.editText!!.text.toString().trim()
        return if (temp.isEmpty()) {
            userName.error = "Field cannot be empty"
            false
        } else if (!noSpace(temp)) {
            userName.error = "Username cannot contain spaces"
            false
        } else if (temp.length >= 18) {
            userName.error = "Username is too long"
            false
        } else {
            userName.error = null
            userName.isErrorEnabled = false
            true
        }
    }

    private fun validatePassword(): Boolean {
        val temp = password.editText!!.text.toString().trim()
        return if (temp.isEmpty()) {
            password.error = "Password cannot be empty"
            false
        } else if (!noSpace(temp)) {
            password.error = "Password must not contain spaces"
            false
        } else if (temp.length < 8 || temp.length > 16) {
            password.error = "password range must be from 8 - 16 characters"
            false
        } else {
            password.error = null
            password.isErrorEnabled = false
            true
        }
    }

    private fun validateConfirmPassword(): Boolean {
        val temp = confirmPassword.editText!!.text.toString().trim()

        return if (temp.isEmpty()) {
            confirmPassword.error = "Field cannot be empty"
            false
        } else if (temp != password.editText!!.text.toString()) {
            confirmPassword.error = "Password mismatch"
            false
        } else {
            confirmPassword.error = null
            confirmPassword.isErrorEnabled = false
            true
        }

    }

    private fun noSpace(temp: String): Boolean {
        var validate = false
        for (element in temp) {
            validate = if (element == ' ') {
                return false
            } else {
                true
            }
        }
        return validate
    }
}