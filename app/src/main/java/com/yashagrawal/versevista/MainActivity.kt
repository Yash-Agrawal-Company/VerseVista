package com.yashagrawal.versevista

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import com.yashagrawal.versevista.authentication.SignUpActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.yashagrawal.versevista.adapter.PoetryListAdapter
import com.yashagrawal.versevista.models.PoetryModel


class MainActivity : AppCompatActivity() {
    private lateinit var profileIcon: ImageView
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var poetryRecyclerView: RecyclerView
    private lateinit var db: FirebaseFirestore
    private lateinit var POETRY_COLLECTION: String
    private lateinit var listenerRegistration: ListenerRegistration
    private var myPoetries = ArrayList<PoetryModel>()
    private lateinit var poetryListAdapter: PoetryListAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var auth: FirebaseAuth
    private lateinit var searchIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        initializing the objects
        init()

        // defining progress Bar
        defineProgressBar()

        // Scaling the profile icon
        profileIcon.scaleType = ImageView.ScaleType.CENTER_CROP


        poetryListAdapter = PoetryListAdapter(this, myPoetries)
        poetryRecyclerView.adapter = poetryListAdapter
        poetryRecyclerView.layoutManager = LinearLayoutManager(this)

        // Start listening for real-time updates
        startListeningForUpdates()
        progressBar.visibility = View.GONE
        /// Now i am going to start designing the writing poetry activity
        floatingActionButton.setOnClickListener {
            val intent = Intent(this, WritingPoetryActivity::class.java)
            startActivity(intent)
        }
        // Defining searchIcon
        searchIcon.setOnClickListener {
            val intent = Intent(this,SearchActivity::class.java)
            startActivity(intent)
        }
    }


    private fun startListeningForUpdates() {
        val poetriesCollectionRef = db.collection(POETRY_COLLECTION)
//        poetriesCollectionRef.orderBy(com.google.firebase.firestore.FieldPath.documentId(), com.google.firebase.firestore.Query.Direction.DESCENDING)

        listenerRegistration =
            poetriesCollectionRef.addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    showToast(this, "Failed to load Poetries")
                    exception.localizedMessage?.let {
                        Log.d(
                            "Loading Poetries Exception",
                            it.toString()
                        )
                    }
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    myPoetries.clear()
                    for (document in querySnapshot.documents) {
                        // Convert each document to a PoetryModel object
                        val currentPoetryData = document.toObject(PoetryModel::class.java)
                        // Add the PoetryModel to the list
                        myPoetries.add(currentPoetryData!!)
                    }
                } else {
                    showToast(this, "NO poetry till now")
                }
                poetryListAdapter.notifyDataSetChanged()

            }

    }

    private fun init() {
        poetryRecyclerView = findViewById(R.id.poetryRecyclerView)
        floatingActionButton = findViewById(R.id.floatingActionButton)
        profileIcon = findViewById(R.id.profileIcon)
        POETRY_COLLECTION = "Poetries"
        db = Firebase.firestore
        progressBar = findViewById(R.id.progressBar)
        auth = Firebase.auth
        searchIcon = findViewById(R.id.searchIcon)
    }

    // Go to Profile Activity
    fun profilePage(view: View) {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra("userId", auth.currentUser!!.uid)
        startActivity(intent)
    }

    private fun defineProgressBar() {
        val maxValue = 100
        progressBar.max = maxValue

        // Set the initial progress value
        val startValue = 0
        progressBar.progress = startValue

        // Set the desired end value for the progress
        val endValue = 100

        // Create an ObjectAnimator for smooth animation


        val progressBarAnimator =
            ObjectAnimator.ofInt(progressBar, "progress", startValue, endValue)
        progressBarAnimator.duration = 2000 // Set the duration of the animation in milliseconds
        progressBarAnimator.repeatCount = ValueAnimator.INFINITE
        // Start the animation
        progressBarAnimator.start()
    }



    // Going to design this
    private fun searchUserName() {

    }

}
