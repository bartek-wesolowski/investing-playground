package com.bartoszwesolowski.strategy

import com.bartoszwesolowski.model.Account
import com.bartoszwesolowski.model.SellResult
import com.bartoszwesolowski.scenario.InvestmentState
import javax.money.MonetaryAmount

abstract class BaseInvestmentStrategy(verbose: Boolean) {
    protected val account = Account(verbose = verbose)

    fun currentValue(currentPrice: MonetaryAmount): MonetaryAmount =
        account.currentValue(currentPrice)

    fun currentValueAfterTax(
        currentPrice: MonetaryAmount,
        tax: Double
    ): MonetaryAmount = account.currentValueAfterTax(currentPrice, tax)

    fun currentState(
        currentPrice: MonetaryAmount,
        tax: Double
    ): InvestmentState = account.currentState(currentPrice, tax)

    abstract fun buy(
        currentPrice: MonetaryAmount,
        value: MonetaryAmount
    )

    abstract fun sell(
        currentPrice: MonetaryAmount,
        value: MonetaryAmount,
        tax: Double
    ): SellResult

    abstract fun sellAfterTax(
        currentPrice: MonetaryAmount,
        afterTaxValue: MonetaryAmount,
        tax: Double
    ): SellResult
}