package com.bartoszwesolowski

import com.bartoszwesolowski.model.ExponentialYearlyPriceProvider
import com.bartoszwesolowski.scenario.AfterTaxWithdrawalInvestmentScenario
import com.bartoszwesolowski.strategy.BucketInvestmentStrategy
import com.bartoszwesolowski.strategy.LifoInvestmentStrategy
import com.bartoszwesolowski.strategy.SimpleInvestmentStrategy
import org.javamoney.moneta.Money

fun main() {
    val verbose = false
    val scenario = AfterTaxWithdrawalInvestmentScenario(
        tax = 0.19,
        yearlyInvestment = 100_000.usd,
        investmentYears = 16,
        yearlyWithdrawalAfterTax = 180_000.usd,
        yearlyPriceProvider = ExponentialYearlyPriceProvider(
            initialPrice = 100.usd,
            growthRate = 0.05
        ),
        verbose = verbose,
    )
    println(scenario.simulate(SimpleInvestmentStrategy(verbose = verbose)))
    println(scenario.simulate(LifoInvestmentStrategy(verbose = verbose)))
    println(scenario.simulate(BucketInvestmentStrategy(verbose = verbose, bucket = Money.of(500_000, "USD"))))
}

