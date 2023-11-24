package com.yashagrawal.versevista

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yashagrawal.versevista.adapter.PoetryListAdapter
import com.yashagrawal.versevista.authentication.LoginActivity
import com.yashagrawal.versevista.models.PoetryModel

class ProfileActivity : AppCompatActivity() {
    private lateinit var fullName: TextView
    private lateinit var uname: TextView
    private lateinit var email: TextView
    private lateinit var toolbar: Toolbar
    private lateinit var poetryRecyclerView: RecyclerView
    private lateinit var logOut: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var POETRY_COLLECTION: String
    private lateinit var poetryListAdapter: PoetryListAdapter
    private var myPoetries = ArrayList<PoetryModel>()
    private lateinit var db: FirebaseFirestore
    private lateinit var listenerRegistration: ListenerRegistration
    private var userName: String? = null
    private lateinit var edit_profile_btn: TextView

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
                        userName = documentSnapshot.getString("Username")
                        val emailAddress = documentSnapshot.getString("Email address")
                        fullName.text = name
                        uname.text = userName
                        email.text = "Email : $emailAddress"
                        uname.visibility = View.VISIBLE
                        fullName.visibility = View.VISIBLE
                        email.visibility = View.VISIBLE

                    } else {
                        showToast(this, "User's details does not exists")
                    }
                }
                .addOnFailureListener { e ->
                    showToast(this, "Some error occurred\n" + e.message)
                }
        }


        // Start listening for real-time updates
        startListeningForUpdates()

        poetryListAdapter = PoetryListAdapter(this, myPoetries)
        poetryRecyclerView.adapter = poetryListAdapter
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
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            val dialogNo = dialog.findViewById<Button>(R.id.no)
            dialogNo.setOnClickListener {
                dialog.dismiss()
            }
        }
        edit_profile_btn.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.edit_profile_dialog)
            dialog.setCancelable(true)
            dialog.show()
            dialog.window!!.setBackgroundDrawable(ColorDrawable(0))

            val save_btn = dialog.findViewById<Button>(R.id.send_email)
            save_btn.setOnClickListener {
                val changeNameValue =
                    dialog.findViewById<EditText>(R.id.change_name).text.toString().trim()
                val changeUsernameValue =
                    dialog.findViewById<EditText>(R.id.change_username).text.toString().trim()
                val userData = mutableMapOf<String, Any>()
                userData["Name"] = changeNameValue
                userData["Username"] = changeUsernameValue
                //Taking user details and uploading in the cloud
                val COLLECTION = db.collection("Users")
                val currentUserUid = auth.currentUser?.uid
                currentUserUid?.let { uid ->
                    COLLECTION.document(uid).update(userData).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            showToast(this, "Changes Saved")
                        } else {
                            task.exception!!.localizedMessage?.let { it1 ->
                                showToast(
                                    this,
                                    it1.toString()
                                )
                            }
                        }
                    }
                    val poetryData = mutableMapOf<String, Any>()
                    poetryData["username"] = changeUsernameValue
                    val collectionRef = db.collection(POETRY_COLLECTION).document(currentUserUid)
                        .collection("User Poetries")
                    collectionRef.get().addOnSuccessListener { documents ->
                        for (document in documents) {
                            val documentId = document.id
                            collectionRef.document(documentId).update(poetryData)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        showToast(this, "Changes Saved")
                                    } else {
                                        task.exception!!.localizedMessage?.let { it1 ->
                                            showToast(
                                                this,
                                                it1.toString()
                                            )
                                        }
                                    }
                                }
                        }
                    }
                }
            }
        }
    }

    private fun init() {
        toolbar = findViewById(R.id.toolbar)
        poetryRecyclerView = findViewById(R.id.poetriesPublished)
        logOut = findViewById(R.id.logOut)
        fullName = findViewById(R.id.fullName)
        auth = Firebase.auth
        uname = findViewById(R.id.uname)
        email = findViewById(R.id.email)
        POETRY_COLLECTION = "Poetries"
        db = Firebase.firestore
        edit_profile_btn = findViewById(R.id.edit_profile)
    }

    private fun startListeningForUpdates() {
        db.collection("Poetries").document(auth.currentUser!!.uid).collection("User Poetries").get()
            .addOnSuccessListener { snapshots ->
                myPoetries.clear()
                for (snapshot in snapshots) {
                    val currentPoetryData = snapshot.toObject(PoetryModel::class.java)

                    currentPoetryData.let { poetry ->
                        myPoetries.add(poetry)
                    }

                }
                poetryListAdapter.notifyDataSetChanged()

            }.addOnFailureListener { e ->
                showToast(this, e.localizedMessage!!)
            }
    }

    private fun getUserName(): String? {

        var uName = ""
        val COLLECTION = "Users"
        db.collection(COLLECTION).document(auth.currentUser!!.uid).get().addOnSuccessListener {
            uName = it.getString("Username")!!
            showToast(this, "Correct username \n$uName")
        }
        return uName

    }


}

