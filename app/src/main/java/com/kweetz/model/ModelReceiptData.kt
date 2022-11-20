package com.kweetz.model

import com.kweetz.utils.*

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
    data class Percentage(val percentage:Int,val tag:String,val tagCorrected:String)
    fun getHigherPercentage():Percentage{
        if(percentageAddress?:0>percentageTotal?:0 &&
            percentageAddress?:0>percentageItem?:0 &&
            percentageAddress?:0>percentageReceiptNumber?:0){
            return Percentage(percentageAddress?:0,CLASS_ADDRESS,CLASS_ADDRESS)
        }else  if(percentageTotal?:0>percentageAddress?:0 &&
            percentageTotal?:0>percentageItem?:0 &&
            percentageTotal?:0>percentageReceiptNumber?:0){
            return Percentage(percentageTotal?:0, CLASS_TOTAL,CLASS_TOTAL)
        }else  if(percentageItem?:0>percentageAddress?:0 &&
            percentageItem?:0>percentageTotal?:0 &&
            percentageItem?:0>percentageReceiptNumber?:0){
            return Percentage(percentageItem?:0, CLASS_ITEM,CLASS_ITEM)
        }else  if(percentageReceiptNumber?:0>percentageAddress?:0 &&
            percentageReceiptNumber?:0>percentageTotal?:0 &&
            percentageReceiptNumber?:0>percentageItem?:0){
            return Percentage(percentageReceiptNumber?:0, CLASS_RECEIPT_NO,CLASS_RECEIPT_NO)
        }
        else if(percentageAddress==percentageTotal && percentageTotal>0){
            if(symbols.contains(CLASS_TOTAL,true) ){
                return Percentage(percentageTotal,"$CLASS_ADDRESS / $CLASS_TOTAL"  ,CLASS_TOTAL )
            }else{
                return Percentage(percentageAddress,"$CLASS_ADDRESS / $CLASS_TOTAL" ,CLASS_ADDRESS )
            }

        }else if(percentageAddress==percentageItem && percentageItem>0){

            if(symbols.contains("CURRENCY",true)

            ){
                return Percentage(percentageItem,"$CLASS_ADDRESS / $CLASS_ITEM ",CLASS_ITEM )
            }else{
                return Percentage(percentageItem,"$CLASS_ADDRESS / $CLASS_ITEM ",CLASS_ADDRESS )
            }
        }else if(percentageAddress==percentageReceiptNumber && percentageReceiptNumber>0){

            if(symbols.contains("SCO",true)
            ){
                return Percentage(percentageReceiptNumber,"$CLASS_ADDRESS / $CLASS_RECEIPT_NO" , CLASS_RECEIPT_NO)
            }else{
                return Percentage(percentageAddress,"$CLASS_ADDRESS / $CLASS_RECEIPT_NO ", CLASS_ADDRESS)

            }
        }

        else if(percentageTotal==percentageReceiptNumber&& percentageReceiptNumber>0){
            if(symbols.contains(CLASS_TOTAL,true) ){
                return Percentage(percentageTotal,"$CLASS_RECEIPT_NO / $CLASS_TOTAL", CLASS_TOTAL )
            }else{
                return Percentage(percentageReceiptNumber,"$CLASS_RECEIPT_NO / $CLASS_TOTAL", CLASS_RECEIPT_NO )
            }

        } else if (percentageTotal == percentageItem&&percentageItem>0) {
            if(symbols.contains(CLASS_TOTAL,true) ){
                return Percentage(percentageTotal,"$CLASS_ITEM / $CLASS_TOTAL" ,CLASS_TOTAL )
            }else{
                return Percentage(percentageItem,"$CLASS_ITEM / $CLASS_TOTAL",CLASS_ITEM )
            }
        }else if (percentageItem ==percentageReceiptNumber&& percentageReceiptNumber>0) {
            if(symbols.contains("SCO",true) ){
                return Percentage(percentageReceiptNumber,"$CLASS_ITEM / $CLASS_RECEIPT_NO",  CLASS_RECEIPT_NO )
            }else{
                return Percentage(percentageReceiptNumber,"$CLASS_ITEM / $CLASS_RECEIPT_NO", CLASS_ITEM )
            }
        }else{
            return Percentage(0,"","")
        }
    }


}