package com.bartoszwesolowski.scenario

import com.bartoszwesolowski.model.YearlyPriceProvider
import com.bartoszwesolowski.model.at
import com.bartoszwesolowski.strategy.BaseInvestmentStrategy
import javax.money.MonetaryAmount

class SellAfterTaxEveryYearUntilZero(
    private val valueAfterTax: MonetaryAmount,
    private val yearlyPriceProvider: YearlyPriceProvider,
    private val tax: Double,
): PartialInvestmentScenario {
    override fun setStartYear(year: Int) = Unit

    override fun transact(year: Int, strategy: BaseInvestmentStrategy): InvestmentState {
        val priceProvider = yearlyPriceProvider.at(year)
        check(strategy.currentValue(priceProvider).isPositive) { "No value to sell" }
        val valueToSell = if (strategy.currentValueAfterTax(priceProvider, tax) >= valueAfterTax) {
            valueAfterTax
        } else {
            strategy.currentValue(priceProvider)
        }
        strategy.sellAfterTax(priceProvider, valueToSell, tax)
        return strategy.currentState(priceProvider, tax)    }

    override fun isFinished(year: Int, strategy: BaseInvestmentStrategy): Boolean {
        val priceProvider = yearlyPriceProvider.at(year)
        return strategy.currentValue(priceProvider).isZero
    }
}