package com.bartoszwesolowski.strategy

import com.bartoszwesolowski.min
import nl.hiddewieringa.money.minus
import org.javamoney.moneta.Money
import javax.money.MonetaryAmount

class SizedBucketInvestmentStrategy(
    verbose: Boolean,
    private val bucket: MonetaryAmount
) : ManualInvestmentStrategy(verbose) {
    override fun buy(currentPrice: MonetaryAmount, value: MonetaryAmount) {
        var remainingValue = value
        while (remainingValue.isPositive) {
            val lastAsset = account.assets.lastOrNull()
            val lastAssetValue = lastAsset?.currentValue(currentPrice)
                ?: Money.zero(value.currency)
            val valueToBuy: MonetaryAmount
            if (lastAssetValue >= bucket) {
                super.newAsset()
                valueToBuy = min(bucket, remainingValue)
            } else {
                valueToBuy = min(bucket - lastAssetValue, remainingValue)
            }
            super.buy(currentPrice, valueToBuy)
            remainingValue -= valueToBuy
        }
    }
}