package com.bartoszwesolowski.scenario

import com.bartoszwesolowski.model.SellResult
import com.bartoszwesolowski.model.YearlyPriceProvider
import com.bartoszwesolowski.model.at
import com.bartoszwesolowski.strategy.BaseInvestmentStrategy
import nl.hiddewieringa.money.times
import org.javamoney.moneta.Money
import javax.money.Monetary
import javax.money.MonetaryAmount

class NominalValueWithdrawalInvestmentScenario(
    private val tax: Double,
    private val yearlyInvestment: MonetaryAmount,
    private val investmentYears: Int,
    private val yearlyWithdrawal: MonetaryAmount,
    private val yearlyPriceProvider: YearlyPriceProvider,
    private val verbose: Boolean,
): InvestmentScenario {
    override fun simulate(strategy: BaseInvestmentStrategy): List<InvestmentState> {
        val result = mutableListOf(InvestmentState.initial(yearlyInvestment.currency))
        var year = 1
        val priceProvider = yearlyPriceProvider.at(year)
        repeat(investmentYears) {
            if (verbose) println("Year $year")
            strategy.buy(priceProvider, yearlyInvestment)
            result += InvestmentState(
                investedValue = yearlyInvestment * year,
                value = strategy.currentValue(priceProvider),
                valueAfterTax = strategy.currentValueAfterTax(priceProvider, tax),
                withdrawnValue = Money.zero(yearlyInvestment.currency),
                withdrawnValueAfterTax = Money.zero(yearlyInvestment.currency),
                taxValue = Money.zero(yearlyInvestment.currency)
            )
            year++
        }
        var sellResult = SellResult.zero(yearlyInvestment.currency)
        var withdrawalYears = 0
        val rounding = Monetary.getRounding(yearlyInvestment.currency)
        while (strategy.currentValueAfterTax(priceProvider, tax).isPositive) {
            if (verbose) println("Year $year")
            withdrawalYears++
            val currentValue = strategy.currentValue(priceProvider)
            sellResult += if (yearlyWithdrawal <= currentValue) {
                strategy.sell(priceProvider, yearlyWithdrawal, tax)
            } else {
                strategy.sell(priceProvider, currentValue, tax)
            }
            result += InvestmentState(
                investedValue = yearlyInvestment * investmentYears,
                value = strategy.currentValue(priceProvider),
                valueAfterTax = strategy.currentValueAfterTax(priceProvider, tax),
                withdrawnValue = sellResult.value.with(rounding),
                withdrawnValueAfterTax = sellResult.valueAfterTax.with(rounding),
                taxValue = sellResult.valueAfterTax.with(rounding)
            )
            year++
        }
        return result
    }
}