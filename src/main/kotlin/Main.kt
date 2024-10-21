package com.bartoszwesolowski

import com.bartoszwesolowski.model.SP500PriceProvider
import com.bartoszwesolowski.scenario.BuyEveryYear
import com.bartoszwesolowski.scenario.CompoundInvestmentScenario
import com.bartoszwesolowski.scenario.InvestmentState
import com.bartoszwesolowski.scenario.SellAfterTaxEveryYearUntilZero
import com.bartoszwesolowski.strategy.LifoInvestmentStrategy
import com.bartoszwesolowski.strategy.SimpleInvestmentStrategy
import com.bartoszwesolowski.strategy.SizedBucketInvestmentStrategy
import com.bartoszwesolowski.strategy.ValueRangeBucketInvestmentStrategy
import org.javamoney.moneta.format.CurrencyStyle
import java.util.Locale
import javax.money.format.AmountFormatQueryBuilder
import javax.money.format.MonetaryAmountFormat
import javax.money.format.MonetaryFormats

fun main() {
    val verbose = false
    val priceProvider = SP500PriceProvider(100.usd)
    val tax = 0.19
    val strategies = listOf(
        SimpleInvestmentStrategy(verbose = verbose),
        LifoInvestmentStrategy(verbose = verbose),
        SizedBucketInvestmentStrategy(verbose = verbose, bucket = 500_000.usd),
        ValueRangeBucketInvestmentStrategy(verbose = verbose, bucketSizePercent = 0.5),
    )
    val scenario = CompoundInvestmentScenario(
        listOf(
            BuyEveryYear(100_000.usd, 8, priceProvider, tax),
            SellAfterTaxEveryYearUntilZero(100_000.usd, priceProvider, tax),
        )
    )
    for (strategy in strategies) {
        println(strategy.javaClass.simpleName)
        println(scenario.simulate(strategy).last().toPrettyString())
    }
}

private val formatter: MonetaryAmountFormat = MonetaryFormats.getAmountFormat(
    AmountFormatQueryBuilder.of(Locale.US)
        .set(MonetaryFormats.getAmountFormat(Locale.US))
        .set(CurrencyStyle.SYMBOL)
        .build()
)

private fun InvestmentState.toPrettyString(): String {
    return "  withdrawnValue=${formatter.format(withdrawnValue)},\n" +
            "  withdrawnValueAfterTax=${formatter.format(withdrawnValueAfterTax)},\n" +
            "  taxValue=${formatter.format(taxValue)}"
}

