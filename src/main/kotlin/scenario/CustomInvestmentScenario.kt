package com.bartoszwesolowski.scenario

import com.bartoszwesolowski.model.YearlyPriceProvider
import com.bartoszwesolowski.strategy.BaseInvestmentStrategy
import nl.hiddewieringa.money.plus
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
    override fun simulate(strategy: BaseInvestmentStrategy): List<InvestmentScenarioResult> {
        val result = mutableListOf<InvestmentScenarioResult>()
        var year = 1
        val priceProvider: (assetName: String) -> MonetaryAmount = { assetName ->
            yearlyPriceProvider.getPriceInYear(year, assetName)
        }
        result += InvestmentScenarioResult(
            investmentValue = Money.zero(yearlyInvestment.currency),
            accountValue = strategy.currentValue(priceProvider),
            accountValueAfterTax = strategy.currentValueAfterTax(priceProvider, tax),
            withdrawnValueAfterTax = Money.zero(yearlyInvestment.currency),
            taxValue = Money.zero(yearlyInvestment.currency)
        )
        repeat(investmentYears) {
            if (verbose) println("Year $year")
            strategy.buy(priceProvider, yearlyInvestment)
            result += InvestmentScenarioResult(
                investmentValue = yearlyInvestment * year,
                accountValue = strategy.currentValue(priceProvider),
                accountValueAfterTax = strategy.currentValueAfterTax(priceProvider, tax),
                withdrawnValueAfterTax = Money.zero(yearlyInvestment.currency),
                taxValue = Money.zero(yearlyInvestment.currency)
            )
            year++
        }
        var withdrawnValue: MonetaryAmount = Money.zero(yearlyInvestment.currency)
        var taxValue: MonetaryAmount = Money.zero(yearlyInvestment.currency)
        val rounding = Monetary.getRounding(yearlyInvestment.currency)
        while (year < withdrawAllYear && strategy.currentValueAfterTax(priceProvider, tax).isPositive) {
            if (verbose) println("Year $year")
            val currentValue = strategy.currentValue(priceProvider)
            taxValue += if (yearlyWithdrawal <= currentValue) {
                withdrawnValue += yearlyWithdrawal
                strategy.sellValue(priceProvider, yearlyWithdrawal, tax)
            } else {
                withdrawnValue += currentValue
                strategy.sellValue(priceProvider, currentValue, tax)
            }
            result += InvestmentScenarioResult(
                investmentValue = yearlyInvestment * investmentYears,
                accountValue = strategy.currentValue(priceProvider),
                accountValueAfterTax = strategy.currentValueAfterTax(priceProvider, tax),
                withdrawnValueAfterTax = withdrawnValue.with(rounding),
                taxValue = taxValue.with(rounding)
            )
            year++
        }
        if (verbose) println("Year $year")
        val currentValue = strategy.currentValue(priceProvider)
        withdrawnValue += currentValue
        taxValue += strategy.sellValue(priceProvider, currentValue, tax)
        result += InvestmentScenarioResult(
            investmentValue = yearlyInvestment * investmentYears,
            accountValue = strategy.currentValue(priceProvider),
            accountValueAfterTax = strategy.currentValueAfterTax(priceProvider, tax),
            withdrawnValueAfterTax = withdrawnValue.with(rounding),
            taxValue = taxValue.with(rounding)
        )
        return result
    }
}