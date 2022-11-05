package com.kweetz.utils

import java.util.*

class GenerateSymbols {


    /**
     * You can edit, run, and share this code.
     * play.kotlinlang.org
     */


    fun main() {

        val regAlphabets = Regex("^[a-zA-Z]*$")
        val regNumber = Regex("^[0-9]*$")
        val regAlphaNum = Regex("^[a-zA-Z0-9]*$")
        val arrayCurrency = arrayOf("$", "¥", "EUR", "GBP", "ZR", "SR")
        val arraySymbols = arrayOf("x", "kg", "g")
        val ignoreSymbols = arrayOf(".", ",")

        val str = arrayOf(" Appe 1gab $77.55 A","Appe  ds 1gab $7.555 A", "Appegg5k 5k 5 1gab $776.55 A" )
        val regCombo = Regex("^[$0-9.]*$")

        lateinit var sb: StringBuffer

        str.forEach { it ->
            val strResult = StringBuilder()

            val arrayString = it.trim().split(" ")
            arrayString.forEach { word ->

                val space = " "
                strResult.append(space.toString())


                if (arrayCurrency.contains(word.toString())) {
                    strResult.append("CURRENCY")
                } else if (word.toString().matches(regAlphabets) && !arraySymbols.contains(
                        word.toString().lowercase()
                    )
                ) {
                    strResult.append("STR")
                } else if (word.toString().matches(regNumber)) {
                    strResult.append("NUM")
                } else if (word.toString().matches(regAlphaNum) && !arraySymbols.contains(
                        word.toString().lowercase()
                    )
                ) {
                    strResult.append("ALPHANUM")
                } else if (word.toString().matches(regCombo)) {
                    var tempPettern =StringBuffer()
                    var current:String=""
                    word.toString().forEach { chr ->
                        if (arrayCurrency.contains(chr.toString() )&& !current.equals("CURR")) {
                            tempPettern.append("CURR")
                            current="CURR"
                        } else if (chr.toString().matches(regAlphabets)&&current!="CHR") {
                            tempPettern.append("CHR")
                            current="CHR"
                        } else if (chr.toString().matches(regNumber)&&current!="NUM") {
                            tempPettern.append("NUM")
                            current="NUM"
                        }else if(chr.toString().equals(".")&&current!="SFS"){
                            tempPettern.append("SFS")
                            current="SFS"
                        }else if(chr.toString().equals(",")&&current!="SC"){
                            tempPettern.append("SC")
                            current="SC"
                        }

                    }
                    strResult.append(tempPettern.toString())
                }

                else {
                    sb = StringBuffer()
                    word.toString().forEach { chr ->


                        if (chr.toString().matches(regAlphabets) && arraySymbols.contains(
                                chr.toString().lowercase()
                            ) == false
                        ) {
                            strResult.append("CHR")

                        } else if (chr.toString().matches(regNumber)) {
                            strResult.append("NUM")
                        } else {


                            when (chr.toString()) {
                                "$" -> {
                                    strResult.append("CURRENCY")
                                }
                                ":" -> {
                                    strResult.append("SCO")
                                }

                                "." -> {
                                    strResult.append("SFS")
                                }
                                "," -> {
                                    strResult.append("SC")
                                }

                                "(" -> {
                                    strResult.append("SPS")
                                }
                                ")" -> {
                                    strResult.append("SPE")
                                }
                                "-" -> {
                                    strResult.append("SH")
                                }
                                "%" -> {
                                    strResult.append("QSP")
                                }
                                "/" -> {
                                    strResult.append("QSS")
                                }
                                "kg" -> {
                                    strResult.append("QSKG")
                                }


                                "pc" -> {
                                    strResult.append("QSPC")
                                }
                                "X" -> {
                                    strResult.append("QSMX")
                                }
                                "*" -> {
                                    strResult.append("QSMS")
                                }

                            }
                        }
                    }
                }
            }
            println(strResult)
        }

    }

}