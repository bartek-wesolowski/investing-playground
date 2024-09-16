package com.bartoszwesolowski.scenario

import com.bartoszwesolowski.model.SellResult
import com.bartoszwesolowski.model.YearlyPriceProvider
import com.bartoszwesolowski.strategy.BaseInvestmentStrategy
import nl.hiddewieringa.money.times
import org.javamoney.moneta.Money
import javax.money.Monetary
import javax.money.MonetaryAmount

class CustomInvestmentScenario(
    private val tax: Double,
    private val yearlyInvestment: MonetaryAmount,
    private val investmentYears: Int,
    private val yearlyWithdrawal: MonetaryAmount,
    private val withdrawAllYear: Int,
    private val yearlyPriceProvider: YearlyPriceProvider,
    private val verbose: Boolean,
) : InvestmentScenario {
    override fun simulate(strategy: BaseInvestmentStrategy): List<InvestmentState> {
        val result = mutableListOf(InvestmentState.zero(yearlyInvestment.currency))
        var year = 1
        val priceProvider: (assetName: String) -> MonetaryAmount = { assetName ->
            yearlyPriceProvider.getPriceInYear(year, assetName)
        }
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
        val rounding = Monetary.getRounding(yearlyInvestment.currency)
        while (year < withdrawAllYear && strategy.currentValueAfterTax(priceProvider, tax).isPositive) {
            if (verbose) println("Year $year")
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
                taxValue = sellResult.taxValue.with(rounding)
            )
            year++
        }
        if (verbose) println("Year $year")
        result += InvestmentState(
            investedValue = yearlyInvestment * investmentYears,
            value = strategy.currentValue(priceProvider),
            valueAfterTax = strategy.currentValueAfterTax(priceProvider, tax),
            withdrawnValue = sellResult.value.with(rounding),
            withdrawnValueAfterTax = sellResult.valueAfterTax.with(rounding),
            taxValue = sellResult.taxValue.with(rounding)
        )
        return result
    }
}