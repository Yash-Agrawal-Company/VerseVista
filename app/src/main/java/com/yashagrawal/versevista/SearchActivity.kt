package com.yashagrawal.versevista

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yashagrawal.versevista.adapter.PoetryListAdapter
import com.yashagrawal.versevista.adapter.UserListAdapter
import com.yashagrawal.versevista.models.PoetryModel
import com.yashagrawal.versevista.models.UserModel

class SearchActivity : AppCompatActivity() {
    private lateinit var backBtn : ImageView
    private val userList = ArrayList<UserModel>()
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userListAdapter : UserListAdapter
    private lateinit var searchBox : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        init()
        backBtn.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        // Start listening for real-time updates
        startListeningForUpdates()

        userListAdapter = UserListAdapter(this,userList)
        userRecyclerView.adapter = userListAdapter
        userRecyclerView.layoutManager = LinearLayoutManager(this)

    }
    private fun init(){
        backBtn = findViewById(R.id.back_btn)
        userRecyclerView = findViewById(R.id.usersRecyclerView)
        auth = Firebase.auth
        db = Firebase.firestore
        searchBox = findViewById(R.id.searchBox)
    }

    private fun startListeningForUpdates() {
        db.collection("Users").get()
            .addOnSuccessListener { documents ->
                userList.clear()
                for (document in documents) {
                    val currentUserData = document.toObject(UserModel::class.java)
                    if (document.id != auth.currentUser!!.uid) {
                        currentUserData.let { user ->
                            userList.add(user)
                            Log.d("Current USER id ",currentUserData.uid.toString())
                        }
                    }
                }
                userListAdapter.notifyDataSetChanged()

            }.addOnFailureListener { e ->
                showToast(this, e.localizedMessage!!)
            }
    }
}

// Now i am going to implement search box