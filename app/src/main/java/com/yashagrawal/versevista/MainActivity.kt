package com.yashagrawal.versevista
import com.yashagrawal.versevista.authentication.SignUpActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yashagrawal.versevista.adapter.PoetryListAdapter
import com.yashagrawal.versevista.models.PoetryModel


class MainActivity : AppCompatActivity() {
    private lateinit var profileIcon : ImageView
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var poetryRecyclerView : RecyclerView
    private lateinit var db : FirebaseFirestore
    private lateinit var POETRY_COLLECTION : String
    private lateinit var listenerRegistration: ListenerRegistration
    private var myPoetries = ArrayList<PoetryModel>()
    private lateinit var poetryListAdapter : PoetryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        initializing the objects
        init()
        // Scaling the profile icon
        profileIcon.scaleType = ImageView.ScaleType.FIT_XY


        poetryListAdapter = PoetryListAdapter(this, myPoetries)
        poetryRecyclerView.adapter = poetryListAdapter
        poetryRecyclerView.layoutManager = LinearLayoutManager(this)

        // Start listening for real-time updates
        startListeningForUpdates()

        /// Now i am going to start designing the writing poetry activity
        floatingActionButton.setOnClickListener {
            val intent = Intent(this,WritingPoetryActivity::class.java)
            startActivity(intent)
        }
    }
    private fun startListeningForUpdates() {
        val collectionRef = db.collection(POETRY_COLLECTION)

        // Order by document ID in descending order (newest first)
        collectionRef.orderBy(com.google.firebase.firestore.FieldPath.documentId(), com.google.firebase.firestore.Query.Direction.DESCENDING)

        // Set up a listener for real-time updates
        listenerRegistration = collectionRef.addSnapshotListener { documentSnapshot, exception->
            documentSnapshot?.let {
                myPoetries.clear()

                for (document in it.documents) {
                    val currentPoetryData = document.toObject(PoetryModel::class.java)
                    currentPoetryData?.let { poetry -> myPoetries.add(poetry) }
                }
                if(exception != null){
                showToast(this,exception.message.toString())
                }
                // Notify the adapter that the data has changed
                poetryListAdapter.notifyDataSetChanged()
            }
        }
    }
    private fun init(){
        poetryRecyclerView = findViewById(R.id.poetryRecyclerView)
        floatingActionButton = findViewById(R.id.floatingActionButton)
        profileIcon = findViewById(R.id.profileIcon)
        POETRY_COLLECTION = "Poetries"
        db = Firebase.firestore
    }

    // Go to Profile Activity
    fun profilePage(view: View) {
        val intent = Intent(this,ProfileActivity::class.java)
        startActivity(intent)
    }
}