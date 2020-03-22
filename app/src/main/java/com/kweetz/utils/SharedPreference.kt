package com.kweetz.utils

import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity

class SharedPreference {
    lateinit var pref: SharedPreferences
    fun SharedPreference(activity: AppCompatActivity) {
        pref = activity.getPreferences(Context.MODE_PRIVATE) ?: return
    }

    fun putString(key: String, value: String) {
        with(pref.edit()) {
            putString(key, value)
            commit()
        }
    }

    fun getValue(key: String): String {
        return pref?.getString(key, "") ?: ""
    }

}