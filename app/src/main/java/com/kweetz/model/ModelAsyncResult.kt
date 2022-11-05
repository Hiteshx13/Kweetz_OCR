package com.kweetz.model

import android.graphics.Bitmap
import com.kweetz.database.model.Receipt

data class ModelAsyncResult(
    var bitmap: Bitmap? = null,
    var receipt: Receipt? = null,
    var arrayLeft: HashMap<Int, ModelReceiptData>,
    var arrayRight: HashMap<Int, ModelReceiptData>
)