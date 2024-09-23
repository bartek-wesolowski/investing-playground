package com.bartoszwesolowski.scenario

import com.bartoszwesolowski.model.YearlyPriceProvider
import com.bartoszwesolowski.model.at
import com.bartoszwesolowski.strategy.BaseInvestmentStrategy
import javax.money.MonetaryAmount

class SellAfterTaxEveryYear(
    private val valueAfterTax: MonetaryAmount,
    private val years: Int,
    private val yearlyPriceProvider: YearlyPriceProvider,
    private val tax: Double,
): PartialInvestmentScenario {
    private var startYear: Int = 1

    override fun setStartYear(year: Int) {
        startYear = year
    }

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
        return year - startYear >= years || strategy.currentValue(priceProvider).isZero
    }
}