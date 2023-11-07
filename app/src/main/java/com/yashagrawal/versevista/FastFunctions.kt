package com.yashagrawal.versevista

import android.content.Context
import android.widget.Toast

fun showToast(context : Context,msg: String){
    Toast.makeText(context,msg,Toast.LENGTH_SHORT).show()
}