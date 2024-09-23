package com.bartoszwesolowski.scenario

import com.bartoszwesolowski.model.YearlyPriceProvider
import com.bartoszwesolowski.model.at
import com.bartoszwesolowski.strategy.BaseInvestmentStrategy

class SellAll(
    private val yearlyPriceProvider: YearlyPriceProvider,
    private val tax: Double,
): PartialInvestmentScenario {
    override fun setStartYear(year: Int) = Unit

    override fun transact(year: Int, strategy: BaseInvestmentStrategy): InvestmentState {
        val priceProvider = yearlyPriceProvider.at(year)
        check(strategy.currentValue(priceProvider).isPositive) { "No value to sell" }
        strategy.sell(priceProvider, strategy.currentValue(priceProvider), tax)
        return strategy.currentState(priceProvider, tax)
    }

    override fun isFinished(year: Int, strategy: BaseInvestmentStrategy): Boolean {
        val priceProvider = yearlyPriceProvider.at(year)
        return strategy.currentValue(priceProvider).isZero
    }
}