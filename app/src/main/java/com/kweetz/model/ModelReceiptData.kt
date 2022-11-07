package com.kweetz.model

data class ModelReceiptData(
    val left: Int?=null,
    val top: Int?=null,
    val right: Int?=null,
    val bottom: Int?=null,
    val text: String="",
    val symbols:String
)