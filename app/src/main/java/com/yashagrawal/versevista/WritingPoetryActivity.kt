package com.yashagrawal.versevista

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class WritingPoetryActivity : AppCompatActivity() {
    private lateinit var POETRY_COLLECTION : String
    private lateinit var db : FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    private lateinit var currentUser : FirebaseUser
    private lateinit var writingPad : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_writing_poetry_activity)

        init()
       val userName = intent.getStringExtra("com.yashagrawal.versevista.authentication.USER_NAME")
//        db.collection(POETRY_COLLECTION).document(userNameText).collection(POETRY_COLLECTION).document()
        writingPad.setText(userName)
    }
    private fun init(){
        POETRY_COLLECTION = "Poetries"
        db = Firebase.firestore
         mAuth = FirebaseAuth.getInstance()
         currentUser = mAuth.currentUser!!
        writingPad = findViewById(R.id.writingArea)
    }
}