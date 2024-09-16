package com.bartoszwesolowski.model

import nl.hiddewieringa.money.div
import nl.hiddewieringa.money.plus
import org.javamoney.moneta.Money
import java.util.LinkedList
import javax.money.Monetary
import javax.money.MonetaryAmount

class Account(private val verbose: Boolean) {
    internal val assets = mutableListOf<Asset>()

    fun currentValue(priceProvider: PriceProvider): MonetaryAmount {
        if (assets.isEmpty()) return Money.zero(Monetary.getCurrency("USD"))
        return assets
            .map { it.currentValue(priceProvider.getPrice(it.name)) }
            .reduce { sum, value -> sum + value }
    }

    fun currentValueAfterTax(priceProvider: PriceProvider, tax: Double): MonetaryAmount {
        if (assets.isEmpty()) return Money.zero(Monetary.getCurrency("USD"))
        return assets
            .map { it.currentValueAfterTax(priceProvider.getPrice(it.name), tax) }
            .reduce { sum, value -> sum + value }
    }

    fun currentAssetValue(assetName: String, currentPrice: MonetaryAmount): MonetaryAmount =
        assets.first { it.name == assetName }.currentValue(currentPrice)

    fun currentAssetValueAfterTax(assetName: String, currentPrice: MonetaryAmount, tax: Double): MonetaryAmount =
        assets.first { it.name == assetName }.currentValueAfterTax(currentPrice, tax)

    fun buy(assetName: String, price: MonetaryAmount, value: MonetaryAmount) {
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
        return result
    }
}
