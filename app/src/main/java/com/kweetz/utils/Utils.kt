package com.kweetz.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.text.format.DateFormat
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.kweetz.R
import java.io.File
import java.io.IOException
import java.lang.NumberFormatException
import java.text.SimpleDateFormat
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

fun isReceiptTotal(str: String): Boolean {
    var arry = arrayOf("Samaksai EUR", "Kopa apmaksai", "Samaksa EUR", "Kopeja summa apmaksai", "Kopsumma EUR", "KOPA", "KOPA SUMMA", "Kopa EUR")
    var isTotal = false
    arry.forEach {
        if (str.toLowerCase().contains(it.toString().toLowerCase())) {
            isTotal = true
        }
    }
    return isTotal
}
fun isReceiptDate(strDate: String): Boolean {
    var isDate = false
    var dateFormat1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    var dateFormat2 = SimpleDateFormat("yyyy-MM-dd")

    try {
        var date = dateFormat1.parse(strDate)
        isDate = true
    } catch (e: Exception) {
        Log.d("ParseException", "" + e.message)
    }

    try {
        var date = dateFormat2.parse(strDate)
        isDate = true
    } catch (e: Exception) {
        Log.d("ParseException", "" + e.message)
    }
    return isDate
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