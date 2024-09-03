package com.bartoszwesolowski.model

import nl.hiddewieringa.money.div
import nl.hiddewieringa.money.minus
import nl.hiddewieringa.money.times
import javax.money.MonetaryAmount

data class Transaction(
    val buyPrice: MonetaryAmount,
    var units: Double
) {
    fun buyValue(): MonetaryAmount = buyPrice * units

    fun currentValue(currentPrice: MonetaryAmount): MonetaryAmount = currentPrice.multiply(units)

    fun currentValueAfterTax(currentPrice: MonetaryAmount, tax: Double): MonetaryAmount {
        val currentValue = currentValue(currentPrice)
        val profit = currentValue - buyValue()
        val taxValue = profit * tax
        return currentValue - taxValue
    }

    fun sellValue(value: MonetaryAmount, currentPrice: MonetaryAmount, tax: Double): MonetaryAmount {
        val unitsSold = (value / currentPrice.number).number.toDouble()
        val profit = value - unitsSold * buyPrice
        units -= unitsSold
        return profit * tax
    }

    fun sellValueAfterTax(afterTaxValue: MonetaryAmount, currentPrice: MonetaryAmount, tax: Double): MonetaryAmount {
        val value = afterTaxValue / (1 - tax * (1 - (buyPrice / currentPrice.number).number.toDouble()))
        val unitsSold = (value / currentPrice.number).number.toDouble()
        val profit = value - unitsSold * buyPrice
        val taxValue = profit * tax
        units -= (value / currentPrice.number).number.toDouble()
        return taxValue
    }
}
