package com.yashagrawal.versevista.adapter;

import android.content.Context;
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yashagrawal.versevista.ProfileActivity
import com.yashagrawal.versevista.R
import com.yashagrawal.versevista.models.UserModel
import com.yashagrawal.versevista.showToast

class UserListAdapter(var context: Context, val userList: ArrayList<UserModel>) :
    RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.user_list_item, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.username.text = currentUser.Username
        holder.userCard.setOnClickListener{
            showProfile(currentUser.Username.toString())
        }
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.user_name)
        val userCard : LinearLayout = itemView.findViewById(R.id.foundUser)
    }
    private fun showProfile(uName : String?){
        val db: FirebaseFirestore = Firebase.firestore
        var uid  = ""
        db.collection("Users").get().addOnSuccessListener {documents->
            for (document in documents.documents){
                val userName = document.getString("Username")
                if (userName!! == uName){
                    uid = document.id
                    val intent = Intent(context, ProfileActivity::class.java)
                    intent.putExtra("userId",uid)
                    context.startActivity(intent)
                    return@addOnSuccessListener
                }
            }
        }.addOnFailureListener {
            showToast(context,"User does not exists")
        }

    }
}
