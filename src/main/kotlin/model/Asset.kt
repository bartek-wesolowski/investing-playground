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

    fun sell(value: MonetaryAmount, currentPrice: MonetaryAmount, tax: Double): SellResult {
        var remainingValue = value
        var result = SellResult.zero(value.currency)
        while (remainingValue.isPositive) {
            val transaction = transactions.first()
            val currentValue = transaction.currentValue(currentPrice)
            if (currentValue <= remainingValue) {
                transactions.removeFirst()
                val profit = currentValue - transaction.buyValue()
                remainingValue -= currentValue
                val taxValue = profit * tax
                result = SellResult(
                    value = result.value + currentValue,
                    valueAfterTax = result.valueAfterTax + currentValue - taxValue,
                    taxValue = result.taxValue + taxValue
                )
            } else {
                val transactionResult = transaction.sell(remainingValue, currentPrice, tax)
                result += transactionResult
                remainingValue = Money.zero(value.currency)
            }
        }
        return result
    }

    fun sellAfterTax(afterTaxValue: MonetaryAmount, currentPrice: MonetaryAmount, tax: Double): SellResult {
        var remainingValue = afterTaxValue
        var result = SellResult.zero(afterTaxValue.currency)
        while (remainingValue.isPositive) {
            val transaction = transactions.first()
            val currentValueAfterTax = transaction.currentValueAfterTax(currentPrice, tax)
            if (currentValueAfterTax <= remainingValue) {
                transactions.removeFirst()
                val profit = transaction.currentValue(currentPrice) - transaction.buyValue()
                val transactionTax = profit * tax
                remainingValue -= currentValueAfterTax
                result = SellResult(
                    value = result.value + currentValueAfterTax + transactionTax,
                    valueAfterTax = result.valueAfterTax + currentValueAfterTax,
                    taxValue = result.taxValue + transactionTax
                )
            } else {
                val transactionResult = transaction.sellAfterTax(remainingValue, currentPrice, tax)
                result += transactionResult
                remainingValue = Money.zero(afterTaxValue.currency)
            }
        }
        return result
    }
}
