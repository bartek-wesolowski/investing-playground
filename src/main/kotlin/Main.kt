package com.bartoszwesolowski

import com.bartoszwesolowski.model.SP500YearlyPriceProvider
import com.bartoszwesolowski.scenario.AfterTaxWithdrawalInvestmentScenario
import com.bartoszwesolowski.strategy.BucketInvestmentStrategy
import com.bartoszwesolowski.strategy.LifoInvestmentStrategy
import com.bartoszwesolowski.strategy.SimpleInvestmentStrategy

fun main() {
    val verbose = false
    val scenario = AfterTaxWithdrawalInvestmentScenario(
        tax = 0.19,
        yearlyInvestment = 100_000.usd,
        investmentYears = 14,
        yearlyWithdrawalAfterTax = 180_000.usd,
        maxWithdrawalYears = 20,
        yearlyPriceProvider = SP500YearlyPriceProvider(
            initialPrice = 100.usd
        ),
        verbose = verbose,
    )
    println(scenario.simulate(SimpleInvestmentStrategy(verbose = verbose)).last())
    println(scenario.simulate(LifoInvestmentStrategy(verbose = verbose)).last())
    println(scenario.simulate(BucketInvestmentStrategy(verbose = verbose, bucket = 500_000.usd)).last())
}

