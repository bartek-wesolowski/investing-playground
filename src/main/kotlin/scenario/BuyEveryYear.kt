package com.bartoszwesolowski.scenario

import com.bartoszwesolowski.model.PriceProvider
import com.bartoszwesolowski.strategy.BaseInvestmentStrategy
import javax.money.MonetaryAmount

class BuyEveryYear(
    private val value: MonetaryAmount,
    private val years: Int,
    private val priceProvider: PriceProvider,
    private val tax: Double,
) : PartialInvestmentScenario {
    private var startYear: Int = 1

    override fun setStartYear(year: Int) {
        startYear = year
    }

    override fun transact(year: Int, strategy: BaseInvestmentStrategy): InvestmentState {
        check(year - startYear < years) { "Scenario is finished" }
        val currentPrice = priceProvider.getPriceInYear(year)
        strategy.buy(currentPrice, value)
        return strategy.currentState(currentPrice, tax)
    }

    override fun isFinished(year: Int, strategy: BaseInvestmentStrategy): Boolean {
        return year - startYear >= years
    }
}