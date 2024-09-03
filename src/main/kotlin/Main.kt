package com.bartoszwesolowski

import com.bartoszwesolowski.scenario.AfterTaxWithdrawalInvestmentScenario
import com.bartoszwesolowski.strategy.BucketInvestmentStrategy
import com.bartoszwesolowski.strategy.LifoInvestmentStrategy
import com.bartoszwesolowski.strategy.SimpleInvestmentStrategy
import org.javamoney.moneta.Money
import javax.money.Monetary
import kotlin.math.pow

fun main() {
    val verbose = false
    val currency = Monetary.getCurrency("USD")
    val scenario = AfterTaxWithdrawalInvestmentScenario(
        tax = 0.19,
        yearlyInvestment = 100_000.usd,
        investmentYears = 16,
        yearlyWithdrawalAfterTax = 180_000.usd,
        yearlyPriceProvider = { year, _ -> (100 * 1.05.pow(year - 1)).usd.with(Monetary.getRounding(currency)) },
        verbose = verbose,
    )
    println(scenario.simulate(SimpleInvestmentStrategy(verbose = verbose)))
    println(scenario.simulate(LifoInvestmentStrategy(verbose = verbose)))
    println(scenario.simulate(BucketInvestmentStrategy(verbose = verbose, bucket = Money.of(500_000, "USD"))))
}

