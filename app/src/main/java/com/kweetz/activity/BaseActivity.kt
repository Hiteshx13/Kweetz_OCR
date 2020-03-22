package com.kweetz.activity

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kweetz.R
import com.kweetz.database.AppDatabase
//aec8adf967e336214b6d795fca233f0b51a5af8d
abstract class BaseActivity:AppCompatActivity() {

    lateinit var roomDB: AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        roomDB = AppDatabase.getDatabase(applicationContext)
        Log.d("a","")
    }

}