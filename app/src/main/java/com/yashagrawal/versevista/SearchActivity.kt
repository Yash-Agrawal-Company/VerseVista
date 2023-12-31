package com.yashagrawal.versevista

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yashagrawal.versevista.adapter.UserListAdapter
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

        searchBox.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not implemented
            }

            override fun onTextChanged(textValue: CharSequence?, start: Int, before: Int, count: Int) {
                val userNameValue = textValue.toString()
                val cancelIcon = ContextCompat.getDrawable(this@SearchActivity, R.drawable.cancel_icon)
                val searchIcon = ContextCompat.getDrawable(this@SearchActivity, R.drawable.search_icon)
                if (userNameValue.isNotEmpty()){
                    searchBox.setCompoundDrawablesWithIntrinsicBounds(
                        null,  // left
                        null,  // top
                        cancelIcon,  // right
                        null   // bottom
                    )
                }else{
                    searchBox.setCompoundDrawablesWithIntrinsicBounds(
                        null,  // left
                        null,  // top
                        searchIcon,  // right
                        null   // bottom
                    )
                }
//                searchBox.setOnTouchListener { v, event ->
//                    val DRAWABLE_RIGHT = 2
//                    if (event.action == MotionEvent.ACTION_UP) {
//                        if (event.rawX >= searchBox.right - searchBox.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
//                            // Your action here
//
//                            // Call performClick to satisfy lint warning
//
//                            return@setOnTouchListener true
//                        }
//                    }
//                    false
//                }


                foundedUser(userNameValue)
            }

            override fun afterTextChanged(s: Editable?) {
                // Not implemented
            }

        })

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
    private fun foundedUser(username : String){

        try {
            db.collection("Users").get()
                .addOnSuccessListener { documents ->
                    userList.clear()
                    for (document in documents) {
                        val currentUserData = document.toObject(UserModel::class.java)

                        // Checking whether the user is not the current user
                        if (document.id != auth.currentUser!!.uid) {
                            // Now checking the user name of the user searched
                            if (currentUserData.Username!!.startsWith(
                                    username.trim(),
                                    ignoreCase = true
                                )
                            ) {
                                currentUserData.let { user ->
                                    userList.add(user)
                                    Log.d("Current user id ", currentUserData.uid.toString())
                                }
                            }
                        }
                    }
                    userListAdapter.notifyDataSetChanged()

                }.addOnFailureListener { e ->
                    showToast(this, e.localizedMessage!!)
                }
        }
        catch (e : Exception){
            Log.d("Search Result problem",e.message.toString())
        }
    }
}


// Now i am going to implement search box