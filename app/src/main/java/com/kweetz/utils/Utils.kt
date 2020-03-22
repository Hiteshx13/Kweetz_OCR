package com.kweetz.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.text.format.DateFormat
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.kweetz.R
import java.io.File
import java.io.IOException
import java.lang.NumberFormatException
import java.util.*


fun lounchActivity(context: Context, target: AppCompatActivity) {
    var intent = Intent(context, target::class.java)
    context.startActivity(intent)
}

fun lounchActivity(context: Context, intent: Intent) {
    context.startActivity(intent)
}

fun showToast(context: Context, msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}

fun getCameraUri(context: Context): Uri {
    val dir: String = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            .toString() + "/" + context.getString(R.string.app_name) + "/"

    val newdir = File(dir)
    if (!newdir.exists()) {
        newdir.mkdirs()
    }

    val file = dir + DateFormat.format("IMG_dd_MM_yyyy_hh_mm_ss", Date()).toString() + ".jpg"
    val newfile = File(file)
    try {
        newfile.createNewFile()
    } catch (e: IOException) {
    }

    return FileProvider.getUriForFile(context, context.applicationContext.packageName.toString() + ".provider", newfile)


}

fun isStrNumber(str: String?): Boolean {

    var num: Number? = null
    var temp = str
    if (str?.contains(",") == true) {
        temp = str.replace(",", ".")
        try {
            num = temp.toFloat()
        } catch (e: NumberFormatException) {

        }
    } else {
        try {
            num = temp?.toFloat()
        } catch (e: NumberFormatException) {

        }

    }
    return num != null
}

fun isAlphaNumerical(str: String?): Boolean {
    return str!=null&& str.matches(Regex( ".*[a-zA-Z1-9\\n].*"))
}

fun strToNumber(str: String?): Number? {

    var num: Number? = null
    var temp = str
    if (str?.contains(",") == true) {
        temp = str.replace(",", ".")
        try {
            num = temp.toFloat()
        } catch (e: NumberFormatException) {

        }
    } else {
        try {
            num = temp?.toInt()
        } catch (e: NumberFormatException) {

        }

    }
    return num
}