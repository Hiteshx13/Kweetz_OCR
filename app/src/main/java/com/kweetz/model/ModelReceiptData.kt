package com.kweetz.model

data class ModelReceiptData(
    var percentageOfMatch: Int?=null,
    var left: Int?=null,
    var top: Int?=null,
    var right: Int?=null,
    var bottom: Int?=null,
    var text: String="",
    var symbols:String="",

    var percentageAddress: Int=0,
    var percentageTotal: Int=0,
    var percentageItem: Int=0,
    var percentageReceiptNumber: Int=0,
){
    data class Percentage(val percentage:Int,val tag:String)
    fun getHigherPercentage():Percentage{
        if(percentageAddress?:0>percentageTotal?:0 &&
            percentageAddress?:0>percentageItem?:0 &&
            percentageAddress?:0>percentageReceiptNumber?:0){
            return Percentage(percentageAddress?:0,"percentageAddress")
        }else  if(percentageTotal?:0>percentageAddress?:0 &&
            percentageTotal?:0>percentageItem?:0 &&
            percentageTotal?:0>percentageReceiptNumber?:0){
            return Percentage(percentageTotal?:0,"percentageTotal")
        }else  if(percentageItem?:0>percentageAddress?:0 &&
            percentageItem?:0>percentageTotal?:0 &&
            percentageItem?:0>percentageReceiptNumber?:0){
            return Percentage(percentageItem?:0,"percentageItem")
        }else  if(percentageReceiptNumber?:0>percentageAddress?:0 &&
            percentageReceiptNumber?:0>percentageTotal?:0 &&
            percentageReceiptNumber?:0>percentageItem?:0){
            return Percentage(percentageReceiptNumber?:0,"percentageReceiptNumber")
        }
        else if(percentageAddress==percentageTotal){
            return Percentage(percentageAddress,"percentageAddress / percentageTotal")
        }else if(percentageAddress==percentageItem){
            return Percentage(percentageAddress,"percentageAddress / percentageItem")
        }else if(percentageAddress==percentageReceiptNumber){
            return Percentage(percentageAddress,"percentageAddress / percentageReceiptNumber")
        }

        else if(percentageTotal==percentageReceiptNumber){
            return Percentage(percentageTotal,"percentageTotal / percentageReceiptNumber")
        } else if (percentageTotal == percentageItem) {
            return Percentage(percentageTotal, "percentageTotal / percentageItem")
        }else if (percentageItem ==percentageReceiptNumber ) {
            return Percentage(percentageItem, "percentageItem / percentageReceiptNumber")
        }else{
            return Percentage(0,"")
        }
    }
}