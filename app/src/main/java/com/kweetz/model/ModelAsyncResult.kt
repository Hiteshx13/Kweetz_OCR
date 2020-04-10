package com.kweetz.model

import android.graphics.Bitmap
import com.kweetz.database.model.Receipt

data class ModelAsyncResult (var bitmap: Bitmap?=null,var receipt: Receipt?=null,var listParent: HashMap<Int, ArrayList<ModelReceiptData>?>?=null){
}