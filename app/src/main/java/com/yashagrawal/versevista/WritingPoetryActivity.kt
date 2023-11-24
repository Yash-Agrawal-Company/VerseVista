package com.yashagrawal.versevista

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.activity.addCallback
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
    private lateinit var progressBar: ProgressBar
    @RequiresApi(Build.VERSION_CODES.O)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_writing_poetry_activity)


        // Initializing
        init()
        progressBar.visibility = View.GONE
        val poetryData = PoetryModel()
        publish.setOnClickListener {
            if (isInternetAvailable()) {

                val poetry = writingPad.text.toString().trim()
                if (poetry.isNotEmpty()){
                    progressBar.visibility = View.VISIBLE
                    publish.isEnabled = false
                    db.collection(COLLECTION).document(mAuth.currentUser!!.uid).get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                val formatter =
                                    DateTimeFormatter.ofPattern("dd-MMMM-yyyy", Locale.ENGLISH)
                                val currentDate = LocalDate.now()
                                val formattedDate = currentDate.format(formatter).toString()
                                poetryData.username = document.get("Username").toString()
                                poetryData.date = formattedDate
                                poetryData.poetry = "\"$poetry\""

                                // Inputting poetry data into the database
                                db.collection(POETRY_COLLECTION).document(mAuth.currentUser!!.uid)
                                    .collection("User Poetries").add(poetryData)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            showToast(this, "Poetry Published Successfully")
                                            progressBar.visibility = View.GONE
                                            publish.isEnabled = true
                                            writingPad.clearFocus()
                                            writingPad.text = null
                                        } else {
                                            progressBar.visibility = View.GONE
                                            showToast(
                                                this,
                                                "Some error occurred while publishing poetry"
                                            )
                                            publish.isEnabled = true
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        showToast(this, "Error adding poetry data: $exception")
                                        progressBar.visibility = View.GONE
                                        publish.isEnabled = true

                                    }
                            } else {
                                showToast(this, "Document not found")
                                progressBar.visibility = View.GONE
                                publish.isEnabled = true

                            }
                        }
                        .addOnFailureListener { exception ->
                            showToast(this, "Error getting user document: $exception")
                            progressBar.visibility = View.GONE
                            publish.isEnabled = true

                        }
            }else{
                    writingPad.error = "Poetry can't be blank"
            }
            } else {
                showToast(this, "Network problem. Poetry not published.")
            }
        }



    }
    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }


    private fun init(){
        publish = findViewById(R.id.publish)
        POETRY_COLLECTION = "Poetries"
        db = Firebase.firestore
         mAuth = FirebaseAuth.getInstance()
         currentUser = mAuth.currentUser!!
        writingPad = findViewById(R.id.writingArea)
        COLLECTION = "Users"
        progressBar = findViewById(R.id.progress_publish)
    }

}