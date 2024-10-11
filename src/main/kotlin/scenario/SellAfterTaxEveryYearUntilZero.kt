package com.bartoszwesolowski.scenario

import com.bartoszwesolowski.min
import com.bartoszwesolowski.model.PriceProvider
import com.bartoszwesolowski.strategy.BaseInvestmentStrategy
import javax.money.MonetaryAmount

class SellAfterTaxEveryYearUntilZero(
    private val valueAfterTax: MonetaryAmount,
    private val priceProvider: PriceProvider,
    private val tax: Double,
): PartialInvestmentScenario {
    override fun setStartYear(year: Int) = Unit

    override fun transact(year: Int, strategy: BaseInvestmentStrategy): InvestmentState {
        val currentPrice = priceProvider.getPriceInYear(year)
        check(strategy.currentValue(currentPrice).isPositive) { "No value to sell" }
        val currentValueAfterTax = strategy.currentValueAfterTax(currentPrice, tax)
        val valueToSell = min(currentValueAfterTax, valueAfterTax)
        strategy.sellAfterTax(currentPrice, valueToSell, tax)
        return strategy.currentState(currentPrice, tax)    }

    override fun isFinished(year: Int, strategy: BaseInvestmentStrategy): Boolean {
        val currentPrice = priceProvider.getPriceInYear(year)
        return strategy.currentValue(currentPrice).isZero
    }
}