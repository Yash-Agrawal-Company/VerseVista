package com.yashagrawal.versevista.adapter

import android.content.Context
import android.content.Intent
import android.provider.ContactsContract.Profile
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yashagrawal.versevista.MainActivity
import com.yashagrawal.versevista.ProfileActivity
import com.yashagrawal.versevista.R
import com.yashagrawal.versevista.models.PoetryModel
import com.yashagrawal.versevista.showToast

class PoetryListAdapter(var context : Context, var poetryList : ArrayList<PoetryModel>) : RecyclerView.Adapter<PoetryListAdapter.PoetryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoetryViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.poetry_list_item,parent,false)
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
            holder.userName.setOnClickListener {
                showProfile(currentPoetry.username.toString())
            }
    }
    class PoetryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val userName : TextView = itemView.findViewById(R.id.userName)
        val poetry : TextView = itemView.findViewById(R.id.poetry)
        val date : TextView = itemView.findViewById(R.id.dateOfPublished)

    }

    private fun showProfile(uName : String?){
         val db: FirebaseFirestore = Firebase.firestore
        var uid : String = ""
        db.collection("Users").get().addOnSuccessListener {documents->
            for (document in documents.documents){
                val userName = document.getString("Username")
                    if (userName!! == uName){
                        uid = document.id
                        val intent = Intent(context,ProfileActivity::class.java)
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