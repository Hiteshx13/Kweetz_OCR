package com.kweetz.utils

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity

fun lounchActivity(context: Context,target: AppCompatActivity){
    var intent=Intent(context,target::class.java)
    context.startActivity(intent)
}