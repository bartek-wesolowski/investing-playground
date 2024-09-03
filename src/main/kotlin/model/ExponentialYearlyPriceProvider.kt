package com.bartoszwesolowski.model

import nl.hiddewieringa.money.times
import javax.money.Monetary
import javax.money.MonetaryAmount
import kotlin.math.pow

class ExponentialYearlyPriceProvider(
    private val initialPrice: MonetaryAmount,
    private val growthRate: Double
) : YearlyPriceProvider {
    override fun getPriceInYear(year: Int, assetName: String): MonetaryAmount {
        return (initialPrice * (1 + growthRate).pow(year - 1)).with(Monetary.getRounding(initialPrice.currency))
    }
}