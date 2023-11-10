package com.yashagrawal.versevista

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yashagrawal.versevista.models.PoetryModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


class WritingPoetryActivity : AppCompatActivity() {
    private lateinit var POETRY_COLLECTION : String
    private lateinit var db : FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    private lateinit var currentUser : FirebaseUser
    private lateinit var writingPad : EditText
    private lateinit var publish : Button
    private lateinit var COLLECTION: String
    @RequiresApi(Build.VERSION_CODES.O)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_writing_poetry_activity)

        // Initializing
        init()
        var poetryData = PoetryModel()
        publish.setOnClickListener {
           val poetry = writingPad.text.toString().trim()
            db.collection(COLLECTION).document(mAuth.currentUser!!.uid).get().addOnSuccessListener{document->
                if (document.exists()){
                    val formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy", Locale.ENGLISH)
                    val currentDate = LocalDate.now()
                    val formattedDate = currentDate.format(formatter).toString()
                    poetryData.userName = document.get("Username").toString()
                    poetryData.date = formattedDate
                    poetryData.poetry = "\""+poetry+"\""

//                    inputting poetry data into database
                    db.collection(POETRY_COLLECTION).add(poetryData).addOnCompleteListener {task->
                        if (task.isSuccessful){
                            showToast(this,"Poetry Published Successfully")
                            writingPad.clearFocus()
                            writingPad.text = null
                        }else{
                            showToast(this,"Some error occurred while publishing poetry")
                        }
                    }
                }else{
                    showToast(this,"Document not found")
                }

            }



        }
    }
    private fun init(){
        publish = findViewById(R.id.publish)
        POETRY_COLLECTION = "Poetries"
        db = Firebase.firestore
         mAuth = FirebaseAuth.getInstance()
         currentUser = mAuth.currentUser!!
        writingPad = findViewById(R.id.writingArea)
        COLLECTION = "Users"

    }
}