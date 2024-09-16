package com.bartoszwesolowski.strategy

import com.bartoszwesolowski.model.SellResult
import com.bartoszwesolowski.model.PriceProvider
import nl.hiddewieringa.money.minus
import nl.hiddewieringa.money.plus
import org.javamoney.moneta.Money
import javax.money.MonetaryAmount

open class ManualInvestmentStrategy(verbose: Boolean) : BaseInvestmentStrategy(verbose) {
    private var assetIndex = 1

    override fun buy(priceProvider: PriceProvider, value: MonetaryAmount) =
        account.buy(ASSET_NAME + assetIndex, priceProvider.getPrice(ASSET_NAME), value)

    override fun sell(
        priceProvider: PriceProvider,
        value: MonetaryAmount,
        tax: Double
    ): SellResult {
        var assetName = ASSET_NAME + assetIndex
        var remainingValue = value
        var sellResult = SellResult.zero(value.currency)
        while (remainingValue.isPositive) {
            val currentPrice = priceProvider.getPrice(ASSET_NAME)
            val assetValue = account.currentAssetValue(assetName, currentPrice)
            if (assetValue >= remainingValue) {
                sellResult += account.sell(assetName, remainingValue, currentPrice, tax)
                return sellResult
            } else {
                sellResult += account.sell(assetName, assetValue, currentPrice, tax)
                remainingValue -= assetValue
                assetIndex--
                assetName = ASSET_NAME + assetIndex
            }
        }
        return sellResult
    }

    override fun sellAfterTax(
        priceProvider: PriceProvider,
        afterTaxValue: MonetaryAmount,
        tax: Double
    ): SellResult {
        var assetName = ASSET_NAME + assetIndex
        var remainingValueAfterTax = afterTaxValue
        var sellResult = SellResult.zero(afterTaxValue.currency)
        while (remainingValueAfterTax.isPositive) {
            val currentPrice = priceProvider.getPrice(assetName)
            val assetValueAfterTax = account.currentAssetValueAfterTax(assetName, currentPrice, tax)
            if (assetValueAfterTax > remainingValueAfterTax) {
                sellResult += account.sellAfterTax(assetName, remainingValueAfterTax, currentPrice, tax)
                remainingValueAfterTax = Money.zero(afterTaxValue.currency)
            } else {
                sellResult += account.sellAfterTax(assetName, assetValueAfterTax, currentPrice, tax)
                remainingValueAfterTax -= assetValueAfterTax
                if (assetIndex > 1) {
                    assetIndex--
                    assetName = ASSET_NAME + assetIndex
                }
            }
        }
        return sellResult
    }

    fun newAsset() {
        assetIndex++
    }

    private companion object {
        const val ASSET_NAME = "ETF-"
    }
}