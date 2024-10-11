package com.bartoszwesolowski.scenario

import com.bartoszwesolowski.model.PriceProvider
import com.bartoszwesolowski.strategy.BaseInvestmentStrategy
import javax.money.MonetaryAmount

class SellAfterTaxEveryYear(
    private val valueAfterTax: MonetaryAmount,
    private val years: Int,
    private val priceProvider: PriceProvider,
    private val tax: Double,
): PartialInvestmentScenario {
    private var startYear: Int = 1

    override fun setStartYear(year: Int) {
        startYear = year
    }

    override fun transact(year: Int, strategy: BaseInvestmentStrategy): InvestmentState {
        val currentPrice = priceProvider.getPriceInYear(year)
        check(strategy.currentValue(currentPrice).isPositive) { "No value to sell" }
        val valueToSell = if (strategy.currentValueAfterTax(currentPrice, tax) >= valueAfterTax) {
            valueAfterTax
        } else {
            strategy.currentValue(currentPrice)
        }
        strategy.sellAfterTax(currentPrice, valueToSell, tax)
        return strategy.currentState(currentPrice, tax)    }

    override fun isFinished(year: Int, strategy: BaseInvestmentStrategy): Boolean {
        val currentPrice = priceProvider.getPriceInYear(year)
        return year - startYear >= years || strategy.currentValue(currentPrice).isZero
    }
}