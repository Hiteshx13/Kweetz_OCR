package com.kweetz.utils

var RECEIPT="receipt"
var CLASS_ADDRESS="address"
var CLASS_ITEM="item"
var CLASS_TOTAL="total"
var CLASS_RECEIPT_NO="receipt_no"


fun isReceiptNumber(strSymbols:String):Boolean{
    val regPatternAddress3 = """([0-9]{3,15})""".toRegex(RegexOption.IGNORE_CASE)
    return regPatternAddress3.containsMatchIn(strSymbols)
}