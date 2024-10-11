package com.bartoszwesolowski.model

import nl.hiddewieringa.money.times
import javax.money.Monetary
import javax.money.MonetaryAmount
import kotlin.math.pow

class ExponentialPriceProvider(
    private val initialPrice: MonetaryAmount,
    private val growthRate: Double
) : PriceProvider {
    override fun getPriceInYear(year: Int): MonetaryAmount {
        return (initialPrice * (1 + growthRate).pow(year - 1)).with(Monetary.getRounding(initialPrice.currency))
    }
}