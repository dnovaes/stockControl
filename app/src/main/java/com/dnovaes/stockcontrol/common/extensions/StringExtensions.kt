package com.dnovaes.stockcontrol.common.extensions

import com.dnovaes.stockcontrol.common.monitoring.log


fun String.formatCurrency(): String {
    val typedValue = this.swapCommaAndDot()
    return when {
        (typedValue.length <= 3) -> {
            if (typedValue.contains(".")) {
                typedValue.formatToDecimal(2).swapCommaAndDot()
            } else {
                val padded = typedValue.padStart(3, '0')
                StringBuilder(padded).insert(padded.length - 2, ',').toString()
            }
        }
        (typedValue.length == 4) -> {
            typedValue.swapCommaAndDot()
        }
        (typedValue.length > 7) -> {
            typedValue.substring(1, 8).swapCommaAndDot().reAddDecimalComma()
        }
        else -> {
            val numericValue = (typedValue.toFloat() * 10)
            numericValue.toString().formatToDecimal(2).swapCommaAndDot()
        }
    }
}

fun String.reAddDecimalComma(): String {
    val removedSymbols = this.filter { it.isDigit() }
    return StringBuilder(removedSymbols).insert(removedSymbols.length - 2, ',').toString()
}

fun String.removeLeadingZeroesAndSymbols(): String {
    val leadingZeroesRemoved = this.filter { it.isDigit() }
        .replaceFirst("^0+".toRegex(), "")
    return leadingZeroesRemoved.ifEmpty { "0" }
}

fun String.formatToDecimal(numDecimal: Int): String
    = String.format("%.${numDecimal}f", this.toFloat())

fun String.swapCommaAndDot(): String {
    return this.map {
        when (it) {
            ',' -> '.'
            '.' -> ','
            else -> it
        }
    }.joinToString("")
}
