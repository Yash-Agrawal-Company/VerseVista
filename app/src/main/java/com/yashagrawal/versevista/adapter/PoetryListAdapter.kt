package com.yashagrawal.versevista.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yashagrawal.versevista.R
import com.yashagrawal.versevista.models.PoetryModel

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
        holder.userName.text = currentPoetry.userName
        holder.poetry.text = currentPoetry.poetry
        holder.date.text = currentPoetry.date
    }
    class PoetryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val userName : TextView = itemView.findViewById(R.id.userName)
        val poetry : TextView = itemView.findViewById(R.id.poetry)
        val date : TextView = itemView.findViewById(R.id.dateOfPublished)
    }
}