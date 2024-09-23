package com.bartoszwesolowski.scenario

import org.javamoney.moneta.Money
import javax.money.CurrencyUnit
import javax.money.MonetaryAmount

data class InvestmentState(
    val investedValue: MonetaryAmount,
    val value: MonetaryAmount,
    val valueAfterTax: MonetaryAmount,
    val withdrawnValue: MonetaryAmount,
    val withdrawnValueAfterTax: MonetaryAmount,
    val taxValue: MonetaryAmount,
) {
    companion object {
        fun initial(currencyUnit: CurrencyUnit) = InvestmentState(
            investedValue = Money.zero(currencyUnit),
            value = Money.zero(currencyUnit),
            valueAfterTax = Money.zero(currencyUnit),
            withdrawnValue = Money.zero(currencyUnit),
            withdrawnValueAfterTax = Money.zero(currencyUnit),
            taxValue = Money.zero(currencyUnit)
        )
    }
}