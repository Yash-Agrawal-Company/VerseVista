package com.yashagrawal.versevista

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yashagrawal.versevista.adapter.PoetryListAdapter
import com.yashagrawal.versevista.authentication.LoginActivity
import com.yashagrawal.versevista.models.PoetryModel

class ProfileActivity : AppCompatActivity() {
    private lateinit var fullName : TextView
    private lateinit var uname : TextView
    private lateinit var email : TextView
    private lateinit var toolbar : Toolbar
    private lateinit var poetryRecyclerView : RecyclerView
    private lateinit var logOut : TextView
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

       init()
        setSupportActionBar(toolbar)
        uname.visibility = View.GONE
        fullName.visibility = View.GONE
        email.visibility = View.GONE

// Assuming currentUserUid is the UID of the authenticated user
        val db = Firebase.firestore
        val COLLECTION = db.collection("Users")
        val currentUserUid = auth.currentUser?.uid
        currentUserUid?.let { uid ->
            COLLECTION.document(uid).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val name = documentSnapshot.getString("Name")
                        val userName = documentSnapshot.getString("Username")
                        val emailAddress = documentSnapshot.getString("Email address")
                        fullName.text = name
                        uname.text = userName
                        email.text = "Email : ${emailAddress}"
                        uname.visibility = View.VISIBLE
                        fullName.visibility = View.VISIBLE
                        email.visibility = View.VISIBLE

                    } else {
                        showToast(this,"User's details does not exists")
                    }
                }
                .addOnFailureListener { e ->
                    showToast(this,"Some error occurred")
                }
        }

        var poetry = "\"In love's gentle embrace, hearts find their sweetest song,\n" +
                "A dance of souls, where two spirits truly belong.\n" +
                "Eternal bliss in every beat, love\\'s melody, forever strong.\""
        var p1 = PoetryModel("yash_agrawal",poetry,"4-11-2023")
        var myPoetries = arrayListOf<PoetryModel>(p1,p1,p1,p1)
        poetryRecyclerView.adapter = PoetryListAdapter(this,myPoetries)
        poetryRecyclerView.layoutManager = LinearLayoutManager(this)

        logOut.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.log_out_dialog)
            dialog.setCancelable(false)
            dialog.show()
            dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
            val dialogYes = dialog.findViewById<Button>(R.id.yes)
            dialogYes.setOnClickListener {
                auth.signOut()
                val intent = Intent(this,LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            val dialogNo = dialog.findViewById<Button>(R.id.no)
            dialogNo.setOnClickListener {
                dialog.dismiss()
            }
        }
    }
    private fun init(){
        toolbar = findViewById(R.id.toolbar)
        poetryRecyclerView = findViewById(R.id.poetriesPublished)
        logOut = findViewById(R.id.logOut)
        fullName = findViewById(R.id.fullName)
        auth = Firebase.auth
        uname = findViewById(R.id.uname)
        email = findViewById(R.id.email)
    }
}