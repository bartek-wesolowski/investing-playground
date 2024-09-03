package com.bartoszwesolowski.strategy

import com.bartoszwesolowski.model.Account
import com.bartoszwesolowski.model.PriceProvider
import javax.money.MonetaryAmount

abstract class BaseInvestmentStrategy(verbose: Boolean) {
    protected val account = Account(verbose = verbose)

    fun currentValue(priceProvider: PriceProvider): MonetaryAmount =
        account.currentValue(priceProvider)

    fun currentValueAfterTax(priceProvider: PriceProvider, tax: Double): MonetaryAmount =
        account.currentValueAfterTax(priceProvider, tax)

    abstract fun buy(priceProvider: PriceProvider, value: MonetaryAmount)

    abstract fun sellValue(priceProvider: PriceProvider, value: MonetaryAmount, tax: Double): MonetaryAmount

    abstract fun sellAfterTaxValue(priceProvider: PriceProvider, afterTaxValue: MonetaryAmount, tax: Double): MonetaryAmount
}