package com.yashagrawal.versevista.adapter
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.runBlocking
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.yashagrawal.versevista.ProfileActivity
import com.yashagrawal.versevista.R
import com.yashagrawal.versevista.models.PoetryModel
import com.yashagrawal.versevista.models.RatingModel
import com.yashagrawal.versevista.showToast
import kotlinx.coroutines.tasks.await

class PoetryListAdapter(var context : Context, var poetryList : ArrayList<PoetryModel>) : RecyclerView.Adapter<PoetryListAdapter.PoetryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoetryViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.poetry_list_item, parent, false)
        return PoetryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return poetryList.size
    }

    override fun onBindViewHolder(holder: PoetryViewHolder, position: Int) {
        val currentPoetry = poetryList[position]
        holder.userName.text = currentPoetry.username
        holder.poetry.text = currentPoetry.poetry
        holder.date.text = currentPoetry.date
        holder.globalRating.text = "Global rating : " + currentPoetry.globalRating.toString()
        holder.ratingBar.rating = getRating(currentPoetry.poetryId)
        holder.userName.setOnClickListener {
            // Go to profile activity after clicking on username from the poetry frame
            showProfile(currentPoetry.username.toString())
        }
        holder.ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            setUpdateRating(currentPoetry.poetryId.toString(),rating)
        }

    }

    class PoetryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.userName)
        val poetry: TextView = itemView.findViewById(R.id.poetry)
        val date: TextView = itemView.findViewById(R.id.dateOfPublished)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val globalRating: TextView = itemView.findViewById(R.id.globalRating)
    }

    private fun showProfile(uName: String?) {
        val db: FirebaseFirestore = Firebase.firestore
        var uid: String = ""
        db.collection("Users").get().addOnSuccessListener { documents ->
            for (document in documents.documents) {
                val userName = document.getString("Username")
                if (userName!! == uName) {
                    uid = document.id
                    val intent = Intent(context, ProfileActivity::class.java)
                    intent.putExtra("userId", uid)
                    context.startActivity(intent)
                    return@addOnSuccessListener
                }
            }
        }.addOnFailureListener {
            showToast(context, "User does not exists")
        }

    }

    private fun setUpdateRating(poetryId: String,rating : Float){
        val db: FirebaseFirestore = Firebase.firestore
        val auth: FirebaseAuth = Firebase.auth
        val ratingRef = db.collection("Poetries").document(poetryId).collection("Rating")

        // Check if the user has already rated
        ratingRef.document(auth.currentUser!!.uid).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                    // User has already rated, update the existing rating
                    ratingRef.document(auth.currentUser!!.uid).update("rating", rating)
                        .addOnSuccessListener {
                            Log.d("Rating Uploaded", "Rating updated successfully")
                        }
                        .addOnFailureListener {
                            showToast(context, "Failed to update rating")
                        }
                } else {
                    // User is rating for the first time, create a new rating document
                    val ratingUser =
                        RatingModel(userId = auth.currentUser!!.uid, rating = rating)
                    ratingRef.document(auth.currentUser!!.uid).set(ratingUser)
                        .addOnSuccessListener {
                            Log.d("Rating updated", "Rating updated successfully")
                        }
                        .addOnFailureListener {
                            showToast(context, "Failed to add rating")
                        }
                }
            } else {
                showToast(context, "Error checking existing rating")
            }
        }
    }

    private fun getRating(poetryId: String?): Float = runBlocking {
        val db: FirebaseFirestore = Firebase.firestore
        val auth: FirebaseAuth = Firebase.auth

        val document = try {
            db.collection("Poetries").document(poetryId!!)
                .collection("Rating").document(auth.currentUser!!.uid)
                .get().await()
        } catch (e: Exception) {
            showToast(context, "Some error occurred while getting rating details")
            null
        }
        if (document != null && document.exists()) {
            val ratingUserObject = document.toObject(RatingModel::class.java)
             ratingUserObject?.rating ?: 0.0f
        } else {
            0.0f
        }
    }
}
