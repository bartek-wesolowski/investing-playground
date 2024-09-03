package com.bartoszwesolowski.scenario

import com.bartoszwesolowski.model.YearlyPriceProvider
import com.bartoszwesolowski.strategy.BaseInvestmentStrategy
import nl.hiddewieringa.money.plus
import nl.hiddewieringa.money.times
import org.javamoney.moneta.Money
import javax.money.Monetary
import javax.money.MonetaryAmount

class AfterTaxWithdrawalInvestmentScenario(
    private val tax: Double,
    private val yearlyInvestment: MonetaryAmount,
    private val investmentYears: Int,
    private val yearlyWithdrawalAfterTax: MonetaryAmount,
    private val yearlyPriceProvider: YearlyPriceProvider,
    private val verbose: Boolean,
): InvestmentScenario {
    override fun simulate(strategy: BaseInvestmentStrategy): List<InvestmentScenarioResult> {
        val result = mutableListOf<InvestmentScenarioResult>()
        result += InvestmentScenarioResult(
            investmentValue = Money.zero(yearlyInvestment.currency),
            withdrawnValue = Money.zero(yearlyInvestment.currency),
            withdrawalYears = 0,
            taxValue = Money.zero(yearlyInvestment.currency)
        )
        var year = 1
        val priceProvider: (assetName: String) -> MonetaryAmount = {
            assetName -> yearlyPriceProvider.getPriceInYear(year, assetName)
        }
        repeat(investmentYears) {
            if (verbose) println("Year $year")
            strategy.buy(priceProvider, yearlyInvestment)
            year++
        }
        var withdrawnValue: MonetaryAmount = Money.zero(yearlyInvestment.currency)
        var taxValue: MonetaryAmount = Money.zero(yearlyInvestment.currency)
        var withdrawalYears = 0
        val rounding = Monetary.getRounding(yearlyInvestment.currency)
        while (strategy.currentValueAfterTax(priceProvider, tax).isPositive) {
            if (verbose) println("Year $year")
            withdrawalYears++
            val currentValueAfterTax = strategy.currentValueAfterTax(priceProvider, tax)
            taxValue += if (yearlyWithdrawalAfterTax <= currentValueAfterTax) {
                withdrawnValue += yearlyWithdrawalAfterTax
                strategy.sellAfterTaxValue(priceProvider, yearlyWithdrawalAfterTax, tax)
            } else {
                withdrawnValue += currentValueAfterTax
                strategy.sellAfterTaxValue(priceProvider, currentValueAfterTax, tax)
            }
            result += InvestmentScenarioResult(
                investmentValue = yearlyInvestment * investmentYears,
                withdrawnValue = withdrawnValue.with(rounding),
                withdrawalYears = withdrawalYears,
                taxValue = taxValue.with(rounding)
            )
            year++
        }
        return result
    }
}