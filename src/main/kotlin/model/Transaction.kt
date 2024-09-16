package com.bartoszwesolowski.model

import nl.hiddewieringa.money.div
import nl.hiddewieringa.money.minus
import nl.hiddewieringa.money.plus
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

    fun sell(value: MonetaryAmount, currentPrice: MonetaryAmount, tax: Double): SellResult {
        val unitsSold = (value / currentPrice.number).number.toDouble()
        val profit = value - unitsSold * buyPrice
        units -= unitsSold
        val taxValue = profit * tax
        return SellResult(
            value = value,
            valueAfterTax = value - taxValue,
            taxValue = taxValue
        )
    }

    fun sellAfterTax(valueAfterTax: MonetaryAmount, currentPrice: MonetaryAmount, tax: Double): SellResult {
        val value = valueAfterTax / (1 - tax * (1 - (buyPrice / currentPrice.number).number.toDouble()))
        val unitsSold = (value / currentPrice.number).number.toDouble()
        val profit = value - unitsSold * buyPrice
        val taxValue = profit * tax
        units -= (value / currentPrice.number).number.toDouble()
        return SellResult(
            value = valueAfterTax + taxValue,
            valueAfterTax = valueAfterTax,
            taxValue = taxValue,
        )
    }
}
