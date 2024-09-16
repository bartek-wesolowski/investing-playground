package com.bartoszwesolowski.model

import nl.hiddewieringa.money.plus
import org.javamoney.moneta.Money
import javax.money.CurrencyUnit
import javax.money.MonetaryAmount

data class SellResult(
    val value: MonetaryAmount,
    val valueAfterTax: MonetaryAmount,
    val taxValue: MonetaryAmount,
) {
    operator fun plus(other: SellResult): SellResult = SellResult(
        value = value + other.value,
        valueAfterTax = valueAfterTax + other.valueAfterTax,
        taxValue = taxValue + other.taxValue
    )

    companion object {
        fun zero(currency: CurrencyUnit): SellResult = SellResult(
            value = Money.zero(currency),
            valueAfterTax = Money.zero(currency),
            taxValue = Money.zero(currency)
        )
    }
}
