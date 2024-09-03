package com.bartoszwesolowski.strategy

import com.bartoszwesolowski.model.PriceProvider
import nl.hiddewieringa.money.minus
import nl.hiddewieringa.money.plus
import org.javamoney.moneta.Money
import javax.money.MonetaryAmount

open class ManualInvestmentStrategy(verbose: Boolean) : BaseInvestmentStrategy(verbose) {
    private var assetIndex = 1

    override fun buy(priceProvider: PriceProvider, value: MonetaryAmount) =
        account.buy(ASSET_NAME + assetIndex, priceProvider.getPrice(ASSET_NAME), value)

    override fun sellValue(
        priceProvider: PriceProvider,
        value: MonetaryAmount,
        tax: Double
    ): MonetaryAmount {
        var assetName = ASSET_NAME + assetIndex
        var remainingValue = value
        var taxValue: MonetaryAmount = Money.zero(value.currency)
        while (remainingValue.isPositive) {
            val currentPrice = priceProvider.getPrice(ASSET_NAME)
            val assetValue = account.currentAssetValue(assetName, currentPrice)
            if (assetValue >= remainingValue) {
                return account.sellValue(assetName, remainingValue, currentPrice, tax)
            } else {
                taxValue += account.sellValue(assetName, assetValue, currentPrice, tax)
                remainingValue -= assetValue
                assetIndex--
                assetName = ASSET_NAME + assetIndex
            }
        }
        return taxValue
    }

    override fun sellAfterTaxValue(
        priceProvider: PriceProvider,
        afterTaxValue: MonetaryAmount,
        tax: Double
    ): MonetaryAmount {
        var assetName = ASSET_NAME + assetIndex
        var remainingValueAfterTax = afterTaxValue
        var taxValue: MonetaryAmount = Money.zero(afterTaxValue.currency)
        while (remainingValueAfterTax.isPositive) {
            val currentPrice = priceProvider.getPrice(assetName)
            val assetValueAfterTax = account.currentAssetValueAfterTax(assetName, currentPrice, tax)
            if (assetValueAfterTax >= remainingValueAfterTax) {
                return account.sellAfterTaxValue(assetName, remainingValueAfterTax, currentPrice, tax)
            } else {
                taxValue += account.sellAfterTaxValue(assetName, assetValueAfterTax, currentPrice, tax)
                remainingValueAfterTax -= assetValueAfterTax
                assetIndex--
                assetName = ASSET_NAME + assetIndex
            }
        }
        return taxValue
    }

    fun newAsset() {
        assetIndex++
    }

    private companion object {
        const val ASSET_NAME = "ETF-"
    }
}