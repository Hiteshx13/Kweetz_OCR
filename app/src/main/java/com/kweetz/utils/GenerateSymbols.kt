package com.kweetz.utils
import java.util.*


fun getSymbolicString(textLine: String): String {

    val regAlphabets = Regex("^[a-zA-Z]*$")
    val regNumber = Regex("^[0-9]*$")
    val regAlphaNum = Regex("^(?=.*[A-Za-z])(?=.*[0-9])[a-zA-Z0-9]+$")
    val arrayCurrency = arrayOf("$", "¥", "EUR", "GBP", "ZR", "SR")
    val arrayTotal = arrayOf("total", "samaksai eur")
    val arraySymbols = arrayOf("x", "kg", "g")
    val regCombo = Regex("^[$0-9.]*$")

    val sbResults = StringBuilder()
    val arrayWords = textLine.trim().split(" ")
    arrayWords.forEach { word ->


        if (arrayTotal.contains(word.lowercase(Locale.getDefault()))) {
            sbResults.append("TOTAL")
        } else if (arrayCurrency.contains(word)) {
            sbResults.append("CURRENCY")
        } else if (word.matches(regAlphabets) && !arraySymbols.contains(word.lowercase()) && !word.contains(
                "total",
                true
            )
        ) {
            sbResults.append("STR")
        } else if (word.matches(regNumber)) {
            sbResults.append("NUM")
        } else if (word.matches(regAlphaNum) && !arraySymbols.contains(
                word.lowercase()
            )
        ) {
            sbResults.append("ALPHANUM")
        } else if (word.matches(regCombo)) {
            val tempPettern = StringBuffer()
            var current = ""
            word.forEach { chr ->


                if (arrayCurrency.contains(chr.toString()) && current != "CURRENCY") {
                    tempPettern.append("CURRENCY")
                    current = "CURRENCY"
                } else if (chr.toString().matches(regAlphabets) && current != "CHR") {
                    tempPettern.append("CHR")
                    current = "CHR"
                } else if (chr.toString().matches(regNumber) && current != "NUM") {
                    tempPettern.append("NUM")
                    current = "NUM"
                } else if (chr.toString().equals(".") && current != "SFS") {
                    tempPettern.append("SFS")
                    current = "SFS"
                } else if (chr.toString().equals(",") && current != "SC") {
                    tempPettern.append("SC")
                    current = "SC"
                }

            }
            sbResults.append(tempPettern.toString())
        } else if (word.contains("total")) {
            sbResults.append("TOTAL")
        } else {
            word.forEach { chr ->

                if (chr.toString().matches(regAlphabets) && !arraySymbols.contains(
                        chr.toString().lowercase()
                    )
                ) {
                    sbResults.append("CHR")

                } else if (chr.toString().matches(regNumber)) {
                    sbResults.append("NUM")
                } else {


                    when (chr.toString()) {
                        "$" -> {
                            sbResults.append("CURRENCY")
                        }
                        ":" -> {
                            sbResults.append("SCO")
                        }

                        "." -> {
                            sbResults.append("SFS")
                        }
                        "," -> {
                            sbResults.append("SC")
                        }

                        "(" -> {
                            sbResults.append("SPS")
                        }
                        ")" -> {
                            sbResults.append("SPE")
                        }
                        "-" -> {
                            sbResults.append("SH")
                        }
                        "%" -> {
                            sbResults.append("QSP")
                        }
                        "/" -> {
                            sbResults.append("QSS")
                        }
                        "kg" -> {
                            sbResults.append("QSKG")
                        }

                        "pc" -> {
                            sbResults.append("QSPC")
                        }
                        "X" -> {
                            sbResults.append("QSMX")
                        }
                        "*" -> {
                            sbResults.append("QSMS")
                        }

                    }
                }
            }
        }

        sbResults.append(" ")

    }
    println(sbResults)
    return sbResults.toString()
}

