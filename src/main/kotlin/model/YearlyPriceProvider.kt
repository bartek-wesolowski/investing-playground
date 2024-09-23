package com.bartoszwesolowski.model

import javax.money.MonetaryAmount

fun interface YearlyPriceProvider {
    fun getPriceInYear(year: Int, assetName: String): MonetaryAmount
}

fun YearlyPriceProvider.at(year: Int) = PriceProvider { assetName -> getPriceInYear(year, assetName) }