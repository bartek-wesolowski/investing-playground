package com.bartoszwesolowski

import com.bartoszwesolowski.model.ExponentialYearlyPriceProvider
import com.bartoszwesolowski.scenario.BuyEveryYear
import com.bartoszwesolowski.scenario.CompoundInvestmentScenario
import com.bartoszwesolowski.scenario.SellAfterTaxEveryYearUntilZero
import com.bartoszwesolowski.strategy.BucketInvestmentStrategy
import com.bartoszwesolowski.strategy.LifoInvestmentStrategy
import com.bartoszwesolowski.strategy.SimpleInvestmentStrategy

fun main() {
    val verbose = false
    val priceProvider = ExponentialYearlyPriceProvider(100.usd, 0.05)
    val tax = 0.19
    val scenario = CompoundInvestmentScenario(
        listOf(
            BuyEveryYear(100_000.usd, 15, priceProvider, tax),
            SellAfterTaxEveryYearUntilZero(100_000.usd, priceProvider, tax),
        )
    )
    println(scenario.simulate(SimpleInvestmentStrategy(verbose = verbose)).last())
    println(scenario.simulate(LifoInvestmentStrategy(verbose = verbose)).last())
    println(scenario.simulate(BucketInvestmentStrategy(verbose = verbose, bucket = 500_000.usd)).last())
}

