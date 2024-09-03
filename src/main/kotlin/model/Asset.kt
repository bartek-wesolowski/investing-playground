package com.bartoszwesolowski.model

import nl.hiddewieringa.money.minus
import nl.hiddewieringa.money.plus
import nl.hiddewieringa.money.times
import org.javamoney.moneta.Money
import java.util.LinkedList
import javax.money.MonetaryAmount

data class Asset(
    val name: String,
    val transactions: LinkedList<Transaction>
) {
    fun currentValue(currentPrice: MonetaryAmount): MonetaryAmount = transactions
        .map { it.currentValue(currentPrice)}
        .reduce { sum, value -> sum + value }

    fun currentValueAfterTax(currentPrice: MonetaryAmount, tax: Double): MonetaryAmount = transactions
        .map { it.currentValueAfterTax(currentPrice, tax)}
        .reduce { sum, value -> sum + value }

    fun sellValue(value: MonetaryAmount, currentPrice: MonetaryAmount, tax: Double): MonetaryAmount {
        var remainingValue = value
        var taxValue: MonetaryAmount = Money.zero(value.currency)
        while (remainingValue.isPositive) {
            val transaction = transactions.first()
            val currentValue = transaction.currentValue(currentPrice)
            if (currentValue <= remainingValue) {
                transactions.removeFirst()
                val profit = currentValue - transaction.buyValue()
                remainingValue -= currentValue
                taxValue += profit * tax
            } else {
                taxValue += transaction.sellValue(remainingValue, currentPrice, tax)
                remainingValue = Money.zero(value.currency)
            }
        }
        return taxValue
    }

    fun sellAfterTaxValue(afterTaxValue: MonetaryAmount, currentPrice: MonetaryAmount, tax: Double): MonetaryAmount {
        var remainingValue = afterTaxValue
        var taxValue: MonetaryAmount = Money.zero(afterTaxValue.currency)
        while (remainingValue.isPositive) {
            val transaction = transactions.first()
            val currentValueAfterTax = transaction.currentValueAfterTax(currentPrice, tax)
            if (currentValueAfterTax <= remainingValue) {
                transactions.removeFirst()
                val profit = transaction.currentValue(currentPrice) - transaction.buyValue()
                val transactionTax = profit * tax
                remainingValue -= currentValueAfterTax
                taxValue += transactionTax
            } else {
                taxValue += transaction.sellValueAfterTax(remainingValue, currentPrice, tax)
                remainingValue = Money.zero(afterTaxValue.currency)
            }
        }
        return taxValue
    }
}
