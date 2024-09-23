package com.bartoszwesolowski.model

import com.bartoszwesolowski.scenario.InvestmentState
import com.bartoszwesolowski.usd
import nl.hiddewieringa.money.div
import nl.hiddewieringa.money.plus
import java.util.LinkedList
import javax.money.Monetary
import javax.money.MonetaryAmount

class Account(private val verbose: Boolean) {
    internal val assets = mutableListOf<Asset>()
    private var investedValue: MonetaryAmount = 0.usd
    private var aggregatedSellResult: SellResult = SellResult.zero(Monetary.getCurrency("USD"))
    
    fun currentValue(priceProvider: PriceProvider): MonetaryAmount {
        if (assets.isEmpty()) return 0.usd
        return assets
            .map { it.currentValue(priceProvider.getPrice(it.name)) }
            .reduce { sum, value -> sum + value }
    }

    fun currentValueAfterTax(priceProvider: PriceProvider, tax: Double): MonetaryAmount {
        if (assets.isEmpty()) return 0.usd
        return assets
            .map { it.currentValueAfterTax(priceProvider.getPrice(it.name), tax) }
            .reduce { sum, value -> sum + value }
    }
    
    fun currentState(priceProvider: PriceProvider, tax: Double) = InvestmentState(
        investedValue = investedValue,
        value = currentValue(priceProvider),
        valueAfterTax = currentValueAfterTax(priceProvider, tax),
        withdrawnValue = aggregatedSellResult.value,
        withdrawnValueAfterTax = aggregatedSellResult.valueAfterTax,
        taxValue = aggregatedSellResult.taxValue
    )

    fun currentAssetValue(assetName: String, currentPrice: MonetaryAmount): MonetaryAmount =
        assets.first { it.name == assetName }.currentValue(currentPrice)

    fun currentAssetValueAfterTax(assetName: String, currentPrice: MonetaryAmount, tax: Double): MonetaryAmount =
        assets.first { it.name == assetName }.currentValueAfterTax(currentPrice, tax)

    fun buy(assetName: String, price: MonetaryAmount, value: MonetaryAmount) {
        investedValue += value
        val asset = assets.find { it.name == assetName }
        val units = (value / price.number).number.toDouble()
        if (verbose) println("Buying $units units of $assetName for the total value of $value for unit price of $price")
        if (asset != null) {
            asset.transactions.add(Transaction(price, units))
        } else {
            assets.add(Asset(assetName, LinkedList(listOf(Transaction(price, units)))))
        }
    }

    fun sell(
        assetName: String,
        value: MonetaryAmount,
        currentPrice: MonetaryAmount,
        tax: Double
    ): SellResult {
        val asset = assets.first { it.name == assetName }
        val result = asset.sell(value, currentPrice, tax)
        if (verbose) println("Selling $value of $assetName for unit price of $currentPrice, taxValue: $result")
        if (asset.transactions.isEmpty()) {
            assets.remove(asset)
        }
        aggregatedSellResult += result
        return result
    }

    fun sellAfterTax(
        assetName: String,
        valueAfterTax: MonetaryAmount,
        currentPrice: MonetaryAmount,
        tax: Double
    ): SellResult {
        val asset = assets.first { it.name == assetName }
        val result = asset.sellAfterTax(valueAfterTax, currentPrice, tax)
        if (verbose) println("Selling $valueAfterTax (after tax) of $assetName for unit price of $currentPrice, taxValue: $result")
        if (asset.transactions.isEmpty()) {
            assets.remove(asset)
        }
        aggregatedSellResult += result
        return result
    }
}
