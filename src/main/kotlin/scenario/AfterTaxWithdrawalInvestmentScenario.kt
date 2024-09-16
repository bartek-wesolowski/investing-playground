package com.bartoszwesolowski.scenario

import com.bartoszwesolowski.model.SellResult
import com.bartoszwesolowski.model.YearlyPriceProvider
import com.bartoszwesolowski.strategy.BaseInvestmentStrategy
import nl.hiddewieringa.money.times
import org.javamoney.moneta.Money
import javax.money.Monetary
import javax.money.MonetaryAmount

class AfterTaxWithdrawalInvestmentScenario(
    private val tax: Double,
    private val yearlyInvestment: MonetaryAmount,
    private val investmentYears: Int,
    private val yearlyWithdrawalAfterTax: MonetaryAmount,
    private val maxWithdrawalYears: Int,
    private val yearlyPriceProvider: YearlyPriceProvider,
    private val verbose: Boolean,
): InvestmentScenario {
    override fun simulate(strategy: BaseInvestmentStrategy): List<InvestmentState> {
        val result = mutableListOf<InvestmentState>()
        var year = 1
        val priceProvider: (assetName: String) -> MonetaryAmount = {
                assetName -> yearlyPriceProvider.getPriceInYear(year, assetName)
        }
        result += InvestmentState(
            investedValue = Money.zero(yearlyInvestment.currency),
            value = strategy.currentValue(priceProvider),
            valueAfterTax = strategy.currentValueAfterTax(priceProvider, tax),
            withdrawnValue = Money.zero(yearlyInvestment.currency),
            withdrawnValueAfterTax = Money.zero(yearlyInvestment.currency),
            taxValue = Money.zero(yearlyInvestment.currency)
        )
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
        while (year < investmentYears + maxWithdrawalYears && strategy.currentValueAfterTax(priceProvider, tax).isPositive) {
            if (verbose) println("Year $year")
            withdrawalYears++
            val currentValueAfterTax = strategy.currentValueAfterTax(priceProvider, tax)
            sellResult += if (yearlyWithdrawalAfterTax <= currentValueAfterTax) {
                strategy.sellAfterTax(priceProvider, yearlyWithdrawalAfterTax, tax)
            } else {
                strategy.sellAfterTax(priceProvider, currentValueAfterTax, tax)
            }
            result += InvestmentState(
                investedValue = yearlyInvestment * investmentYears,
                value = strategy.currentValue(priceProvider),
                valueAfterTax = strategy.currentValueAfterTax(priceProvider, tax),
                withdrawnValue = sellResult.value,
                withdrawnValueAfterTax = sellResult.valueAfterTax.with(rounding),
                taxValue = sellResult.taxValue.with(rounding)
            )
            year++
        }
        return result
    }
}