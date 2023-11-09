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
import com.yashagrawal.versevista.adapter.PoetryListAdapter
import com.yashagrawal.versevista.models.PoetryModel


class MainActivity : AppCompatActivity() {
    private lateinit var profileIcon : ImageView
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var poetryRecyclerView : RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        initializing the objects
        init()
        profileIcon.scaleType = ImageView.ScaleType.FIT_XY
        var poetry = "\"In love's gentle embrace, hearts find their sweetest song,\n" +
                "A dance of souls, where two spirits truly belong.\n" +
                "Eternal bliss in every beat, love\\'s melody, forever strong.\""
        var p1 = PoetryModel("yash_agrawal",poetry,"4-11-2023")
        var myPoetries = arrayListOf<PoetryModel>(p1,p1,p1,p1)
        poetryRecyclerView.adapter = PoetryListAdapter(this,myPoetries)
        poetryRecyclerView.layoutManager = LinearLayoutManager(this)

        /// Now i am going to start designing the writing poetry activity
        floatingActionButton.setOnClickListener {
            val intent = Intent(this,WritingPoetryActivity::class.java)
            startActivity(intent)
        }
    }
    private fun init(){
        poetryRecyclerView = findViewById(R.id.poetryRecyclerView)
        floatingActionButton = findViewById(R.id.floatingActionButton)
        profileIcon = findViewById(R.id.profileIcon)
    }

    // Go to Profile Activity
    fun profilePage(view: View) {
        val intent = Intent(this,ProfileActivity::class.java)
        startActivity(intent)
    }
}