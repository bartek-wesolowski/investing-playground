package com.bartoszwesolowski.scenario

import com.bartoszwesolowski.min
import com.bartoszwesolowski.model.YearlyPriceProvider
import com.bartoszwesolowski.model.at
import com.bartoszwesolowski.strategy.BaseInvestmentStrategy
import javax.money.MonetaryAmount

class SellEveryYearUntilZero(
    private val value: MonetaryAmount,
    private val yearlyPriceProvider: YearlyPriceProvider,
    private val tax: Double,
): PartialInvestmentScenario {
    override fun setStartYear(year: Int) = Unit

    override fun transact(year: Int, strategy: BaseInvestmentStrategy): InvestmentState {
        val priceProvider = yearlyPriceProvider.at(year)
        val currentValue = strategy.currentValue(priceProvider)
        check(currentValue.isPositive) { "No value to sell" }
        val valueToSell = min(currentValue, value)
        strategy.sell(priceProvider, valueToSell, tax)
        return strategy.currentState(priceProvider, tax)
    }

    override fun isFinished(year: Int, strategy: BaseInvestmentStrategy): Boolean {
        val priceProvider = yearlyPriceProvider.at(year)
        return strategy.currentValue(priceProvider).isZero
    }
}