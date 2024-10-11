package com.bartoszwesolowski.strategy

import com.bartoszwesolowski.model.SellResult
import nl.hiddewieringa.money.minus
import org.javamoney.moneta.Money
import javax.money.MonetaryAmount

class ValueRangeBucketInvestmentStrategy(
    verbose: Boolean,
    private val bucketSizePercent: Double,
) : BaseInvestmentStrategy(verbose) {
    private var firstBuyPrice: MonetaryAmount? = null
    private var boughtAssetIndices = sortedSetOf<Int>()

    override fun buy(currentPrice: MonetaryAmount, value: MonetaryAmount) {
        val assetIndex = if (firstBuyPrice != null) {
            val priceDiff = currentPrice - firstBuyPrice!!
            val priceDiffPercent = priceDiff.number.toDouble() / firstBuyPrice!!.number.toDouble()
            (priceDiffPercent / bucketSizePercent).toInt()
        } else {
            firstBuyPrice = currentPrice
            0
        }
        boughtAssetIndices.add(assetIndex)
        account.buy("ETF-$assetIndex", currentPrice, value)
    }

    override fun sell(currentPrice: MonetaryAmount, value: MonetaryAmount, tax: Double): SellResult {
        var assetName = "ETF-" + boughtAssetIndices.last()
        var remainingValue = value
        var sellResult = SellResult.zero(value.currency)
        while (remainingValue.isPositive) {
            val assetValue = account.currentAssetValue(assetName, currentPrice)
            if (assetValue >= remainingValue) {
                sellResult += account.sell(assetName, remainingValue, currentPrice, tax)
                return sellResult
            } else {
                sellResult += account.sell(assetName, assetValue, currentPrice, tax)
                remainingValue -= assetValue
                boughtAssetIndices.removeLast()
                if (boughtAssetIndices.isNotEmpty()) {
                    assetName = "ETF-" + boughtAssetIndices.last()
                }
            }
        }
        return sellResult
    }

    override fun sellAfterTax(currentPrice: MonetaryAmount, afterTaxValue: MonetaryAmount, tax: Double): SellResult {
        var assetName = "ETF-" + boughtAssetIndices.last()
        var remainingValueAfterTax = afterTaxValue
        var sellResult = SellResult.zero(afterTaxValue.currency)
        while (remainingValueAfterTax.isPositive) {
            val assetValueAfterTax = account.currentAssetValueAfterTax(assetName, currentPrice, tax)
            if (assetValueAfterTax > remainingValueAfterTax) {
                sellResult += account.sellAfterTax(assetName, remainingValueAfterTax, currentPrice, tax)
                remainingValueAfterTax = Money.zero(afterTaxValue.currency)
            } else {
                sellResult += account.sellAfterTax(assetName, assetValueAfterTax, currentPrice, tax)
                remainingValueAfterTax -= assetValueAfterTax
                boughtAssetIndices.removeLast()
                if (boughtAssetIndices.isNotEmpty()) {
                    assetName = "ETF-" + boughtAssetIndices.last()
                }
            }
        }
        return sellResult
    }
}