package com.bartoszwesolowski.strategy

import com.bartoszwesolowski.model.PriceProvider
import nl.hiddewieringa.money.minus
import org.javamoney.moneta.Money
import javax.money.MonetaryAmount

class SizedBucketInvestmentStrategy(
    verbose: Boolean,
    private val bucket: MonetaryAmount
) : ManualInvestmentStrategy(verbose) {
    override fun buy(priceProvider: PriceProvider, value: MonetaryAmount) {
        var remainingValue = value
        while (remainingValue.isPositive) {
            val lastAsset = account.assets.lastOrNull()
            val lastAssetValue = lastAsset?.currentValue(priceProvider.getPrice())
                ?: Money.zero(value.currency)
            val valueToBuy: MonetaryAmount
            if (lastAssetValue >= bucket) {
                super.newAsset()
                valueToBuy = min(bucket, remainingValue)
            } else {
                valueToBuy = min(bucket - lastAssetValue, remainingValue)
            }
            super.buy(priceProvider, valueToBuy)
            remainingValue -= valueToBuy
        }
    }

    private fun min(a: MonetaryAmount, b: MonetaryAmount): MonetaryAmount {
        return if (a < b) a else b
    }
}