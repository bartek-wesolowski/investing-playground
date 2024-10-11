package com.bartoszwesolowski.model

import javax.money.MonetaryAmount

fun interface YearlyPriceProvider {
    fun getPriceInYear(year: Int): MonetaryAmount
}

fun YearlyPriceProvider.at(year: Int) = PriceProvider { getPriceInYear(year) }