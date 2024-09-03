package com.bartoszwesolowski.model

import javax.money.MonetaryAmount

fun interface YearlyPriceProvider {
    fun getPriceInYear(year: Int, assetName: String): MonetaryAmount
}