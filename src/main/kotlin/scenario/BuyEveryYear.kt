package com.bartoszwesolowski.scenario

import com.bartoszwesolowski.model.YearlyPriceProvider
import com.bartoszwesolowski.model.at
import com.bartoszwesolowski.strategy.BaseInvestmentStrategy
import javax.money.MonetaryAmount

class BuyEveryYear(
    private val value: MonetaryAmount,
    private val years: Int,
    private val yearlyPriceProvider: YearlyPriceProvider,
    private val tax: Double,
) : PartialInvestmentScenario {
    private var startYear: Int = 1

    override fun setStartYear(year: Int) {
        startYear = year
    }

    override fun transact(year: Int, strategy: BaseInvestmentStrategy): InvestmentState {
        check(year - startYear < years) { "Scenario is finished" }
        val priceProvider = yearlyPriceProvider.at(year)
        strategy.buy(priceProvider, value)
        return strategy.currentState(priceProvider, tax)
    }

    override fun isFinished(year: Int, strategy: BaseInvestmentStrategy): Boolean {
        return year - startYear >= years
    }
}