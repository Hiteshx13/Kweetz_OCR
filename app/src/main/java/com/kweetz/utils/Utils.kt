package com.kweetz.utils

import android.content.Context
import android.content.Intent
import android.graphics.*
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
    return str != null && str.matches(Regex("[a-zA-Z0-9 \\n]"))
}

fun isContainNumerical(str: String?): Boolean {
    return str != null && str.matches(Regex("[0-9 \\n]"))
}

fun isDateTimePattern(str: String): Boolean {
    /**yyyy-mm-dd hh:mm:ss*/
    val pattern1 = "(?:(.*[0-9]{4}+)-) ?(?:([0-9]{2}+)-) ?(?:([0-9]{2}+)) ?(?:([0-9]{2}+):)?(?:([0-9]{2}+):) ?(?:([0-9]{2}.*+))"

    /**yyyy.mm.dd hh:mm:ss*/
    val pattern2 = "(?:(.*[0-9]{4}+)\\.) ?(?:([0-9]{2}+)\\.) ?(?:([0-9]{2}+)) ?(?:([0-9]{2}+):)?(?:([0-9]{2}+):) ?(?:([0-9]{2}.*+))"

    /**dd-mm-yyyy hh:mm:ss*/
    val pattern3 = "(?:(.*[0-9]{2}+)-) ?(?:([0-9]{2}+)-) ?(?:([0-9]{4}+)) ?(?:([0-9]{2}+):)?(?:([0-9]{2}+):) ?(?:([0-9]{2}.*+))"

    /**dd.mm.yyyy hh:mm:ss*/
    val pattern4 = "(?:(.*[0-9]{2}+)\\.) ?(?:([0-9]{2}+)\\.) ?(?:([0-9]{4}+)) ?(?:([0-9]{2}+):)?(?:([0-9]{2}+):) ?(?:([0-9]{2}.*+))"


    return str.matches(Regex(pattern1))
            || str.matches(Regex(pattern2))
            || str.matches(Regex(pattern3))
            || str.matches(Regex(pattern4))
}

fun isDatePattern(str: String): Boolean {

    /**yyyy-mm-dd*/
    val pattern1 = "(?:(.*[0-9]{4}+)-) ?(?:([0-9]{2}+)-) ?(?:([0-9]{2}.*+))"

    /**yyyy.mm.dd*/
    val pattern2 = "(?:(.*[0-9]{4}+)\\.) ?(?:([0-9]{2}+)\\.) ?(?:([0-9]{2}.*+))"

    /**dd-mm-yyyy*/
    val pattern3 = "(?:(.*[0-9]{2}+)-) ?(?:([0-9]{2}+)-) ?(?:([0-9]{4}.*+))"

    /**dd.mm.yyyy*/
    val pattern4 = "(?:(.*[0-9]{2}+)\\.) ?(?:([0-9]{2}+)\\.) ?(?:([0-9]{4}.*+))"

    return str.matches(Regex(pattern1))
            || str.matches(Regex(pattern2))
            || str.matches(Regex(pattern3))
            || str.matches(Regex(pattern4))
}

fun isTimePattern(str: String): Boolean {
    /**hh:mm:ss*/
    val pattern1 = ".*(?:([0-9]{2}))(?:[:])(?:([0-9]{2}))(?:[:])(?:([0-9]{2})).*"

    /**hh:mm*/
    val pattern2 = ".*(?:([0-9]{2}))(?:[:])(?:([0-9]{2})).*"


    return str.matches(Regex(pattern1))
            || str.matches(Regex(pattern2))
}

fun gerReceiptIssuer(str: String): String {
    var isIssuer = false
    var issuer = ""
    var array = arrayOf("PVN LV", "PVN maksātāja kods", "FVN maksātāja kods", "PUN maksātāja kods", "PUN LV", "PUN LU")
    array.forEach {

        if (str.toLowerCase().contains(it.toLowerCase())) {
            isIssuer = true
            issuer = str.replace(it, "")
        } else if (str.contains("FVN") || str.contains("PVN")) {
            array = arrayOf("kods", "kous")
            array.forEach {
                if (str.contains(it.toLowerCase())) {
                    issuer = str.substring(str.lastIndexOf(it), str.length).replace(it, "")

                }
            }
        }
    }

    return issuer
}

fun getReceiptNumber(str: String): String {

    var arry = arrayOf("dok, nr", "dolk. nr", "dolk, nr", "dok. nr", "dok. #", "ceks nr", "ceks", "ceks#", "ceka nr", "dokuments:", "kvits nr", "kvits nr.", "kvits")

    var text = ""
    arry.forEach {
        if (str.toLowerCase().contains(it.toLowerCase())) {
            text = it

/*var trimmed = strLower.replace(it.toLowerCase().toString(), "").trim()
            if (isAlphaNumerical(trimmed)) {
                if (isContainNumerical(trimmed)) {
                    receiptNo = trimmed
                    if (trimmed.contains("\n")) {
                        newStr = trimmed.substring(0, str.indexOf("\n"))
                        receiptNo = newStr
                    }
                }
            }*/
        }
    }
    return text
}

fun isReceiptTotal(str: String): Boolean {
    var arry = arrayOf("samaksai eur", "sanaksai eur", "sarnaksai eur", "sarnäsai eur", "sarnäsaik eur", "kopa apmaksai", "samaksa eur", "kopeja summa apmaksai", "kopsumma eur", "kopa", "kopa summa", "kopa eur", "kopă eur", "kopå eur")
    var isTotal = false
    if (arry.contains(str.toLowerCase())) {
        isTotal = true
    }
    return isTotal
}

fun isReceiptDate(strDate: String): Boolean {
    var isDate = false
    var dateFormat1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    var dateFormat2 = SimpleDateFormat("yyyy-MM-dd")
    var dateFormat3 = SimpleDateFormat("DD.MM.yyyy")
    var dateFormat4 = SimpleDateFormat("DD.MM.yyyy HH:mm")

    if (isDate(strDate, dateFormat1)) {
        isDate = true
    } else if (isDate(strDate, dateFormat2)) {
        isDate = true
    } else if (isDate(strDate, dateFormat3)) {
        isDate = true
    } else if (isDate(strDate, dateFormat4)) {
        isDate = true
    }

    return isDate
}


fun isDate(date: String, dateFormat: SimpleDateFormat): Boolean {
    var isDate = false
    try {
        var date = dateFormat.parse(date)
        isDate = true
    } catch (e: Exception) {
        isDate = false
        Log.d("ParseException", "" + e.message)
    }
    return isDate
}

fun isTime(time: String, dateFormat: SimpleDateFormat): Boolean {
    var isTime = false
    try {
        var time = dateFormat.parse(time)
        isTime = true
    } catch (e: Exception) {
        isTime = false
        Log.d("ParseException", "" + e.message)
    }
    return isTime
}

fun convevrtToGrayscale(bmpOriginal: Bitmap): Bitmap {
    var height: Int = bmpOriginal.height
    var width: Int = bmpOriginal.width
    val bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val c = Canvas(bmpGrayscale)
    val paint = Paint()
    val cm = ColorMatrix()
    cm.setSaturation(0f)
    val f = ColorMatrixColorFilter(cm)
    paint.colorFilter = f
    c.drawBitmap(bmpOriginal, 0f, 0f, paint)
    return bmpGrayscale
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